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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.eclipse.jetty.util.log.Log;

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
@WebServlet(value = "/GenerateCode", name = "CodeGenerator")

public class GenerateServlet extends HttpServlet {
	private Properties props = null;
	private static final String ONT = "ontologies";
	private static final String TEMPLATE = "template";
	private static final String VAR = "variables";
	private static final String CONTENT_TYPE = "Content-Type";
	private File tempFolder;
	private String outputAlias, outDir,out ;
	private VelocityContext velocityContext;
	private VelocityEngine vel_eng;
	private Template template;
	private final String HTMLtemplate = "listing.htm.vm";
	private final String baseTemplatePath = "/";
	private String servletName = "/GenerateCode";
	private JsonObject outO = null;
	private String response_get_path="";

	public GenerateServlet(String outDir) {
		this.outDir = outDir;
		this.vel_eng = new VelocityEngine();
		this.velocityContext = new VelocityContext();
		outO = new JsonObject();
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("post request");

		//if(req.getHeader(CONTENT_TYPE).contains("application/json")) {
			//System.out.println("post request");
			// { template: "", ontologies:[{url:"", recursive:""}],
			// variables:{varname:varvalue} }
			// { template: "http://localhost/template/",
			// ontologies:[{url:"https://protege.stanford.edu/ontologies/pizza/pizza.owl",recursive:"true"}], variables:{varname:varvalue} }
			//{ "template": "http://localhost/template/","ontologies":[{"url":"https://protege.stanford.edu/ontologies/pizza/pizza.owl", "recursive":"true"}], "variables":{"varname":"varvalue"} }
			JsonParser jp = new JsonParser();
			JsonElement sreq = jp.parse(req.getReader());
			if (sreq instanceof JsonObject) {
				JsonObject gc = (JsonObject) sreq;
				// set template & init project
				XmlParser parser = new XmlParser();
				TemplateDataModel model = null;
				try {
					model = parser.generateXMLCoordinator(gc.get(TEMPLATE).getAsString());
				} catch (Exception e) {
					resp.sendError(400, e.getMessage());
					return;
					// e.printStackTrace();
				}

				GenerateProject gp = new GenerateProject();
				gp.setMainModel(model);
				// set ontologies
				OntologyLoader ontologyLoader = new OntologyLoader();
				if (gc.get(ONT).isJsonArray() && gc.get(ONT).getAsJsonArray().size() > 0) {
					for (int i = 0; i < gc.get(ONT).getAsJsonArray().size(); i++) {
						if (gc.get(ONT).getAsJsonArray().get(0).isJsonPrimitive()) {
							// array of strings (multiple onts without recursive)
							gp.addOntology(
									ontologyLoader.loadOntology(gc.get(ONT).getAsJsonArray().get(i).getAsString()),
									false);
						} else {
							// array of object (multiple onts with recursive)
							JsonObject ont = gc.get(ONT).getAsJsonArray().get(0).getAsJsonObject();

							gp.addOntology(ontologyLoader.loadOntology(ont.get("url").getAsString()),
									ont.get("recursive").getAsBoolean());
						}
					}
				} else {
					if (gc.get(ONT).isJsonPrimitive()) {
						// string (single ont without recursive)
						gp.addOntology(ontologyLoader.loadOntology(gc.get(ONT).getAsString()), false);
					} else {
						// object (single ont with recursive parameter)
						JsonObject ont = gc.get(ONT).getAsJsonObject();
						gp.addOntology(ontologyLoader.loadOntology(ont.get("url").getAsString()),
								ont.get("recursive").getAsBoolean());
					}
				}

				// set variables
				for (Map.Entry<String, JsonElement> varEntry : gc.get(VAR).getAsJsonObject().entrySet()) {
					gp.setVariable(varEntry.getKey(), varEntry.getValue().getAsString());
				}

				// set Output
				out = Integer.toHexString(sreq.hashCode());
				File outFile = new File(tempFolder, out);
				// Files.deleteIfExists(outFile.toPath());
				this.deleteFolder(outFile);
				outFile.mkdirs();
				gp.setOutputFolder(outFile.getAbsolutePath() + File.separatorChar);
				// generate
				try {
					gp.process();
				} catch (Exception e) {
					resp.getWriter().println(e.getMessage());

				}
				boolean result;
				this.response_get_path = outputAlias;
				outO.addProperty("output", outputAlias+"/"+out);
				resp.addHeader(CONTENT_TYPE, "application/json");
				resp.getWriter().write(outO.toString());
			}
		//}
		
	
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String line, req_data, aux;
		URL urlToFile;
		System.out.println("doGET "+req.getRequestURI());
		
	
		if(req.getRequestURI().contains("/GenerateCode/ui")) {
			System.out.println("ui requested");
			resp.getWriter().write("<!DOCTYPE html>\r\n" + 
					"<html lang=\"en\">\r\n" + 
					"<head>\r\n" + 
					"    <meta charset=\"UTF-8\">\r\n" + 
					"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + 
					"    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\r\n" + 
					"    <title>Document</title>\r\n" + 
					"    <script type=\"text/javascript\">\r\n" + 
					"   \r\n" + 
					"    var jsonmock = {template:\"\",ontologies:[],variables:{}};\r\n" + 
					"\r\n" + 
					"    function addRow() {\r\n" + 
					"        // Find a <table> element with id=\"myTable\":\r\n" + 
					"        var table = document.getElementById(\"variables_table\");\r\n" + 
					"        // Create an empty <tr> element and add it to the 1st position of the table:\r\n" + 
					"        var row = table.insertRow(1);\r\n" + 
					"        var cell1 = row.insertCell(0);\r\n" + 
					"        var cell2 = row.insertCell(1);\r\n" + 
					"        var cell3 = row.insertCell(2);\r\n" + 
					"        var cell4 = row.insertCell(3);\r\n" + 
					"        var var_name = document.getElementById(\"var_name\").value;\r\n" + 
					"        var var_def_val = document.getElementById(\"def_val\").value;\r\n" + 
					"        var var_description = document.getElementById(\"description\").value;\r\n" + 
					"        //var var_req = document.getElementById(\"required\").value;        \r\n" + 
					"        var var_req =\"\"\r\n" + 
					"        if(document.getElementById(\"required\").checked){\r\n" + 
					"            var_req=\"true\";\r\n" + 
					"        }else{\r\n" + 
					"            var_req=\"false\";\r\n" + 
					"        }\r\n" + 
					"        cell1.innerHTML=var_name;\r\n" + 
					"        cell2.innerHTML=var_def_val;\r\n" + 
					"        cell3.innerHTML=var_description;\r\n" + 
					"        cell4.innerHTML=var_req;\r\n" + 
					"        \r\n" + 
					"    }\r\n" + 
					"    function addOnt(){\r\n" + 
					"        var table = document.getElementById(\"ont_table\");\r\n" + 
					"        var row = table.insertRow(1);\r\n" + 
					"        var cell1 = row.insertCell(0);\r\n" + 
					"        var cell2 = row.insertCell(1);\r\n" + 
					"        var ont_url = document.getElementById(\"ontology\").value;\r\n" + 
					"        var var_req =\"\"\r\n" + 
					"        if(document.getElementById(\"ont_required\").checked){\r\n" + 
					"            var_req=\"true\";\r\n" + 
					"        }else{\r\n" + 
					"            var_req=\"false\";\r\n" + 
					"        }\r\n" + 
					"        cell1.innerHTML=ont_url;\r\n" + 
					"        cell2.innerHTML=var_req;\r\n" + 
					"        var ontology={url:\"\",recursive:\"\"}\r\n" + 
					"        ontology.url=ont_url;\r\n" + 
					"        ontology.recursive=var_req;\r\n" + 
					"        jsonmock.ontologies.push(ontology);\r\n" + 
					"\r\n" + 
					"    }\r\n" + 
					"    var coordinator_path=document.getElementById(\"coordinator\").value\r\n" + 
					"    jsonmock.template=coordinator_path;\r\n" + 
					"    </script>\r\n" + 
					"</head>\r\n" + 
					"<body>\r\n" + 
					"    <form name=\"process\" method=\"POST\" action=\"GenerateCode\">\r\n" + 
					"        <h1>Web interface to CodeGenerator REST tool</h1>\r\n" + 
					"        \r\n" + 
					"             \r\n" + 
					"        <table id=\"ont_table\" border=\"1px\">\r\n" + 
					"            <tr>\r\n" + 
					"                <th>Ontology URL</th>\r\n" + 
					"                <th>Required</th>\r\n" + 
					"            </tr>\r\n" + 
					"        </table>\r\n" + 
					"        <fieldset>\r\n" + 
					"        <legend>Ontologies</legend>\r\n" + 
					"\r\n" + 
					"        <input type=\"text\" id=\"ontology\"><br>\r\n" + 
					"        <input type=\"button\" onclick=\"addOnt()\" value=\"add\">\r\n" + 
					"        <input type=\"checkbox\"   id=\"ont_required\">Required?<br>\r\n" + 
					"\r\n" + 
					"        </fieldset>\r\n" + 
					"\r\n" + 
					"        \r\n" + 
					"        <table id=\"variables_table\" border=\"1px\">\r\n" + 
					"            <tr>\r\n" + 
					"                <th>Variable name</th>\r\n" + 
					"                <th>Variable default value</th>\r\n" + 
					"                <th>Variable description</th>\r\n" + 
					"                <th>Variable required(true/false)</th>\r\n" + 
					"            </tr>\r\n" + 
					"        </table>\r\n" + 
					"        <fieldset>\r\n" + 
					"            <legend>varaibles:</legend>\r\n" + 
					"            Name: <input type=\"text\" id=\"var_name\"><br>\r\n" + 
					"            Default Value: <input type=\"text\" id=\"def_val\"><br>\r\n" + 
					"            Description: <input type=\"text\" id=\"description\"> <br>\r\n" + 
					"        <input type=\"checkbox\"   id=\"required\">Required?<br>\r\n" + 
					"        <input type=\"button\" onclick=\"addRow()\" value=\"add\">\r\n" + 
					"        </fieldset>\r\n" + 
					"        <p>recursive</p>\r\n" + 
					"        <input type=\"checkbox\" name=\"vehicle1\" >Load Recursive?<br>\r\n" + 
					"        <p>Description</p>\r\n" + 
					"        <input type=\"text\"><br>\r\n" + 
					"        <p>XML Coordinator url</p>\r\n" + 
					"        <input type=\"text\" id=\"coordinator\"><br>\r\n" + 
					"        <input type=\"submit\" value=\"Submit\">\r\n" + 
					"      </form> \r\n" + 
					"</body>\r\n" + 
					"</html>");
			return;
		}else if(req.getRequestURI().contains("/GenerateCode/")) {
			System.out.println("GenerateCode requested ");
			
			req_data = req.getRequestURI().replaceAll(servletName, "");
			urlToFile = this.getServletContext().getResource(req_data);

			try {
				File t = new File(urlToFile.getFile());
				if (t.isFile()) {

					BufferedReader br = null;
					br = new BufferedReader(new FileReader(t));
					StringBuilder sb = new StringBuilder();

					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					resp.getWriter().write(sb.toString());
				}

				if (t.isDirectory()) {
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
					context.put("BACK", req_data.split("/").length > 3);
					template.setData(runtimeServices.parse(reader, HTMLtemplate));
					// template.setData(runtimeServices.parse(reader, template));
					// template.setData(runtimeServices.parse(reader, HTMLtemplate, false));
					template.initDocument();
					template.merge(context, stringWriter);
					resp.getWriter().write(stringWriter.toString());
					stringWriter.close();

				}

			} catch (Exception e) {
						System.out.println(e.getMessage());
				}
			return;
		}
		
		
		else {
			resp.getWriter().write("nothing to show in this path");

		}
		
		

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
		this.outputAlias = outputAlias;
	}

}
