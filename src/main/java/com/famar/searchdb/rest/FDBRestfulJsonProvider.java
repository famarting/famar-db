package com.famar.searchdb.rest;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class FDBRestfulJsonProvider extends JacksonJsonProvider{

	private ObjectMapper mapper;
	
	public FDBRestfulJsonProvider() {
		super();
		overrideMapper();
    }

	public FDBRestfulJsonProvider(ObjectMapper mapper) {
		super(mapper);
		overrideMapper();
	}

	private void overrideMapper() {
		this.mapper = new ObjectMapper();
		this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        setMapper(this.mapper);
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
	
}

