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

	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLOntology ontology;
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
		 System.out.println("\n------------------------------complex test--------------------------------------\n");


		this.parser.generateXMLCoordinator(this.templateBasePath+"complexXml.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");
		this.genPro.addOntology(this.ontology, true);

		this.genPro.setVariable("outputBaseDir","/exampleFolder1");//required

		this.genPro.setVariable( "cardinality", "/exampleFolder4");//optional
		this.genPro.setVariable( "templateCount", "/exampleFolder5");//optional
		this.genPro.setVariable( "ontologyCount", "/exampleFolder6");//optional

		try{
			File f = new File("target/completeTest/");
			f.mkdirs();
		}catch(Exception a) {
			a.printStackTrace();
		}

		this.genPro.setOutputFolder("target/completeTest/");
		this.genPro.setLocalBaseLoaderPath(this.templateBasePath);
		try{
			assertTrue(genPro.process());
		}catch(Exception a) {
			a.printStackTrace();
		}

	}


}
