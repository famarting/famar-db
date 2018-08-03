package com.famar.searchdb.admin;

import java.io.Serializable;

public class FDBCollectionDefinition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8452124026255477665L;
	
	private String collection;
	private String uuid;
	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
