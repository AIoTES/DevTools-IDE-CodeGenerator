package es.upm.tfo.lst.CodeGenerator.xmlparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.Author;
import es.upm.tfo.lst.CodeGenerator.model.MacroModel;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
/**
 * Class who read XML file and parse it to Java code. These class can recognize if given string represents a URL or a directory in local file system.
 * If user give a URL, these class automatically read all of files contained in given URL and store it into a temporary directory
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class XmlParser {

	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private URL xmlSource=null;
	private NodeList nodeVariable, nodeMacro,templateName, templateVersion,templateDescription,templateAuthor;
	private Map<String,Variable> variableList;
	private List<MacroModel> macroList;
	private TemplateDataModel javaXMLModel = null;
	private Author author;
	private URL templateBasePath=null;
	
	//---------------
	private org.jsoup.nodes.Document doc; 
	private URI uri;
	private Elements t;
	private String output="";
	private File fileToWrite,templatesTempDir,xmlFile;
	private List<URL> arrayOfSites = new ArrayList<>();
	private List<String> arrayOfNames = new ArrayList<>();
	private boolean isLocal=true;
	
	

	public boolean isLocal() {
		return isLocal;
	}

	public XmlParser() {
			this.variableList = new HashMap<>();
			this.macroList = new ArrayList<>();

		}

	/**
	 *generate XmlCoordinator object who represent XML file into java code. In this method will be generated all files contained in remote directory (URL)
	 * if a URL is given, the method automatically download all template content to use 
	 * @param xmlPath {@link String } path to XML file
 	 * @return {@link TemplateDataModel} object representing XML file in Java code
	 */
	public TemplateDataModel generateXMLCoordinator(String xmlPath){
		
		
		boolean flag=false;
		System.out.println("generate XML coordinator "+xmlPath);
		try{
			this.xmlSource =  new URL(xmlPath);
			flag=true;
		}catch(Exception a) {
			System.out.println("couldnt interpret current path as URL "+a.getMessage());
			flag=false;
		}
		
		if(flag) {
			log.debug("generating XML coordinator  from url= "+this.xmlSource);
			this.isLocal=false;
			//this.readWebTemplate(this.xmlSource);
			try {
				//this.xmlSource =  new File(this.templatesTempDir.getPath()).toURI().toURL();
				this.templateBasePath=xmlSource.toURI().resolve(".").toURL();
			} catch (MalformedURLException | URISyntaxException e) {
				log.fatal("problems reading URL",e);
			}
		}else {
			log.debug("generating XML coordinator  from local file system= "+xmlPath);
			this.isLocal=true;
			log.warn("given URL isn't valid, trying to interpret as filesystem...given path="+xmlPath);
			try {
				this.xmlSource = new File(xmlPath).toURI().toURL();
				this.templateBasePath=xmlSource.toURI().resolve(".").toURL();		
			} catch (Exception e2) {
				log.fatal("error processing xml from given path="+xmlPath, e2);
				
			}
		}
		

		this.readXML();
		if(flag) {
			log.debug("adding remote loader path to xml model "+this.templateBasePath);
			this.javaXMLModel.setBaseTemplatePath(this.templateBasePath.toString());
		}else {
			log.debug("adding local loader path to xml model "+this.templateBasePath.getPath());
			this.javaXMLModel.setBaseTemplatePath(this.templateBasePath.getPath());
		}
		return this.javaXMLModel;
	}
	


	/**
	 * generate from XML file an {@link TemplateDataModel} object representing XML file into Java code
	 * @param xmlSource {@link String}  representing the location from XML file to load
	 */
	private void readXML()  {
		
		Element t;
		this.author = new Author();
		try {

			this.javaXMLModel = new TemplateDataModel();
			 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	         log.debug("readXML "+xmlSource.getPath());
	         Document doc = docBuilder.parse (xmlSource.openStream());
	         this.nodeMacro = doc.getElementsByTagName("macro");
	         this.nodeVariable = doc.getElementsByTagName("variable");
	         this.templateAuthor = doc.getElementsByTagName("template-author");
	         this.templateName = doc.getElementsByTagName("template-name");
	         this.templateVersion = doc.getElementsByTagName("template-version");
	         this.templateDescription = doc.getElementsByTagName("template-description");

	         t = (Element)this.templateName.item(0);

	         this.javaXMLModel.setName( t.getFirstChild().getTextContent());
	         t = (Element)this.templateVersion.item(0);
	         this.javaXMLModel.setVersion( t.getFirstChild().getTextContent());
	         t = (Element)this.templateDescription.item(0);
	         this.javaXMLModel.setDescription( t.getFirstChild().getTextContent() );
	         t = (Element)this.templateAuthor.item(0);
	         this.author.setName(t.getElementsByTagName("author-name").item(0).getTextContent());
	         


	         for(int y=0;y<this.nodeVariable.getLength();y++){
	             	Element b = (Element)this.nodeVariable.item(y);
	             	this.variableList.put(
	             			b.getElementsByTagName("name").item(0).getTextContent(),new Variable(
	             			b.getElementsByTagName("name").item(0).getTextContent(),
	             			b.getElementsByTagName("description").item(0).getTextContent(),
	             			b.getElementsByTagName("required").item(0).getTextContent().equalsIgnoreCase("true"),
	             			b.getElementsByTagName("default").item(0).getTextContent()));

	         }

	         for(int y=0;y<this.nodeMacro.getLength();y++){

	          	Element b = (Element)this.nodeMacro.item(y);

	          	this.macroList.add( new MacroModel(
	          			b.getElementsByTagName("template").item(0).getTextContent(),
	          			b.getElementsByTagName("output").item(0).getTextContent(),
	          			b.getElementsByTagName("for").item(0).getTextContent()));

	         }

	         this.javaXMLModel.setMacroList(this.macroList);
	         //modify definition of array of variables
	         this.javaXMLModel.setVars(this.variableList);
	         
	         this.javaXMLModel.setAuthor(this.author);

	         //this.javaXMLModel.setBaseTemplatePath(this.templateBasePath.toString());
	         	
		}catch ( ParserConfigurationException | IOException | SAXException a) {
			log.fatal("error" , a);
			this.javaXMLModel = null;
		}

	}

	public URL getParentTemplateDir() throws MalformedURLException, URISyntaxException {
		 return xmlSource.toURI().resolve("..").toURL();
	 }

	

	
	/**
	 * method to set the output to store temporary templates
	 * @param output
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	public void setFileToWrite(File fileToWrite) {
		this.fileToWrite = fileToWrite;
	}

//	public String getTemplateBasePath() {
//		return templateBasePath.getPath();
//	}

}
