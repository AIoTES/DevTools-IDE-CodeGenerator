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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

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
@WebServlet (value="/GenerateCode", name="CodeGenerator")

public class GenerateServlet extends HttpServlet {
	private Properties props=null;
	private static final String ONT = "ontologies";
	private static final String TEMPLATE = "template";
	private static final String VAR = "variables";
	private static final String CONTENT_TYPE="Content-Type";
	private File tempFolder;
	private String outputAlias,outDir;
	private VelocityContext velocityContext;
	private VelocityEngine vel_eng;
	private Template template;
	private final String HTMLtemplate="listing.htm.vm";
	private final String baseTemplatePath="/";
	private String servletName="/GenerateCode";
	public GenerateServlet(String outDir) {
		this.outDir = outDir;
		this.vel_eng = new VelocityEngine();
		this.velocityContext = new VelocityContext();
		
	}

	

	
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
		String line,req_data;
		URL urlToFile;
		StringBuilder stringBuilder= new StringBuilder();
		//urlToFile=this.getServletContext().getResource("ac330991/projectOutput");
		req_data=req.getRequestURI().replaceAll(servletName, "");
		System.out.println("req_data "+req_data);
		urlToFile=this.getServletContext().getResource(req_data);
		System.out.println("urlToFile "+urlToFile);
		try {
			
			File t = new File( urlToFile.getFile());
			if(t.isFile()) {
				System.out.println("file exists? "+t.exists());
				BufferedReader reader = new BufferedReader(new FileReader(t));

				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append( System.getProperty("line.separator"));
				}
				resp.getWriter().write(stringBuilder.toString());				
			}else {
				File t1[] = t.listFiles(); 
				
				resp.getWriter().write("dir getted");
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	//	this.doPost(req, resp);
//		System.out.println("...GET request..");
//		String requestContent="";
//		String writer_content="";
//		System.out.println("request URI "+req.getRequestURI());
//		String tempLocation="";
//				requestContent = req.getRequestURI().replace("/FirstServlet", "");
//				System.out.println("requestContent "+requestContent);
//				System.out.println("outDir "+this.outDir+"\\"+requestContent);
//				System.out.println("servlet context "+this.getServletConfig().getServletContext().toString());
//				try {
//					File f = new File(this.outDir+"\\"+requestContent+"\\"+tempLocation);
//					this.vel_eng.init();
//					this.velocityContext.put("path", Paths.get(requestContent.substring(requestContent.indexOf("/"))));
					//this.velocityContext.put("file", f);
					//this.template=vel_eng.getTemplate(this.HTMLtemplate);
//					this.template = new Template();
					//GenerateServlet.class.getClassLoader().getResourceAsStream(HTMLtemplate);
					//this.template.merge(this.velocityContext, resp.getWriter() );
					
				
//					if(f.isFile()){
						
						
//						StringWriter fw = new StringWriter();
//						StringResourceRepository rep= StringResourceLoader.getRepository();
//						//VelocityEngine ve = new VelocityEngine();
//						Template te = new Template();
//						RuntimeServices rs=RuntimeSingleton.getRuntimeServices();
//						StringReader sr = new StringReader(GenerateServlet.class.getClassLoader().getResourceAsStream(HTMLtemplate).toString());
//						SimpleNode sn = rs.parse(sr,"s");
//						rep = new StringResourceRepositoryImpl();
//						StringResourceLoader.setRepository(StringResourceLoader.REPOSITORY_NAME_DEFAULT, rep);
//						te.setRuntimeServices(rs);
//					    te.setData(sn);
//					    te.initDocument();
//						te.merge(velocityContext, fw);
//						resp.getWriter().write(fw.toString());
//						fw.close();
						//f = new File(this.outDir+"\\"+requestContent+"\\"+tempLocation);
//						System.out.println("complete dir "+f.getAbsolutePath());
//						BufferedReader reader = new BufferedReader(new FileReader(f));
//						StringBuilder stringBuilder = new StringBuilder();
//						String line = null;
//						String ls = System.getProperty("line.separator");
//						while ((line = reader.readLine()) != null) {
//							stringBuilder.append(line);
//							stringBuilder.append(ls);
//						}
//						 resp.getWriter().println(stringBuilder.toString());
//					}else{
//						 resp.getWriter().println("given file name or file path is not valid,please check it");
//						 }
//				}catch (Exception e) {
//					e.printStackTrace();
//				} 

		
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
