package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
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
	private final String ontologyBasePath="src/test/resources/ontologies/";
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		ontologyLoader = new OntologyLoader();
	}
	

	@Test
	public void test1() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
 
		 
		this.parser.generateXMLCoordinator(this.templateBasePath+"complexXml.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		//this.genPro = new GenerateProject(this.model);
		this.genPro = new GenerateProject();
		//set XML model to generate project 
		this.genPro.setMainModel(this.model);
		//set the ontology to project and recursive state
		this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl"), true);
		//set diectory path to load all template needed files
		this.genPro.setLocalBaseLoaderPath(parser.getTemplateBasePath().getPath());
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


}
