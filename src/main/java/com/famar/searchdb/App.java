package com.famar.searchdb;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        FDBDocumentService db = new FDBDocumentService();
        
        Map<String,String> doc = new HashMap<>();
        doc.put("name", "fabian");
        doc.put("surname", "martinez");
        db.save(doc);
        
        db.search("name:fabian").forEach(System.out::println);;
    }
}
