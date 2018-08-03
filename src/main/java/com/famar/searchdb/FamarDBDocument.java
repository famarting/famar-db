package com.famar.searchdb;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_EMPTY)
public class FamarDBDocument {

	private String uuid;
	private Map<String, Object> indexedFieds;
	private Object document;
	@JsonIgnore
	private byte[] source;
	

//	public FamarDBDocument(String uuid, byte[] document, Map<String, Object> documentAsMap) {
//		super();
//		this.uuid = uuid;
//		this.document = document;
//		this.documentAsMap = documentAsMap;
//		try {
//			this.documentAsObject = new ObjectMapper().readTree(document);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public FamarDBDocument(String uuid, byte[] source, Map<String, Object> indexedFieds) {
		super();
		this.uuid = uuid;
		this.source = source;
		this.indexedFieds = indexedFieds;
		try {
			this.document = new ObjectMapper().readTree(source);
		} catch (IOException e) {
			throw new FDBException(e);
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, Object> getIndexedFieds() {
		return indexedFieds;
	}

	public void setIndexedFieds(Map<String, Object> indexedFieds) {
		this.indexedFieds = indexedFieds;
	}

	public Object getDocument() {
		return document;
	}

	public void setDocument(Object document) {
		this.document = document;
	}

	public byte[] getSource() {
		return source;
	}

	public void setSource(byte[] source) {
		this.source = source;
	}
	
}
