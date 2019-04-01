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
package es.upm.tfo.lst.CodeGenerator.xmlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.Soundbank;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.velocity.runtime.log.LogDisplayWrapper;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.MacroModel;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;

/**
 * Class to read XML file and parse it to Java code. These class can recognize
 * if given string represents a URL or a directory in local file system. If user
 * gives an URL, this class automatically reads all of files contained in given
 * URL and stores them into a temporary directory.
 *
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class XmlParser {

	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private URL xmlSource = null;
	private NodeList nodeVariable, nodeMacro, templateName, templateVersion, templateDescription, templateAuthor;
	private Map<String, Variable> variableList;
	private List<MacroModel> macroList;
	private TemplateDataModel javaXMLModel = null;
	private URL templateBasePath = null;

	// ---------------
	private org.jsoup.nodes.Document doc;
	private URI uri;
	private Elements t;
	private String output = "";
	private File fileToWrite, templatesTempDir, xmlFile;
	private List<URL> arrayOfSites = new ArrayList<>();
	private List<String> arrayOfNames = new ArrayList<>();
	private List<String> arrayOfImports = new ArrayList<>();
	private boolean isLocal = true;
	private Set<Map<String, String> > imports=null;
	
	public boolean isLocal() {
		return isLocal;
	}

	public XmlParser() {
		this.variableList = new HashMap<>();
		this.macroList = new ArrayList<>();

	}

	/**
	 * generate XmlCoordinator object who represent XML file into java code.
	 *
	 * @param xmlPath {@link String } path to XML file. This String value can be a
	 *                website or path to local file
	 * @return {@link TemplateDataModel} object representing XML file in Java code,
	 *         or null if some problem occur in the process
	 */
	public TemplateDataModel generateXMLCoordinator(String xmlPath) throws Exception {

		boolean flag = false;
		System.out.println("generate XML coordinator from path: " + xmlPath);
		try {
			// xml source has the full URL to xml file
			this.xmlSource = new URL(xmlPath);
			flag = true;
		} catch (Exception a) {
			System.out.println("couldnt interpret current path as URL " + a.getMessage());
			// flag=false;
		}
		// if the flag is true, means the xml file could be processed ok
		if (flag) {
			log.debug("generating XML coordinator  from url= " + this.xmlSource);
			this.isLocal = false;
			// this.readWebTemplate(this.xmlSource);
			try {
				// this.xmlSource = new File(this.templatesTempDir.getPath()).toURI().toURL();
				log.debug("setting up the father directory from given url");
				// teplateBasePath has the father of the given url (father of xm file)
				this.templateBasePath = this.xmlSource.toURI().resolve(".").toURL();
			} catch (MalformedURLException | URISyntaxException | NullPointerException e) {
				log.fatal("problems reading URL", e);
			}
		} else {
			log.debug("generating XML coordinator  from local file system= " + xmlPath);
			this.isLocal = true;
			log.warn("given URL isn't valid, trying to interpret as filesystem...given path=" + xmlPath);
			try {
				this.xmlSource = new File(xmlPath).toURI().toURL();
				this.templateBasePath = xmlSource.toURI().resolve(".").toURL();
			} catch (Exception e2) {
				log.fatal("error processing xml from given path=" + xmlPath, e2);
				throw e2;
			}
		}

		if (this.xmlSource != null) {

			this.readXML();
			if (this.javaXMLModel != null) {
				if (flag) {
					log.debug("adding remote loader path to xml model " + this.templateBasePath);
					this.javaXMLModel.setBaseTemplatePath(this.templateBasePath.toString());
				} else {
					log.debug("adding local loader path to xml model " + this.templateBasePath.getPath());
					this.javaXMLModel.setBaseTemplatePath(this.templateBasePath.getPath());
				}
			} else {
				log.fatal("given path cant be processed as a valid template");
				//throw new Exception("given path cant be processed as a valid template");
				return null;
			}

			return this.javaXMLModel;
		} else {
			log.fatal("given path cant be processed as a valid template");
			return null;
		}

	}

	/**
	 * Generate from XML file an {@link TemplateDataModel} object representing XML
	 * file into Java code.
	 *
	 * @param xmlSource {@link String} representing the location from XML file to
	 *                  load
	 */
	private void readXML() throws Exception {
		
		Element t;

		try {

			this.javaXMLModel = new TemplateDataModel();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			log.debug("readXML " + xmlSource.getPath());

			// IOException - If any IO errors occur.
			// SAXException - If any parse errors occur.
			// IllegalArgumentException - When is is null
			Document doc = docBuilder.parse(this.xmlSource.openStream());

			try {
				this.nodeVariable = doc.getElementsByTagName("variable");
				this.templateAuthor = doc.getElementsByTagName("template-author");
				this.templateName = doc.getElementsByTagName("template-name");
				this.templateVersion = doc.getElementsByTagName("template-version");
				this.templateDescription = doc.getElementsByTagName("template-description");
				
				t = (Element) this.templateName.item(0);

				this.javaXMLModel.setName(t.getFirstChild().getTextContent());
				t = (Element) this.templateVersion.item(0);
				this.javaXMLModel.setVersion(t.getFirstChild().getTextContent());
				t = (Element) this.templateDescription.item(0);
				this.javaXMLModel.setDescription(t.getFirstChild().getTextContent());
				// xml tags not obligatory
				t = (Element) this.templateAuthor.item(0);

				
				//processing node variables
				for (int y = 0; y < this.nodeVariable.getLength(); y++) {
					Element b = (Element) this.nodeVariable.item(y);

					this.variableList
							.put(b.getElementsByTagName("name").item(0).getTextContent(),
									new Variable(b.getElementsByTagName("name").item(0).getTextContent(),
											b.getElementsByTagName("description").item(0).getTextContent(),
											b.getElementsByTagName("required").item(0).getTextContent()
													.equalsIgnoreCase("true"),
											b.getElementsByTagName("default").item(0).getTextContent()));

				}
				
				this.javaXMLModel.setVars(this.variableList);

			} catch (Exception e) {
				log.warn("some optionals tags into XML coordinator file isn't set");
			}

			this.nodeMacro = doc.getElementsByTagName("macro");
			for (int y = 0; y < this.nodeMacro.getLength(); y++) {
				HashMap <String, String> p ;
				this.imports = new HashSet<>();		
				Element b = (Element) this.nodeMacro.item(y);
				Element h = (Element)b.getElementsByTagName("imports").item(0);
				for (int i = 0; i <h.getElementsByTagName("FullyQualifiedName").getLength(); i++) {
					String name = h.getElementsByTagName("FullyQualifiedName").item(i).getTextContent();
					p = new HashMap<String, String>();
					
					if(name != null ) {
						log.debug("name "+name);
						//TODO control characters. Maybe throw an error indicating if the given string to imports is not valid
						if(!name.equals("")) { 
							
							if( name.contains(".")) {
								p.put(name.substring(name.lastIndexOf(".")).replace(".", ""), name);
								this.imports.add(p);
							}else {
								log.warn("The given import ("+name+") has not the correct format");
								p.put(name,name);
								this.imports.add(p);
							}
						
						}else
							log.warn("empty import tag...skipping immport ");
							//throw new Exception("The given import ("+name+") has not the correct format");	
					}
				
					
				}
				
				this.macroList.add(
						new MacroModel(
							b.getElementsByTagName("template").item(0).getTextContent(),
							b.getElementsByTagName("output").item(0).getTextContent(),
							b.getElementsByTagName("for").item(0).getTextContent(),
							this.imports
						));
			}

			this.javaXMLModel.setMacroList(this.macroList);

		} catch (Exception a) {
			if (a instanceof ConnectException)
				log.fatal("Culdn't read given XML file. Seems the XML url is not responding", a);
			if (a instanceof SAXParseException)
				log.fatal("Culdn't read given XML file. Seems the XML have syntax errors", a);
			if (a instanceof FileNotFoundException)
				log.fatal("Culdn't read given XML file. Seems the XML file doesn't exist", a);
			if (a instanceof NullPointerException)
				log.fatal("Culdn't read given XML file. Seems the XML file is null", a);

			this.javaXMLModel = null;
			throw a;
		}

	}

	public URL getParentTemplateDir() throws MalformedURLException, URISyntaxException {
		return xmlSource.toURI().resolve("..").toURL();
	}

	/**
	 * method to set the output to store temporary templates
	 *
	 * @param output
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	public void setFileToWrite(File fileToWrite) {
		this.fileToWrite = fileToWrite;
	}

}
