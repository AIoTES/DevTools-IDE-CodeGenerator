/*******************************************************************************
 * Copyright 2018 Universidad Politécnica de Madrid UPM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package es.upm.tfo.lst.codegenerator.plugin.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * @author amedrano
 *
 */
public class GenerateServlet extends HttpServlet {
	private static final String ONT = "ontologies";
	private static final String TEMPLATE = "template";
	private static final String VAR = "variables";
	private static final String CONTENT_TYPE = "Content-Type";
	private File tempFolder;
	private String outputAlias ;
	private final String HTMLtemplate = "listing.htm.vm";
	private String out;

	private String token=null;
	private JsonObject srver_response;
	private boolean is_token_valid=false;
	private JsonParser jp;
	private JsonObject server_response;
	private String req_url="";

	boolean isAuthorized=false;

	public GenerateServlet() {
		jp = new JsonParser();
	}

	private static final long serialVersionUID = 1L;
	//{"template": "http://localhost/template/","ontologies":[{"url":"https://protege.stanford.edu/ontologies/pizza/pizza.owl", "recursive":"true"}], "variables":{"varname":"varvalue"}}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		addCorsHeaderPOST(resp);
		TemplateDataModel model = null;
		XmlParser parser = new XmlParser();
		OntologyLoader ontologyLoader = new OntologyLoader();
		if(req.getHeader(CONTENT_TYPE).equals("application/json")) {
			try {
				JsonElement sreq = jp.parse(req.getReader());
				if (sreq instanceof JsonObject) {
					JsonObject gc = (JsonObject) sreq;
					model = parser.generateXMLCoordinator(gc.get(TEMPLATE).getAsString());
					GenerateProject gp = new GenerateProject();
					gp.setMainModel(model);
					// set ontologies
					if (gc.get(ONT).isJsonArray() && gc.get(ONT).getAsJsonArray().size() > 0) {
						for (JsonElement item : gc.get(ONT).getAsJsonArray()) {
							if(item.isJsonPrimitive()) {
								gp.addOntology(	ontologyLoader.loadOntology(item.getAsJsonPrimitive().getAsString()),false);
							}
							if(item.isJsonObject()) {
								gp.addOntology(ontologyLoader.loadOntology(item.getAsJsonObject().get("url").getAsJsonPrimitive().getAsString()),item.getAsJsonObject().get("recursive").getAsBoolean());
							}
						}
					} else {
						if (gc.get(ONT).isJsonPrimitive()) {
							// string (single ont without recursive)
							gp.addOntology(ontologyLoader.loadOntology(gc.get(ONT).getAsJsonPrimitive().getAsString()), false);
						} else {
							// object (single ont with recursive parameter)
							JsonObject ont = gc.get(ONT).getAsJsonObject();
							gp.addOntology(ontologyLoader.loadOntology(ont.get("url").getAsJsonPrimitive().getAsString()),ont.get("recursive").getAsBoolean());
						}
					}
					// set variables
					if(gc.has(VAR)) {
						for (Map.Entry<String, JsonElement> varEntry : gc.get(VAR).getAsJsonObject().entrySet()) {
							gp.setVariable(varEntry.getKey(), varEntry.getValue().getAsString());
						}
					}
					// set Output
					this.out = Integer.toHexString(sreq.hashCode());
					File outFile = new File(tempFolder, this.out);
					this.deleteFolder(outFile);
					outFile.mkdirs();
					gp.setOutputFolder(outFile.getAbsolutePath() + File.separatorChar);
					//System.out.println(outFile.getAbsolutePath());
					// generate
					gp.process();
					JsonObject outO = new JsonObject();
					outO.addProperty("output", outputAlias+"/");
					resp.addHeader(CONTENT_TYPE, "application/json");
					System.out.println("response JSON "+outO);
					resp.getWriter().write(outO.toString());
					//resp.sendRedirect(this.SERVER+"GenerateCode");
				}else
					resp.sendError(400,"invalid json root element");
			}catch (Exception e) {
				resp.sendError(400,e.getLocalizedMessage());
			}
		}else 
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String auth ="https://activage-test1.lst.tfo.upm.es:8081/auth/realms/activage/protocol/openid-connect/auth?client_id=account&redirect_uri=https%3A%2F%2Factivage-test1.lst.tfo.upm.es%3A8081%2Fauth%2Frealms%2Factivage%2Faccount%2Flogin-redirect&state=0%2Fedafbc90-7ee2-466a-b02e-1ee24428db50&response_type=code&scope=openid";
		String redirect_url=buildRedirectURL(auth, req.getRequestURL().toString());
		String line, req_data;
		URL urlToFile;
		addCorsHeaderPOST(resp);
		if(req.getRequestURI().equals(outputAlias+"/ui")) {

			if(req.getHeader("Authorization") == null) {
				resp.sendRedirect(redirect_url);
			}else{
				resp.getWriter().write(this.generateWebInterface());
	
			}

		}else if(req.getRequestURI().equals(outputAlias+"/swagger")){
			if(req.getHeader("Authorization")==null) {
				resp.sendRedirect(redirect_url);
			}else {
				InputStream i = getClass().getClassLoader().getResource("swagger.yaml").openStream();
				String yaml = "";
				Scanner s = new Scanner(i);
				s.useDelimiter("\\A");
				yaml = s.hasNext() ? s.next() : "";
				s.close();
				resp.getWriter().write(yaml);
				resp.setContentType("text/plain");
			}

				
		}else {
			req_data = req.getRequestURI().replaceFirst(outputAlias, "");
			urlToFile = this.getServletContext().getResource("/"+this.out+req_data);
			try {
				File t = new File(urlToFile.getFile());
				if (t.isFile()) {
					// request a file
					BufferedReader br = new BufferedReader(new FileReader(t));
					StringBuilder sb = new StringBuilder();

					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					br.close();
					resp.getWriter().write(sb.toString());
					resp.setContentType("text/plain");
				}else if (t.isDirectory() && !t.equals(tempFolder)) {
					// request listing of a directory (not the root)
					BufferedReader br = null;
					br = new BufferedReader(new InputStreamReader(
							GenerateServlet.class.getClassLoader().getResourceAsStream(HTMLtemplate)));
					StringBuilder sb = new StringBuilder();

					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
					StringReader reader = new StringReader(sb.toString());
					StringWriter stringWriter = new StringWriter();
					Template template = new Template();
					template.setRuntimeServices(runtimeServices);
					VelocityContext context = new VelocityContext();
					context.put("path", t.getAbsolutePath());
					context.put("file", t);
					context.put("dirContent", t.listFiles());
					context.put("hash", this.out);
					context.put("BACK", req_data.split("/").length > 3);
					SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
					context.put("dateformatter",df2);
			        
					template.setData(runtimeServices.parse(reader, HTMLtemplate));
					template.initDocument();
					template.merge(context, stringWriter);
					resp.getWriter().write(stringWriter.toString());
					resp.setContentType("text/html");
					stringWriter.close();

				}
			} catch (Exception e) {
						System.out.println(e.getMessage());
				}
			
		}

	}

	
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doOptions(req, resp);
		addCorsHeader(resp);
	}

	public void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public void setOutputDir(File outputDir, String outputAlias) {
		tempFolder = outputDir;
		this.outputAlias = outputAlias.equals("/")?"":outputAlias.startsWith("/")?outputAlias:"/"+outputAlias ;
	}

   private void addCorsHeader(HttpServletResponse response){
	   //TODO CORS may only be needed for GET of http://www.apache.org/icons/*"
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
  
    }
   private void addCorsHeaderPOST(HttpServletResponse response){
       response.addHeader("Access-Control-Allow-Origin", "*");
	    response.addHeader("Access-Control-Allow-Credentials", "true");
   	
   }
 private String generateWebInterface() {
		String web_content = "";

	 try {
		 InputStream y =getClass().getClassLoader().getResource("web-ui.html").openStream();
			Scanner s = new Scanner((InputStream)y);
			s.useDelimiter("\\A");
			web_content = s.hasNext() ? s.next() : "";
			s.close();
			
	} catch (Exception e) {
		e.printStackTrace();
	 
	}
	

	 return web_content;
 }
 

   private String buildRedirectURL(String old_url, String baseURL) {
	   String rebuilded_redirect=""; 
		try {
			String full_url=URLDecoder.decode(old_url);
			String[] y = full_url.split("&");
			for (int i = 0; i < y.length; i++) {
				if(y[i].contains("redirect_uri")) {
					y[i] ="redirect_uri="+URLEncoder.encode(baseURL);
					break;
				}
				
			}
			String result="";
			for (String string : y) {
				result+=string+"&";	
			}
			rebuilded_redirect = result.substring(0,result.lastIndexOf("&"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("REDIRECT ->"+rebuilded_redirect);
		return rebuilded_redirect;
   }



}
