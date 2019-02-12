package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.runtime.log.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationForwardCallback;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.omg.CORBA.portable.InputStream;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.NotSyntaxChecker;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
//import es.upm.tfo.lst.codegenerator.plugin.rest.GenerateServlet;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class CompleteTest  {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	//----constants
	private final String templateBasePath="src/test/resources/template-complex/";
	private final String webTemplatePath="http://localhost:7755/template/complexXml.xml";
	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String sql="src/test/resources/template/SQL/sql.vm";
	private final String webOntology ="https://protege.stanford.edu/ontologies/pizza/pizza.owl";
	private final String baseOutput="target/completeTest/";
	private ClientAndServer mockServer;
	//StringBuilder sb = new StringBuilder();
	private MockServerClient client;

//	@Before
//	public void init() {
//		
//		
//	}
	
	@Before
	public  void startMockServer() throws IOException {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
//		FileInputStream fis = new FileInputStream(new File(templateBasePath+"complexXml.xml"));
//		 BufferedReader br = null;
//			br = new BufferedReader(new InputStreamReader(fis));
////			StringBuilder sb = new StringBuilder();
//			String line;
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//			}	

	    this.mockServer = ClientAndServer.startClientAndServer(7755);
	   client = new MockServerClient("localhost",7755);
	   client.when(HttpRequest.request()
		          .withMethod("GET")
		          .withPath("/template/complexXml.xml"))
		      .respond(HttpResponse.response()
		          .withStatusCode(200)
		          .withHeaders(new Header("Content-Type", "text/plain"))
		          .withBody(this.readFile("complexXml.xml"))
		      );
	   
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/project.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("project.vm"))
//		      );
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/ontology.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("ontology.vm"))
//		      );
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/classes.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("classes.vm"))
//		      );
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/classes2.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("classes2.vm"))
//		      );
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/enumerations.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("enumerations.vm"))
//		      );
//	   client.when(HttpRequest.request()
//		          .withMethod("GET")
//		          .withPath("/template/instances.vm"))
//		      .respond(HttpResponse.response()
//		          .withStatusCode(200)
//		          .withHeaders(new Header("Content-Type", "text/plain"))
//		          .withBody(this.readFile("instances.vm"))
//		      );
	    

	}


	@Test
	public void localCompleteTest() {
		
		System.out.println(this.mockServer.isRunning());
		
		//System.out.println(this.mockServer.);
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
		 try {
			     //get instance of TemplateDataModel,giving to method the local file path or URL of the xml location
				this.model=this.parser.generateXMLCoordinator(this.templateBasePath+"complexXml.xml");
			 	//this.model=this.parser.generateXMLCoordinator(null);
				//set XML model to generate project 
				this.genPro.setMainModel(this.model);
				//set the ontology to project and recursive state
				this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl"), true);
				//set output directory
				this.genPro.setOutputFolder("target/completeTest/");
				//add value to variables
				this.genPro.setVariable("targetOperatingSystem","Linux");
				this.genPro.setVariable( "cardinality", "2");
				this.genPro.setVariable( "templateCount", "2");
				this.genPro.setVariable( "ontologyCount", "88");
				File f = new File(baseOutput);
				f.mkdirs();
				genPro.process();
		} catch (Exception e) {
			genPro.addError(e);
		}
		
		assertTrue(genPro.getErrors().isEmpty());
		}

	
	
	
	@Test
	public void webTemplateTest() throws IOException {
		 System.out.println("\n------------------------------web template with local ontology--------------------------------------\n");
		 System.out.println("mock server is runnung? "+this.mockServer.isRunning());
//		    MockServerClient client = new MockServerClient("localhost",7755);
//			   client.when(HttpRequest.request()
//				          .withMethod("GET")
//				          .withPath("/template/complexXml.xml"))
//				      .respond(HttpResponse.response()
//				          .withStatusCode(200)
//				          .withHeaders(new Header("Content-Type", "text/plain"))
//				          .withBody(sb.toString())
//				      );
		try{
			 	this.model=this.parser.generateXMLCoordinator(webTemplatePath);
				//this.model = this.parser.getXmlCoordinatorDataModel();
				//this.genPro = new GenerateProject(this.model);
				this.genPro = new GenerateProject();
				//set XML model to generate project 
				this.genPro.setMainModel(this.model);
				//set the ontology to project and recursive state
				this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl"), true);
				//set diectory path to load all template needed files
				//this.genPro.setLocalBaseLoaderPath(parser.getTemplateBasePath());
				//set output directory
				this.genPro.setOutputFolder("target/completeTest/");
				//add value to variables
				this.genPro.setVariable("outputBaseDir","/exampleFolder1");
				this.genPro.setVariable( "cardinality", "2");
				this.genPro.setVariable( "templateCount", "2");
				this.genPro.setVariable( "ontologyCount", "88");

			File f = new File("target/completeTest/");
			f.mkdirs();
			this.genPro.process();

		}catch(Exception a) {
			genPro.addError(a);
			a.printStackTrace();
		}
		 
		assertTrue(genPro.getErrors().isEmpty());

	}

	@Test
	public void webTemplateCompleteTest() throws IOException {
		
		 System.out.println("\n------------------------------online template and ontology--------------------------------------\n");
		 System.out.println("mock server is runnung? "+this.mockServer.isRunning());
//		 String line;
//		    MockServerClient client = new MockServerClient("localhost",7755);
//			   client.when(HttpRequest.request()
//				          .withMethod("GET")
//				          .withPath("/template/complexXml.xml"))
//				      .respond(HttpResponse.response()
//				          .withStatusCode(200)
//				          .withHeaders(new Header("Content-Type", "text/plain"))
//				          .withBody(sb.toString())
//				      );
		try{
			this.model=this.parser.generateXMLCoordinator(webTemplatePath);
			//this.model=this.parser.generateXMLCoordinator("http://localhost/template/complexXml.xml");
			this.genPro = new GenerateProject();
			//set XML model to generate project 
			this.genPro.setMainModel(this.model);
			//set the ontology to project and recursive state
			this.genPro.addOntology(this.ontologyLoader.loadOntology(this.webOntology),false);
			//set diectory path to load all template needed files
			//this.genPro.setLocalBaseLoaderPath(parser.getTemplateBasePath());
			//set output directory
			this.genPro.setOutputFolder("target/completeTest/");
			//creating output dir in test 
			File f = new File("target/completeTest/remoteCompleteTest");
			f.mkdirs();
			this.genPro.process();

		}catch(Exception a) {
			a.printStackTrace();
			this.genPro.addError(a);
		}
		
		assertTrue(genPro.getErrors().isEmpty());
		

	}

    
	
	@After
	public void stopMockServer() {
		System.out.println("stopping mock server...");
	    mockServer.stop();
	}
	
	private String readFile(String nameFile) throws IOException {
		StringBuilder sb=new StringBuilder();
		FileInputStream fis = new FileInputStream(new File(templateBasePath+nameFile));
		 BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}	
			fis.close();
			br.close();
			return sb.toString();
	}
}
