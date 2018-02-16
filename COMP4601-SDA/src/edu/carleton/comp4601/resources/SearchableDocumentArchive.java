package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.carleton.comp4601.dao.*;
import edu.carleton.comp4601.resources.*;

import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Path("/sda")
public class SearchableDocumentArchive {
	//Default things we assume we need...
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private String name;
 
	
	DocumentCollection documents;
	
	//Constructor
	public SearchableDocumentArchive(){
		name = "COMP4601 Searchable Document Archive V2.1: Luke Daschko and Mat Schmid";
		documents = DocumentCollection.getInstance();
	}
	
	//Testing Functions
	@GET
	public String printName(){
		return name;
	}
	
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXML(){
		return "<?xml version=\"1.0\"?>" + "<sda> " + name + " </sda>";
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml(){
		return "<html> " + "<title>" + name + "</title>" + "<body><h1>" + name
				+ "</body></h1>" + "</html> ";
	
	}
	
	@GET
	@Path("documents")
	@Produces(MediaType.TEXT_PLAIN)  //remove if returning array
	public String getDocuments() {
		System.out.println("Consolue Test = Success");
		return "This is the TEST for getDocuments()";
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeDocument(edu.carleton.comp4601.dao.Document newDoc) {
		System.out.println("HIT PUT");
		//edu.carleton.comp4601.dao.Document newDoc = d.getValue();
		
		documents.add(newDoc);
		
		Response res = Response.ok().build();
		return res;
	}
	
	@Path("{doc}")
	public DocAction getDocument(@PathParam("doc") Integer id) {
		return new DocAction(uriInfo, request, id);
	}
	
	@Path("search")
	public SearchTagAction searchTaggedDocuments(){
		return new SearchTagAction(uriInfo, request);
	}
	
	@Path("delete")
	public DeleteTagAction deleteTaggedDocuments(){
		return new DeleteTagAction(uriInfo, request);
	}
}
