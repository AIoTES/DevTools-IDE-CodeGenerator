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
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
//@WebServlet (value="/GenerateCode", name="CodeGenerator")
@WebServlet("/FirstServlet")
public class GenerateServlet extends HttpServlet {
	
	public GenerateServlet(String outDir) {
		
		this.outDir = outDir;
	}
	private static final String ONT = "ontologies";
	private static final String TEMPLATE = "template";
	private static final String VAR = "variables";
	private static final String CONTENT_TYPE="Content-Type";
	private File tempFolder;
	private String outputAlias,outDir;


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		if(req.getHeader(CONTENT_TYPE)==null) {
//			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
//			return;
//		}
		if (req.getHeader(CONTENT_TYPE).contains("application/json")) {
			// interpret JSon

			 // { template: "", ontologies:[{url:"", recursive:""}], variables:{varname:varvalue} }
			//{ template: "http://localhost/template/", ontologies:[{url:"https://protege.stanford.edu/ontologies/pizza/pizza.owl", recursive:"true"}], variables:{varname:varvalue} }
			JsonParser jp = new JsonParser();
			JsonElement sreq = jp.parse(req.getReader());
			if (sreq instanceof JsonObject) {
				JsonObject gc = (JsonObject) sreq;
				// set template & init project
				XmlParser parser = new XmlParser();
				TemplateDataModel model=parser.generateXMLCoordinator(gc.get(TEMPLATE).getAsString());
				 
				GenerateProject gp = new GenerateProject();
				gp.setMainModel(model);
				// set ontologies
				OntologyLoader ontologyLoader = new OntologyLoader();
				if (gc.get(ONT).isJsonArray() && gc.get(ONT).getAsJsonArray().size() > 0) {
					for (int i = 0; i < gc.get(ONT).getAsJsonArray().size(); i++) {
						if (gc.get(ONT).getAsJsonArray().get(0).isJsonPrimitive()) {
							// array of strings (multiple onts without recursive)
							gp.addOntology(ontologyLoader.loadOntology(gc.get(ONT).getAsJsonArray().get(i).getAsString()), false);
						} else {
							// array of object (multiple onts with recursive)
							JsonObject ont = gc.get(ONT).getAsJsonArray().get(0).getAsJsonObject();

							gp.addOntology(ontologyLoader.loadOntology(ont.get("url").getAsString()), ont.get("recursive").getAsBoolean());
						}
					}
				} else {
					if (gc.get(ONT).isJsonPrimitive()) {
						// string (single ont without recursive)
						gp.addOntology(ontologyLoader.loadOntology(gc.get(ONT).getAsString()), false);
					} else {
						// object (single ont with recursive parameter)
						JsonObject ont = gc.get(ONT).getAsJsonObject();
						gp.addOntology(ontologyLoader.loadOntology(ont.get("url").getAsString()), ont.get("recursive").getAsBoolean());
					}
				}

			// set variables
			for (Map.Entry<String, JsonElement> varEntry : gc.get(VAR).getAsJsonObject().entrySet()) {
				gp.setVariable(varEntry.getKey(), varEntry.getValue().getAsString());
			}

			// set Output
			String out = Integer.toHexString(sreq.hashCode());
			File outFile = new File(tempFolder, out);
			//Files.deleteIfExists(outFile.toPath());
			this.deleteFolder(outFile);
			outFile.mkdirs();
			gp.setOutputFolder(outFile.getAbsolutePath()+File.separatorChar);
			// generate
			boolean result;
			try {
				 result = gp.process();
			} catch (Exception e) {
				e.printStackTrace(resp.getWriter());
				result = false;
			}

			if (result) {
				// response with Output reference
				JsonObject outO = new JsonObject();
				outO.addProperty("output", outputAlias + "/" + out);
				resp.addHeader(CONTENT_TYPE, "application/json");
				resp.getWriter().println(outO.toString());
				
			}else {
				resp.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
			}else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("...GET request..");
		String requestContent="";
		System.out.println("request URI "+req.getRequestURI());
			//resp.sendRedirect("https://www.google.com/");	
			//RequestDispatcher view = req.getRequestDispatcher("/");
			// don't add your web-app name to the path
			
			//view.forward(req, resp);
			//		req.getParameter(ONT);
			//		req.getParameter(TEMPLATE);
			//		req.getParameter(VAR);
			//		 resp.setContentType("text/html");
			//		 resp.getWriter().println("First Servlet on Jetty - Java Code Geeks");
			//		 
			//		System.out.println("output alias "+this.outputAlias);
				System.out.println("context path "+this.getServletContext().getContextPath());
			//
		requestContent = req.getRequestURI().replace("/FirstServlet", "");
		System.out.println("requestContent "+requestContent);
		try {
			File f = new File(requestContent);
			System.out.println("file "+f.getAbsolutePath());
			if(f.isDirectory()) {
			
				System.out.println("directory");
			}
			if(f.isFile()){
				System.out.println("not directory");
				System.out.println("outDir "+this.outDir);
				BufferedReader reader = new BufferedReader(new FileReader(f));
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				String ls = System.getProperty("line.separator");
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append(ls);
				}
				 resp.getWriter().println(stringBuilder.toString());
			}
		}catch (Exception e) {
			System.out.println("error "+e.getMessage());
		}
		
		
	}

	public  void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
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
		this.outputAlias = outputAlias;
	}



}
