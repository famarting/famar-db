package com.famar.searchdb.rest;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

public class FDBRestfulApiServer {

	public FDBRestfulApiServer() {
		JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
//		factoryBean.setResourceClasses(CourseRepository.class);
		factory.setResourceProvider(new SingletonResourceProvider(new FDBDocumentServiceRestful()));
		factory.setAddress("http://localhost:30987/");
		//providers
		factory.setProvider(new FDBRestfulJsonProvider());
		factory.setProvider(new FDBRestfulExceptionMapper());
		
		
//		Server server = 
		factory.create();
	}
	
}
