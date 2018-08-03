package com.famar.searchdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.IndexableField;

public class FDBLuceneDocument implements Iterable<IndexableField> {

	private final List<IndexableField> fields;

	public FDBLuceneDocument() {
		this.fields = new ArrayList<>();
	}
	
	public FDBLuceneDocument(List<IndexableField> fields) {
		this.fields = fields;
	}

	@Override
	public Iterator<IndexableField> iterator() {
		return fields.iterator();
	}

	public final void add(IndexableField field) {
		fields.add(field);
	}

}
