package com.famar.searchdb.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.famar.searchdb.FDBException;
import com.famar.searchdb.FamarDBConstants;

/**
 * 
 *  
 *  
 *  
 
  <!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>

  
  
 * 
 * 
 * @author Fabian
 *
 */

public class FDBCollectionsManagerService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static FDBCollectionsManagerService instance = null;
	
	public static FDBCollectionsManagerService getInstance() {
		if(instance == null) {
			instance = new FDBCollectionsManagerService();
		}
		return instance;
	}
	
	private volatile Map<String, FDBCollectionSettings> collections;
	
	public FDBCollectionsManagerService() {
		this.collections = new ConcurrentHashMap<>(2);
	}

	public FDBCollectionSettings getCollectionByName(String collection) {
		return collections.computeIfAbsent(collection, this::loadCollection);
	}
	
	private FDBCollectionSettings loadCollection(String collection) {
		
		Path collectionDefinitionFile = Paths.get(FamarDBConstants.ADMIN_PATH+"/"+collection+".fdb");
		if(Files.exists(collectionDefinitionFile)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				Files.copy(collectionDefinitionFile, out);
			} catch (IOException e) {
				logger.error("",e);
				throw new FDBException(e);
			}
			try {
				return new ObjectMapper().readValue(out.toByteArray(), FDBCollectionSettings.class);
			} catch (IOException e) {
				logger.error("",e);
				throw new FDBException(e);
			}
		} else {
			FDBCollectionSettings settings = new FDBCollectionSettings();
			//TODO
			settings.setUuid(UUID.randomUUID().toString());
			try {
				File collectionFile = collectionDefinitionFile.toFile();
				collectionFile.createNewFile();
				new ObjectMapper().writeValue(collectionFile, settings);
			} catch (IOException e) {
				logger.error("",e);
				throw new FDBException(e);
			}
			return settings;
		}
	}
	
	public Path getCollectionLocation(String collection) {
    	FDBCollectionSettings collectionSettings = getCollectionByName(collection);
    	return Paths.get(FamarDBConstants.DATA_PATH+"/"+collectionSettings.getUuid());
	}
	
}
