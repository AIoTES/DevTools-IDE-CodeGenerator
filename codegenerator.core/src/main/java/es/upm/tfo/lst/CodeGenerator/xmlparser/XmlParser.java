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
	 */
	public void generateXMLCoordinator(String xmlPath){
		
		/*
		 * recibe un string con la direcciob del arhivo xml
		 * si es una URL valido usa el metodo readWebTemplate para descargar las templates 
		 * */
		boolean flag=false;
		System.out.println("generate XML coordinator "+xmlPath);
		try{
			this.xmlSource =  new URL(xmlPath);
			flag=true;
			this.xmlSource =  new URL(xmlPath);
			
==== BASE ====
		}catch(Exception a) {
			System.out.println("couldnt interpret current path as URL "+a.getMessage());
			flag=false;
		}
		if(flag) {
			System.out.println("load from url from (xmlSource): "+this.xmlSource);
			this.isLocal=false;
			this.readWebTemplate(this.xmlSource);
			try {
				this.xmlSource =  new File(this.templatesTempDir.getPath()).toURI().toURL();
				this.templateBasePath=xmlSource.toURI().resolve(".").toURL();
				System.out.println("this.templateBasePath= "+this.templateBasePath);
			} catch (MalformedURLException | URISyntaxException e) {
				this.templateBasePath=null;
				e.printStackTrace();
			}
		}else {
			System.out.println("load from local filesystem");
			this.isLocal=true;
			log.warn("given URL isn't valid, trying to interpret as filesystem");
			try {
				this.xmlSource = new File(xmlPath).toURI().toURL();
				System.out.println("files source "+this.xmlSource.toString());
				this.templateBasePath=xmlSource.toURI().resolve(".").toURL();	
				this.readXML("local");
				System.out.println(templateBasePath);
			} catch (Exception e2) {
				log.fatal("giving up.", e2);
				
			}
		}
		

		
	}

	
//	public void generateXMLCoordinator(URL xmlPath){
//		this.xmlSource = xmlPath;
//		this.readXML();
//
//	}
	
	
	/**
	 *
	 * @return {@link TemplateDataModel} object. Null if method {@link #generateXMLCoordinator(String)} isn't called
	 */
	public TemplateDataModel getXmlCoordinatorDataModel() {
		return this.javaXMLModel;
	}

	/**
	 * generate from XML file an {@link TemplateDataModel} object representing XML file into Java code
	 * @param xmlSource {@link String}  representing the location from XML file to load
	 */
	private void readXML(String src)  {
		
		Element t;
		this.author = new Author();
		try {

			this.javaXMLModel = new TemplateDataModel();
			 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
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

==== BASE ====
	         this.javaXMLModel.setBaseTemplatePath(this.getTemplateBasePath());

==== BASE ====
		}catch ( ParserConfigurationException | IOException | SAXException a) {
			log.fatal("error" , a);
			this.javaXMLModel = null;
		}

	}

	public URL getParentTemplateDir() throws MalformedURLException, URISyntaxException {
		 return xmlSource.toURI().resolve("..").toURL();
	 }

==== BASE ====
==== BASE ====
	private void readWebTemplate( URL url) {
==== BASE ====
==== BASE ====
		String tmpDirStr = System.getProperty("java.io.tmpdir");
		System.out.println(" tmpDirStr "+tmpDirStr);
		try {
==== BASE ====
		System.out.println("url parent path "+url.toURI().resolve("."));
		
		URI aux;
		aux=url.toURI().resolve(".");
		doc = Jsoup.connect(aux.toString()).get();
		
		t =  doc.select("a[href*=.vm]");

		t.stream().forEach(f->{
			try {
				this.arrayOfNames.add(f.text());
				this.arrayOfSites.add(new URL(url.toURI().resolve(".")+f.text()));
			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
==== BASE ====
		
				t.stream().forEach(f->{
					try {
						this.arrayOfNames.add(f.text());
						this.arrayOfSites.add(new URL(url.toURI().resolve(".")+f.text()));
					} catch (MalformedURLException | URISyntaxException e) {
						e.printStackTrace();
					}
				});
				
				StringBuilder sb = new StringBuilder();
				BufferedWriter bufferWriter=null;
		 	
				for (URL x : arrayOfSites) {
					
					BufferedReader in = new BufferedReader(new InputStreamReader(x.openStream()));
			        String inputLine="";
			        while ((inputLine = in.readLine()) != null)
			       	 	sb.append(inputLine+"\n");
			        in.close();
			        
			        //templatesTempDir = File.createTempFile(this.arrayOfNames.get(this.arrayOfSites.indexOf(x)), null);
			        templatesTempDir = new File(tmpDirStr);
			        System.out.println("tempFiles "+templatesTempDir.getPath());
			        inputLine="";
			        File fileToWrite = new File(templatesTempDir.getPath()+"/"+this.arrayOfNames.get(this.arrayOfSites.indexOf(x)));
			        
			        System.out.println("fitetowrite "+fileToWrite.getPath());
			        
		            bufferWriter= new BufferedWriter(new FileWriter(fileToWrite,true));
		            bufferWriter.write(sb.toString());
		            bufferWriter.write("\n");
		            bufferWriter.close();
		            
				}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String getTemplateBasePath() {
		return templateBasePath.getPath();
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

==== BASE ====
	public String getTemplateBasePath() {
		return templateBasePath.getPath();
	}
==== BASE ====

}
