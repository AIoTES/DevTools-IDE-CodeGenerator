package es.upm.tfo.lst.CodeGenerator;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variables;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
/**
 * 
 * This class test working templates, working xml and working ontologies.
 * In this case the program uses a more complex templates, and shows 
 * one if variant to use xml (user can put velocity language to process dinamically the output
 * file name and directory )  
 * 
 * @author Buhid Eduardo
 *
 */
public class ComplexTest {
	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLOntology ontology;
	//----constants
	private final String basePath="src/test/resources/complex-test/";
	private final String baseOutputPath="target/complex-test/";
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		ontologyLoader = new OntologyLoader();	
	} 
	@Test
	public void test1() {
		 System.out.println("\n------------------------------complex test--------------------------------------\n");

		 try {
			 File f = new File(this.baseOutputPath);
			 if(!f.exists()) f.mkdirs();
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml/complexXml.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontologies/universidad.owl");
		this.genPro.addOntology(this.ontology, true);
		
		this.genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder1"));//required
		
		this.genPro.setVariable( new Variables("cardinality", "false","/exampleFolder4"));//optional
		this.genPro.setVariable( new Variables("templateCount", "false","/exampleFolder5"));//optional
		this.genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder6"));//optional
		
		this.genPro.setOutputFolder(this.baseOutputPath);
		this.genPro.setLocalBaseLoaderPath(this.basePath+"templates/");
		
		assertTrue(genPro.process());
	}
	
}
