package com.famar.searchdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.famar.searchdb.visitors.FetchSourceFieldsVisitor;

public class FDBDocumentService{

//	private static final String DATA_PATH = "/var/data/famardb";
	private static final String DATA_PATH = "/home/famartinez/software/senseJuanzu/github/data";


	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IndexWriterConfig config;
	private FSDirectory directory;
	private Analyzer analyzer;
	
	public FDBDocumentService() {
        try {
			Path location = Paths.get(DATA_PATH);
			LockFactory lockFactory = SimpleFSLockFactory.INSTANCE;
			
            this.directory = FSDirectory.open(location, lockFactory); // use lucene defaults

		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
        this.analyzer = new StandardAnalyzer();
        this.config = new IndexWriterConfig(analyzer);
	}
	
	public Long save(Map<String,String> simpleDoc) {
		
		Document doc = new Document();
		Optional.ofNullable(simpleDoc)
		.orElseThrow(FDBException::new)
		.entrySet()
		.stream()
		.forEach(entry -> addDocumentField(doc, entry));
		
        try {
        	IndexWriter writer = createWriterRetrying();
			Long id = writer.addDocument(doc);
			writer.close();
			return id;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		
	}

	public String saveV2(Map<String,String> simpleDoc) {
		Document doc = new Document();
		
		Optional.ofNullable(simpleDoc)
		.orElseThrow(FDBException::new)
		.entrySet()
		.stream()
		.forEach(entry -> addDocumentField(doc, entry));
		
		try {
//			doc.add(new SortedDocValuesField(FamarDBConstants.FIELDNAME_SOURCE, new BytesRef(new ObjectMapper().writeValueAsBytes(simpleDoc))));
			doc.add(new StoredField(FamarDBConstants.FIELDNAME_SOURCE, new ObjectMapper().writeValueAsBytes(simpleDoc)));
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		
		String uuid = UUID.randomUUID().toString();
		doc.add(new StringField(FamarDBConstants.FAMARDB_UUID, uuid, Store.YES));
		
        try {
        	IndexWriter writer = createWriterRetrying();
			writer.addDocument(doc);
			writer.close();
			return uuid;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
	}
	
	private IndexWriter createWriterRetrying() throws IOException {
		try {
			return createIndexWriter();			
		}catch(LockObtainFailedException lofe) {
			logger.info("",lofe);
			Files.delete(this.directory.getDirectory().resolve(IndexWriter.WRITE_LOCK_NAME));
			return createIndexWriter();			
		}
	}

	private IndexWriter createIndexWriter() throws IOException {
		return new IndexWriter(directory, config);
	}

	private void addDocumentField(Document doc, Entry<String, String> entry) {
		doc.add(new StringField(entry.getKey(), entry.getValue(), Store.YES));
	}
	
	public List<Map<String,String>> search(String querystr) {
		
		Query q = parseQueryString(querystr);
		int hitsPerPage = 10;
		try {
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(q, hitsPerPage);
			
			List<Map<String,String>> result = new ArrayList<>();
			for(ScoreDoc sc : docs.scoreDocs) {
			    Document doc = searcher.doc(sc.doc);
			    Map<String,String> simpleDoc = new HashMap<>();
			    for(IndexableField field : doc.getFields()) {
			    	simpleDoc.put(field.name(), field.stringValue());
			    }
			    result.add(simpleDoc);
			}
			reader.close();
			return result;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		
	}
	
	public List<byte[]> searchV2(String querystr) {
		
		Query q = parseQueryString(querystr);
		int hitsPerPage = 10;
		IndexReader reader = getIndexReader();
		try {
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(q, hitsPerPage);
			
			List<byte[]> result = new ArrayList<>();
			FetchSourceFieldsVisitor visitor = new FetchSourceFieldsVisitor();
			for(ScoreDoc sc : docs.scoreDocs) {
				visitor.reset();
			    searcher.doc(sc.doc, visitor);
			    result.add(visitor.source().bytes);
			}
			return result;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		} finally {
			closeReader(reader);
		}
	}

	private void searchStrategy(Query query, int pageSize, Consumer<TopDocs> logic) {
		IndexReader reader = getIndexReader();
		IndexSearcher searcher = new IndexSearcher(reader);
		try {
			TopDocs docs = searcher.search(query, pageSize);
			logic.accept(docs);
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		} finally {
			closeReader(reader);
		}
	}
	
	public byte[] get(String uuid) {
		Query q = new TermQuery(new Term(FamarDBConstants.FAMARDB_UUID, uuid));
		IndexReader reader = getIndexReader();
		try {
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(q, 1);
			FetchSourceFieldsVisitor visitor = new FetchSourceFieldsVisitor();
			ScoreDoc doc = Stream.of(docs.scoreDocs).findFirst().orElse(null);
			if(doc!=null) {
				searcher.doc(doc.doc, visitor);
			    return visitor.source().bytes;
			}
			return null;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		} finally {
			closeReader(reader);
		}
	}

	private void closeReader(IndexReader reader) {
		try {
			reader.close();
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
	}

	private IndexReader getIndexReader() {
		IndexReader reader;
		try {
			reader = DirectoryReader.open(directory);
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		return reader;
	}

	private Query parseQueryString(String querystr) {
		Query q;
		try {
			q = new QueryParser("", analyzer).parse(querystr);
		} catch (ParseException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		return q;
	}
	
}
