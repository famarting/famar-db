package com.famar.searchdb.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
	
	public FDBCollectionSettings getCollectionByName(String collection) {
		
//		MessageDigest digest;
//		try {
//			digest = MessageDigest.getInstance("SHA-256");
//		} catch (NoSuchAlgorithmException e) {
//			logger.error("",e);
//			throw new FDBException(e);
//		}
//		byte[] hash = digest.digest(collection.getBytes(StandardCharsets.UTF_8));
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
	
}
