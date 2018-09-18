package com.famar.searchdb;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FDBDocumentService {

	private Map<String, FDBDocumentCollectionService> collections;
	
	private FDBDocumentService() {
		this.collections = new ConcurrentHashMap<>(2);
	}
	 
	private static FDBDocumentService instance;
	
	public static FDBDocumentService getInstance() {
		if(instance==null) {
			instance = new FDBDocumentService();
		}
		return instance;
	}
	
//	public String save(String collection, Map<String, Object> json) {
//		return getCollection(collection).saveV2(json);
//	}
	
	public String save(String collection, byte[] json) {
		return getCollection(collection).save(json);
	}
	
	public void update(String collection, String uuid, byte[] json) {
		getCollection(collection).update(uuid, json);
	}
	
	public void delete(String collection, String uuid) {
		getCollection(collection).delete(uuid);
	}
	
	public Optional<FamarDBDocument> get(String collection, String uuid) {
		return Optional.ofNullable(getCollection(collection).get(uuid));
	}
	
	public Collection<FamarDBDocument> search(String collection, String queryString){
		return getCollection(collection).search(queryString);
	}
	
	private FDBDocumentCollectionService getCollection(String collection) {
		return collections.computeIfAbsent(collection, FDBDocumentCollectionService::new);
	}
	
}
