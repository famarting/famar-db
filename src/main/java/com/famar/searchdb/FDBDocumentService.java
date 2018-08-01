package com.famar.searchdb;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDBDocumentService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IndexWriterConfig config;
	private Directory directory;
	private Analyzer analyzer;
	
	public FDBDocumentService() {
        try {
			this.directory = new SimpleFSDirectory(Paths.get("/var/data/famardb"));
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		} //new 
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
        	IndexWriter writer = new IndexWriter(directory, config);
			Long id = writer.addDocument(doc);
			writer.close();
			return id;
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		
	}

	private void addDocumentField(Document doc, Entry<String, String> entry) {
		doc.add(new StringField(entry.getKey(), entry.getValue(), Store.YES));
	}
	
	public List<Map<String,String>> search(String querystr) {
		
		Query q;
		try {
			q = new QueryParser("", analyzer).parse(querystr);
		} catch (ParseException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
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
	
}
