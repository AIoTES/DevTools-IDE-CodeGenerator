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
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.servlet.ServletException;
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
	private String out;
	private String token=null,redirect_url=null,oidc_code=null;
	private JsonParser jp;
	private String host_name = System.getenv("AIOTES_HOSTNAME");
	private String base_post_path = System.getenv("CODEGENERATOR_PATH");
	private String host_port = System.getenv("AIOTES_API_PORT");
	private String redir_url="/auth/realms/activage/account";
	private String test_redir_url="/auth/realms/Activage/protocol/openid-connect/auth";
	
	boolean isAuthorized=false;

	public GenerateServlet() {
		jp = new JsonParser();
	}

	private static final long serialVersionUID = 1L;
	//{"template": "http://localhost/template/","ontologies":[{"url":"https://protege.stanford.edu/ontologies/pizza/pizza.owl", "recursive":"true"}], "variables":{"varname":"varvalue"}}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST REQUEST " +req.getRequestURL().toString());
		if(req.getParameter("token")!= null) {
			//TODO validate token
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
						System.out.println("response JSON -> "+outO);
						System.out.println("output content location -> "+outFile.getAbsolutePath());
						resp.getWriter().write(outO.toString());
						//resp.sendRedirect(this.SERVER+"GenerateCode");
					}else
						resp.sendError(500,"invalid json root element");
				}catch (Exception e) {
					resp.sendError(400,e.getLocalizedMessage());
				}
			}else 
				resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);

		}else {
			resp.sendError(400, "Missing token in URL param");
		}
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.token= req.getParameter("token");
		this.oidc_code= req.getParameter("code");
		System.out.println("doGet request URL -> "+req.getRequestURL().toString()); 
		String line, req_data;
		URL urlToFile;
		addCorsHeaderPOST(resp);
		if(req.getRequestURI().equals(outputAlias+"/ui")) {
			if(this.token == null && this.oidc_code == null) {
				this.redirect_url=buildRedirectURL(req.getRequestURL().toString());
				if(!this.redirect_url.isEmpty()) {
					System.out.println("Authorization token missing. Redirecting to authentication server -> "+this.redirect_url);
					resp.sendRedirect(this.redirect_url);
				} else {
					resp.sendError(500, "Could not build redirect URL");
				}
			}else{
				System.out.println("doGet Authorization token= "+this.token);
				System.out.println("doGet Authorization code= "+this.oidc_code);
				Map<String, Object > vars = new HashMap<String, Object>();
				vars.put("token",this.token);
				vars.put("post_url",this.base_post_path+"?token="+this.token);
				vars.put("gen_code_path",this.base_post_path);
				vars.put("generaed_code_path",this.base_post_path.replaceAll("/ui", ""));
				try {
					resp.getWriter().write(this.processVelocityTemplate("web-ui.vm", vars));	
				} catch (Exception e) {
					e.printStackTrace();
					resp.sendError(500, "Can't generate web interface. "+e.getMessage());
				}
				
	
			}
		}else if(req.getRequestURI().equals(outputAlias+"/swagger")){
			if(this.token==null && this.oidc_code == null) {
				this.redirect_url=buildRedirectURL(req.getRequestURL().toString());
				resp.sendRedirect(this.redirect_url);
			}else {
				System.out.println("doGet Authorization token= "+this.token);
				System.out.println("doGet Authorization token= "+this.oidc_code);
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
			System.out.println("REQ URI "+req.getRequestURI());
			req_data = req.getRequestURI().replaceFirst(outputAlias, "");
			urlToFile = this.getServletContext().getResource("/"+this.out+req_data);
			System.out.println("sending generated content --> "+"/"+this.out+req_data);
			
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
					Map<String, Object> vbles = new HashMap<String, Object>();
					vbles.put("req_url", req.getRequestURI().toString());
					vbles.put("path", t.getAbsolutePath());
					vbles.put("file", t);
					vbles.put("dirContent", t.listFiles());
					vbles.put("hash", this.out);
					vbles.put("BACK", req_data.split("/").length > 3);
					String processed_teplate = this.processVelocityTemplate("listing.htm.vm", vbles);
					resp.getWriter().write(processed_teplate);
					resp.setContentType("text/html");

				}
			} catch (Exception e) {
				e.printStackTrace();
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
 

   private String buildRedirectURL(String request_uri) {
	   String rebuilded_redirect="";
	   if(this.host_name != null  && this.host_port != null) {
		   if(!this.host_name.startsWith("http")) this.host_name = "https://"+this.host_name;
			try {
				if(this.host_port.equals("443")) {
					rebuilded_redirect = this.host_name+":"+this.test_redir_url;
				}else {
					rebuilded_redirect = this.host_name+":"+this.host_port+this.test_redir_url;	
				}
				rebuilded_redirect+="?client_id=codegenerator&redirect_uri="+URLEncoder.encode(request_uri)+"&scope=openid&state=somestate&response_type=code";
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
	   }else {
		   System.out.println("host_name or host_port null. Host name="+this.host_name+" host_port="+this.host_port);
	   }
System.out.println("rebuilded redirect "+rebuilded_redirect);
		return rebuilded_redirect;
   }

	private String processVelocityTemplate(String template_name, Map<String,Object> variables)  throws Exception{
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		StringWriter stringWriter = new StringWriter();
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(GenerateServlet.class.getClassLoader().getResourceAsStream(template_name)));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(sb.toString());
		Template template = new Template();
		template.setRuntimeServices(runtimeServices);
		VelocityContext context = new VelocityContext();
		
		for (Entry<String, Object> item : variables.entrySet()) {
			context.put(item.getKey(), item.getValue());	
		}
		context.put("dateformatter",df2);
        
		template.setData(runtimeServices.parse(reader, template_name));
		template.initDocument();
		template.merge(context, stringWriter);
		stringWriter.close();
		return stringWriter.toString();
	}




}
