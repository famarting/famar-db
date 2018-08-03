package com.famar.searchdb;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
	
	public static void main(String[] args) {
		System.out.println("Welcome to famar-db!");

		FDBDocumentService db = new FDBDocumentService("test");

		// Map<String,String> doc = new HashMap<>();
		// doc.put("name", "fabian");
		// doc.put("surname", "martinez");
		// db.save(doc);
		// db.search("name:fabian").forEach(System.out::println);;

		Map<String, String> doc = new HashMap<>();
		doc.put("name", "fabian");
		doc.put("surname", "martinez");
		String uuid = db.saveV2(doc);

		System.out.println(new String(db.get(uuid)));
		
		db.searchV2("name:fabian").forEach(json -> System.out.println(new String(json)));

	}
}
