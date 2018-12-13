package es.upm.tfo.lst.CodeGenerator.xmlparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
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
	private File xmlFile;
	private String xmlPath;
	private NodeList nodeVariable, nodeMacro,templateName, templateVersion,templateDescription,templateAuthor;
	private Map<String,Variable> variableList;
	private List<MacroModel> macroList;
	private TemplateDataModel javaXMLModel = null;
	private Author author;

	public XmlParser() {
			this.variableList = new HashMap<>();
			this.macroList = new ArrayList<>();

		}

	/**
	 *generate XmlCoordinator object who represent XML file into java code
	 * @param xmlPath {@link String } path to XML file
	 */
	public void generateXMLCoordinator(String xmlPath){
		URL url;
		try {
			url = new URL(xmlPath);
		}catch (Exception e) {
			
		}
		this.readXML(xmlPath);
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
	 * @param xmlPath {@link String}  representing the location from XML file to load
	 */
	private void readXML(String xmlPath)  {
		this.xmlPath=xmlPath;
		Element t;
		this.author = new Author();
		try {

			this.javaXMLModel = new TemplateDataModel();
			this.xmlFile=new File(this.xmlPath);
			 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	         Document doc = docBuilder.parse (xmlFile);
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
	         //mdifi definition of array pf variables
	         this.javaXMLModel.setVars(this.variableList);
	         
	         this.javaXMLModel.setAuthor(this.author);


		}catch ( ParserConfigurationException | IOException | SAXException a) {
			log.fatal("error" , a);
			this.javaXMLModel = null;
		}



	}
	
	/**
	 * method to get XML file readed of website
	 * @param url
	 * @throws Exception
	 */
	  private void generateFile(URL url) throws Exception{
		  	 StringBuilder sb = new StringBuilder();
		     //URI uri = new URI("http://localhost/templates/simple.xml");
	         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	         String inputLine;
	         while ((inputLine = in.readLine()) != null)
	        	 sb.append(inputLine+"\n");
	             System.out.println(inputLine);
	         in.close();
			BufferedWriter bw = null;
			FileWriter fw = null;
            fw = new FileWriter("target/example.xml");
			bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();

	    }

}
