package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import model.Document;

public class ClientOne {
	private static String REST = "rest";
	private static String SDA = "sda";
	private static String DOCUMENTS = "documents";
	private static String COUNT = "count";
	private static String BASE_URL = "http://localhost:8080/COMP4601-SDA";

	private static URI getBaseURI() { return UriBuilder.fromUri(BASE_URL).build();}
	private String prompt(WebResource service) { return service.path(REST).path(SDA).accept(MediaType.TEXT_PLAIN).get(String.class);}
	
	
	private void run(){
		
		//Some default Client stuff we need to step up...
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		String input = "";
		
		//use a web service to get the prompt
		String prompt = prompt(service) + "> ";
		
		//The run loop
		while (true) {
			System.out.print(prompt);
			try {
				input = in.readLine();
				if (input.length() == 0) {
					System.out.println(prompt+": Have a good day!");
					break;
				}
				Scanner s = new Scanner(input);
				String cmd = s.next();
				System.out.println("Response: " + process(cmd, service, s));
			} 
			catch (Exception allE) 
			{
				allE.printStackTrace();
			}
		}
	}
	
	private String process(String cmd, WebResource service, Scanner s){
		int documentID;
		
		if (cmd.equalsIgnoreCase("newdoc")) {
			
			//Order for user input: ID NAME SCORE URL TEXT
			// newdoc 3 ClientName3 33 www.cilnet3.com Text3Client
			
			//get user input to populate rest of Document varaibles
			Document newDocument = new Document(s.nextInt());
			newDocument.setName(s.next());
			newDocument.setScore(s.nextDouble());
			newDocument.setUrl(s.next());
			newDocument.setText(s.next());
			
			
			//Add defult arrays
			ArrayList<String> links = new ArrayList<String>();
			links.add("Link1");
			links.add("Link2");
			links.add("Link3");
			newDocument.setLinks(links);
			
			ArrayList<String> tags = new ArrayList<String>();
			tags.add("Tag1");
			tags.add("Tag2");
			tags.add("Tag3");
			newDocument.setTags(tags);
			
			try {
				String input = newDocument.jsonify().toString();
				
				//Send it
				ClientResponse response = service.path(REST).path(SDA)
						.type("application/json")
						.post(ClientResponse.class, input);
				return response.toString();
			} 
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}	
		else if (cmd.equalsIgnoreCase("deldoc")) {
			documentID = s.nextInt();

			//Remove it
			ClientResponse response = service.path(REST).path(SDA + "/" + documentID).delete(ClientResponse.class);
			return response.toString();
		}
		else if (cmd.equalsIgnoreCase("crawl")) {
			String responseC = service.path(REST).path(SDA).path("crawl").accept(MediaType.APPLICATION_JSON).get(String.class);
			return responseC;
			
		}
		else if (cmd.equalsIgnoreCase("searchtag")){
			String tags = s.next();
			
			String responseT = service.path(REST).path(SDA).path("search").path(tags).accept(MediaType.APPLICATION_JSON).get(String.class);
			
			return responseT;
		}
		else if (cmd.equalsIgnoreCase("deletetag")){
			String tags = s.next();
			
			String responseT = service.path(REST).path(SDA).path("delete").path(tags).get(String.class);
			
			return responseT;
		}
		return "Uknown command";
	}
	
	public static void main(String[] args){
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				new ClientOne().run();
				return;
			}
		}
		
		new ClientOne().run();
	}

}
