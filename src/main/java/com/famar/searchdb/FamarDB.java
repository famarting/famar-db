package com.famar.searchdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.famar.searchdb.rest.FDBRestfulApiServer;

/**
 * Entry point class for start up the database 
 * 
 * @author famartinez
 *
 */
public class FamarDB {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private FDBDocumentService documentService = FDBDocumentService.getInstance();
	
	public FamarDB() {
		System.out.println("Welcome to famar-db!");
		init();
	}
	
	private void init() {
		//ensure required directories exists
		try {
			Files.createDirectories(Paths.get(FamarDBConstants.ADMIN_PATH));
			Files.createDirectories(Paths.get(FamarDBConstants.DATA_PATH));
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
		//init rest api
		new FDBRestfulApiServer();
	}
	
	public FDBDocumentService getDocumentService() {
		return documentService;
	}

	public static void main(String... args) {
		new FamarDB();
	}
	
}
