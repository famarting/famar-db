package com.famar.searchdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App {
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {

//		FamarDB db = new FamarDB();
//		FDBDocumentService documentService = db.getDocumentService();
//		
//		Map<String,Object> doc = new HashMap<>();
//		doc.put("name", "fabian");
//		doc.put("surname", "martinez");
//		String uuid = documentService.save("people", doc);
//		
//		Optional<FamarDBDocument> fabian = documentService.get("people", uuid);
//		System.out.println(new String(fabian.orElseThrow(FDBException::new).getDocument()));
//		
//		byte[] bdoc = new ObjectMapper().writeValueAsBytes(doc);
//		
//		String uuidbdoc = documentService.save("people", bdoc);
////		
//		Optional<FamarDBDocument> optbdoc = documentService.get("people", uuidbdoc);
//		System.out.println(new String(optbdoc.orElseThrow(FDBException::new).getDocument()));
		
//		FDBDocumentCollectionService db = new FDBDocumentCollectionService("test");
//
//		// Map<String,String> doc = new HashMap<>();
//		// doc.put("name", "fabian");
//		// doc.put("surname", "martinez");
//		// db.save(doc);
//		// db.search("name:fabian").forEach(System.out::println);;
//
//		Map<String, String> doc = new HashMap<>();
//		doc.put("name", "fabian");
//		doc.put("surname", "martinez");
//		String uuid = db.saveV2(doc);
//
//		System.out.println(new String(db.get(uuid)));
//		
//		db.searchV2("name:fabian").forEach(json -> System.out.println(new String(json)));

	}
}
