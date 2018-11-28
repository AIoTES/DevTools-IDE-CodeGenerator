package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variables;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
/**
 * Class to test all cases of xml.
 * <br> this class test:<br>
 * <ul>
 * 	<li>xml schema whos definition is incorect</li>
 *  <li>xml placed in wrong path </li>
 *  <li>xml referenced correctly and with correct schema</li>
 *  <li>variables missing</li>
 *  <li>inexistent variables</li>
 *  <li>correct variables</li>
 *  <li>ontology with missing imports</li>
 *  <li>ontology with broken file imports</li>
 *  <li>inexistent online ontology</li>
 *  <li>existent online ontology</li>
 *  <li>existent local ontology</li>
 *  <li>velocity macros with errors</li>
 *  <li>working templates plus working ontologies</li>
 * </ul>
 * @author Buhid Eduardo
 *
 */
public class SimpleTest {
	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLOntology ontology;
	//----constants
	private final String basePath="src/test/simpleTest/";
	private final String existentOnlineOnt="https://protege.stanford.edu/ontologies/pizza/pizza.owl";
	private final String inexistentOnlineOnt="https://www.protege.stanford.edu/ontologies/pizza/pizza.owl";
	
	
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		ontologyLoader = new OntologyLoader();	
	}
	//----------------------XML tests-------------------------
	/**
	 * Test with xml file with a wrong schema definition
	 */
	
	@Test
	public void testBadSchema() {
		System.out.println("\n------------------------------bad schema XML --------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"xml-test/badSchemaXML/badSchema.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNull(this.model);
	}
	
	/**
	 * Test with XML placed in wrong directory 
	 */
	@Test
	public void testInexistentXML() {
		System.out.println("\n------------------------------inexistent XML--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"xml-test/inexistentXML/exampleXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNull(this.model);
	}
	
	/**
	 * Test with xml with correct schema 
	 */
	@Test
	public void testWorkingXML() {
		System.out.println("\n------------------------------working XML--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
	}
	
	
	//----------------------variables tests-------------------------
	
	/**
	 * Method to test when user miss some required variables. The relevant variables to this method is only required
	 */
	@Test
	public void testMissingVariables() {
		System.out.println("\n------------------------------missing variables--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
	
		//adding variables
		//genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder"));//required
		genPro.setVariable( new Variables("cardinality", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("templateCount", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder"));//optional
		this.model.getRequiredVariables().size(); 
		Set<Variables> aux =this.genPro.getVariablesArray().stream().filter(h->h.getRequired().equals("true")).collect(Collectors.toSet()); 
		//System.out.println();
		assertFalse(this.model.getRequiredVariables().size()==aux.size()  );
	}
	/**
	 * 
	 * Method to test when user try to add variables undefined in xml file.
	 * If the variable isn't exist in xml file, the tool don't add it
	 * 
	 */
	@Test
	public void testInexistentVariables() {
		 System.out.println("\n------------------------------inexistent variables--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
	
		//adding variables
		//genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder"));//required
		genPro.setVariable( new Variables("INEXISTENT","true" ,"/exampleFolder"));//inexistent
		genPro.setVariable( new Variables("ANOTHER INEXISTENT","true" ,"/exampleFolder"));//inexistent
		genPro.setVariable( new Variables("cardinality", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("templateCount", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder"));//optional
		//this.model.getRequiredVariables().stream().forEach(b->System.out.println(b.getName())); 
		Set<Variables> aux =this.genPro.
										getVariablesArray().
										stream().
										filter(h->h.getRequired().equals("true")).
										collect(Collectors.toSet()); 
		assertFalse(this.model.getRequiredVariables().size()==aux.size()  );
		
	}

	/**
	 * Method to test when user add all variables correctly. If user try to include variables undefined in XML file,
	 * the tool don't add it
	 */
	@Test
	public void testCorrectVariables() {
		 System.out.println("\n------------------------------Correct variables--------------------------------------\n");
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
	
	
		//adding variables
		genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder"));//required
		genPro.setVariable( new Variables("INEXISTENT","true" ,"/exampleFolder"));//inexistent
		genPro.setVariable( new Variables("ANOTHER INEXISTENT","true" ,"/exampleFolder"));//inexistent
		genPro.setVariable( new Variables("cardinality", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("templateCount", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder"));//optional
		 
		Set<Variables> aux =this.genPro.
										getVariablesArray().
										stream().
										filter(h->h.getRequired().equals("true")).
										collect(Collectors.toSet()); 
		assertTrue(this.model.getRequiredVariables().size()==aux.size()  );
		
	}
	
	//----------------------otology tests-------------------------

	/**
	 * Method to test when try to load a missing ontology file
	 * 
	 */
	@Test
	public void ontologyFileMissingImports() {
		 System.out.println("\n------------------------------ontology missing imports--------------------------------------\n");
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
		this.genPro = new GenerateProject(this.model);
		
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontology-test/ontologyFileMissingImports/ontologies/universidad.owl");
		
		assertNull(this.ontology);

	}
	/**
	 * Test ontology importing a bad .owl file (edited with notepad and deleted some lines of text)
	 */
	
	@Test
	public void ontologyFileBrokenImports() {
		 System.out.println("\n------------------------------ontology file broken imports--------------------------------------\n");
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontology-test/ontBrokenImports/ontologies/universidad.owl");
		assertNull(this.ontology);
		
	}
	
	/**
	 * Method to test ontology loaded from invalid URL  
	 */
	@Test
	
	public void InexistentOnlineOntology() {
		 System.out.println("\n------------------------------ontology online inexistent--------------------------------------\n");
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.inexistentOnlineOnt);
		assertNull(this.ontology);
		
	}
	/**
	 * Method to test ontology loaded from valid URL  
	 */
	@Test
	public void existentOnlinetOntology() {
		System.out.println("\n------------------------------ontology online existent--------------------------------------\n");
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.existentOnlineOnt);
		assertNotNull(this.ontology);
		//assertNull(this.ontology);
		
	}
	
	/**
	 * Test with existent and workingontology
	 */
	@Test
	public void existentLocalOntology() {
		 System.out.println("\n------------------------------ontology local existent--------------------------------------\n");
		 
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontology-test/workingOntology/universidad.owl");
		
		assertNotNull(this.ontology);
		
	}
	
	//----------------------templates tests-------------------------
	/**
	 * Test templates with code errors located in src/test/simpleTest/templates-test/templatesWithErrors/templates/
	 */
	@Test
	public void templateWithErrors(){
		System.out.println("\n------------------------------templates with errors ###--------------------------------------\n");
				
		this.parser.generateXMLCoordinator(this.basePath+"templates-test/templatesWithErrors/xml/test.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontology-test/workingOntology/universidad.owl");
		this.genPro.addOntology(this.ontology,false);
		genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder"));//required
		genPro.setVariable( new Variables("cardinality", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("templateCount", "false","/exampleFolder"));//optional
		genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder"));//optional

		
		genPro.setLocalBaseLoaderPath(this.basePath+"templates-test/templatesWithErrors/templates/");
		genPro.setOutputFolder(this.basePath+"templatesTest/templatesWithErrors/target");
		
		assertFalse(genPro.process());
		
	}
	
	/**
	 * Method to test working ontologies, working templates.
	 * Directory from  applied templates: src/test/simpleTest/templates-test/workingTemplates/
	 * Output directory to view the resul of apply the templates: src/test/simpleTest/completeText/target
	 */
	@Test
	public void completeTest() {
		System.out.println("\n------------------------------completeTest----------------------------------\n");
		
		
		this.parser.generateXMLCoordinator(this.basePath+"xml-test/workingXML/workingXML.xml");
		this.model = this.parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.basePath+"ontology-test/workingOntology/universidad.owl");
		this.genPro.addOntology(this.ontology, true);
		assertNotNull(this.ontology);
		
		
		this.genPro.setVariable( new Variables("outputBaseDir","true" ,"/exampleFolder1"));//required
		
		this.genPro.setVariable( new Variables("cardinality", "false","/exampleFolder4"));//optional
		this.genPro.setVariable( new Variables("templateCount", "false","/exampleFolder5"));//optional
		this.genPro.setVariable( new Variables("ontologyCount", "false","/exampleFolder6"));//optional
		
		this.genPro.setOutputFolder(this.basePath+"completeTest/target/");
		this.genPro.setLocalBaseLoaderPath(this.basePath+"templates-test/workingTemplates/");
		
		assertTrue(genPro.process());
	}
	
	
	
}
