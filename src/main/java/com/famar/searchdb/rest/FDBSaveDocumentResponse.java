package com.famar.searchdb.rest;

public class FDBSaveDocumentResponse {

	private String uuid;

	public FDBSaveDocumentResponse() {
		super();
	}
	
	public FDBSaveDocumentResponse(String uuid) {
		super();
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
