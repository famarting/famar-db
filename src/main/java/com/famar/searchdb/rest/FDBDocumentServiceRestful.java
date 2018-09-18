package com.famar.searchdb.rest;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.famar.searchdb.FDBDocumentService;
import com.famar.searchdb.FDBException;

@Path("document")
@Produces(MediaType.APPLICATION_JSON)
public class FDBDocumentServiceRestful {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private FDBDocumentService documentService = FDBDocumentService.getInstance();
	
	@POST
	@Path("{collection}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@PathParam("collection") String collection, InputStream jsonDocument) {
		byte[] json;
		try {
			json = IOUtils.readBytesFromStream(jsonDocument);
		} catch (IOException e) {
			logger.error("",e);
			throw new FDBException(e);
		}
	    return Response.ok(new FDBSaveDocumentResponse(documentService.save(collection, json))).build();
	}
	
	@GET
	@Path("{collection}/{uuid}")
	public Response get(@PathParam("collection") String collection, @PathParam("uuid") String uuid) {
	    return Response.ok(documentService.get(collection, uuid).orElseThrow(FDBDocumentNotFoundException::new)).build();
	}
	
	@GET
	@Path("{collection}")
	public Response search(@PathParam("collection") String collection, @QueryParam("q") String queryString) {
	    return Response.ok(documentService.search(collection, queryString)).build();
	}
	
}
