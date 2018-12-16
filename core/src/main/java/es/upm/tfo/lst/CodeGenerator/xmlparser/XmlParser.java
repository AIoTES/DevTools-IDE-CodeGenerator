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
 * Class who read XML file and parse it to Java code
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class XmlParser {

	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private URL xmlSource;
	private NodeList nodeVariable, nodeMacro,templateName, templateVersion,templateDescription,templateAuthor;
	private Map<String,Variable> variableList;
	private List<MacroModel> macroList;
	private TemplateDataModel javaXMLModel = null;
	private Author author;
	//---------------
	private org.jsoup.nodes.Document doc; 
	private URI uri;
	private Elements t;
	private String output="";
	private File fileToWrite;
	private List<URL> arrayOfSites = new ArrayList<>();
	private List<String> arrayOfNames = new ArrayList<>();
	
	

	public XmlParser() {
			this.variableList = new HashMap<>();
			this.macroList = new ArrayList<>();

		}

	/**
	 *generate XmlCoordinator object who represent XML file into java code
	 * @param xmlPath {@link String } path to XML file
	 */
	public void generateXMLCoordinator(String xmlPath){
		try {

			this.xmlSource =  new URL(xmlPath);
			System.out.println(this.xmlSource);
			this.readWebTemplate(this.xmlSource);
		}catch (Exception e) {
			log.warn("given URL ist valid, trying to interpret as filesystem");
			try {
				this.xmlSource = new File(xmlPath).toURI().toURL();
				
			} catch (Exception e2) {
				log.fatal("giving up.", e2);
				
			}
		}
		this.readXML();
	}

	
	public void generateXMLCoordinator(URL xmlPath){
		this.xmlSource = xmlPath;
		this.readXML();
		//readWebTemplate();
	}
	
	
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
	private void readXML()  {
		
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
	         this.author.setEmail(t.getElementsByTagName("author-email").item(0).getTextContent());
	         this.author.setPhone(t.getElementsByTagName("author-phone").item(0).getTextContent());


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


		}catch ( ParserConfigurationException | IOException | SAXException a) {
			log.fatal("error" , a);
			this.javaXMLModel = null;
		}

	}

	public URL getParentTemplateDir() throws MalformedURLException, URISyntaxException {
		 return xmlSource.toURI().resolve("..").toURL();
	 }

	private void readWebTemplate( URL url) {
		try {
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
		
		StringBuilder sb = new StringBuilder();
		BufferedWriter bufferWriter=null;
 	
		for (URL x : arrayOfSites) {
			System.out.println("array of sites "+x);
			BufferedReader in = new BufferedReader(new InputStreamReader(x.openStream()));
	        String inputLine="";
	        while ((inputLine = in.readLine()) != null)
	       	 	sb.append(inputLine+"\n");
	        in.close();
	        
	        inputLine="";
	        File fileToWrite = new File(output,this.arrayOfNames.get(this.arrayOfSites.indexOf(x)));
	        System.out.println("fitetowrite "+fileToWrite.getPath());
	        
            bufferWriter= new BufferedWriter(new FileWriter(fileToWrite,true));
            bufferWriter.write(sb.toString());
            bufferWriter.write("\n");
            bufferWriter.close();
            
		}
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		
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

	

}
