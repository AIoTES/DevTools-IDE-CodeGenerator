package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

public class CompleteTest {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	//----constants
	private final String templateBasePath="src/test/resources/template-complex/";
	private final String webTemplatePath="http://localhost/template/complexXml.xml";
	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String sql="src/test/resources/template/SQL/sql.vm";
	private final String sqlCoordinator="src/test/resources/SQL/coordinator.xml";
	private final String sqlOutput="src/test/resources/SQL/output/";
	private final String baseOutput="target/completeTest/";
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
	}
	

	@Test
	public void localCompleteTest() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
		 
		//get instance of TemplateDataModel,giving to method the local file path or URL of the xml location
		this.model=this.parser.generateXMLCoordinator(this.templateBasePath+"complexXml.xml");
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
		
	
	
		
		try{
			File f = new File(baseOutput);
			//File f = new File(baseOutput+"SQL/");
			f.mkdirs();
		}catch(Exception a) {
			a.printStackTrace();
		}

		try{
			assertTrue(genPro.process());
		}catch(Exception a) {
			a.printStackTrace();
		}

	}
	
	@Test
	public void webTemplateTest() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
 		 
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
		
		//creating output dir in test 
		try{
			File f = new File("target/completeTest/");
			f.mkdirs();
		}catch(Exception a) {
			a.printStackTrace();
		}
		//this.genPro.setLocalBaseLoaderPath(this.templateBasePath);
		try{
			assertTrue(genPro.process());
		}catch(Exception a) {
			a.printStackTrace();
		}

	}

	@Test
	public void webTemplateCompleteTest() {
		//http://localhost/template/complexXml.xml
		
		 System.out.println("\n------------------------------online template--------------------------------------\n");

		this.model=this.parser.generateXMLCoordinator("http://localhost/template/complexXml.xml");
		this.genPro = new GenerateProject();
		//set XML model to generate project 
		this.genPro.setMainModel(this.model);
		//set the ontology to project and recursive state
		this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl"), true);
		//set diectory path to load all template needed files
		//this.genPro.setLocalBaseLoaderPath(parser.getTemplateBasePath());
		//set output directory
		this.genPro.setOutputFolder("target/completeTest/");
		//creating output dir in test 
		try{
			File f = new File("target/completeTest/remoteCompleteTest");
			f.mkdirs();
		}catch(Exception a) {
			a.printStackTrace();
		}
		try{
			assertTrue(genPro.process());
		}catch(Exception a) {
			a.printStackTrace();
		}

	}

}
