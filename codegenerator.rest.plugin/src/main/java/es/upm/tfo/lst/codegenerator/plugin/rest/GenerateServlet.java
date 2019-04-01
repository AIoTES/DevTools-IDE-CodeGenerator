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

import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
@WebServlet(value = "/GenerateCode", name = "CodeGenerator")

public class GenerateServlet extends HttpServlet {
	private Properties props = null;
	private static final String ONT = "ontologies";
	private static final String TEMPLATE = "template";
	private static final String VAR = "variables";
	private static final String CONTENT_TYPE = "Content-Type";
	private File tempFolder;
	private String outputAlias, outDir;
	private VelocityContext velocityContext;
	private VelocityEngine vel_eng;
	private Template template;
	private final String HTMLtemplate = "listing.htm.vm";
	private final String baseTemplatePath = "/";
	private String servletName = "/GenerateCode";
	private JsonObject outO = null;

	public GenerateServlet(String outDir) {
		this.outDir = outDir;
		this.vel_eng = new VelocityEngine();
		this.velocityContext = new VelocityContext();
		outO = new JsonObject();
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (req.getHeader(CONTENT_TYPE).contains("application/json")) {

			// { template: "", ontologies:[{url:"", recursive:""}],
			// variables:{varname:varvalue} }
			// { template: "http://localhost/template/",
			// ontologies:[{url:"https://protege.stanford.edu/ontologies/pizza/pizza.owl",
			// recursive:"true"}], variables:{varname:varvalue} }
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
				String out = Integer.toHexString(sreq.hashCode());
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

				outO.addProperty("output", outputAlias+"/"+out);
				resp.addHeader(CONTENT_TYPE, "application/json");
				resp.getWriter().println(outO.toString());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String line, req_data, aux;
		URL urlToFile;
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
