package com.famar.searchdb.rest;

import java.io.EOFException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FDBRestfulExceptionMapper implements ExceptionMapper<Exception>{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Response toResponse(Exception exception) {
		logger.error("",exception);
		if(exception instanceof WebApplicationException) {
			return ((WebApplicationException)exception).getResponse();
		}
		if(exception instanceof EOFException) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.build();
	}

}
