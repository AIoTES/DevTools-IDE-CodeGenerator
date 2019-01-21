package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * 	Class to test ontologies 
 * 
 * @author Buhid Eduardo
 *
 */
public class OntologyTest {
	
	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLOntology ontology;
	//----constants
	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String templatesBasePath="src/test/resources/template-simple/";
	private final String existentOnlineOnt="https://protege.stanford.edu/ontologies/pizza/pizza.owl";
	private final String inexistentOnlineOnt="https://www.protege.stanford.edu/ontologies/pizza/pizza.owl";


	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		ontologyLoader = new OntologyLoader();
	}
	/**
	 * Method to test when try to load a missing ontology file
	 *
	 */
	@Test
	public void ontologyInconsistemtImports() {
		 System.out.println("\n------------------------------inconsistent ontology--------------------------------------\n");
		 try {
			 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
				
				this.genPro = new GenerateProject(this.model);
				this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad-inconsistentImports.owl");
				assertNull(this.ontology);
		} catch (Exception e) {
			// TODO: handle exception
		}
//		 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
//		
//		this.genPro = new GenerateProject(this.model);
//		this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad-inconsistentImports.owl");
//		assertNull(this.ontology);

	}
	/**
	 * Method to test when try to load a missing ontology file
	 *
	 */
	@Test
	public void ontologyFileMissingImports() {
		 System.out.println("\n------------------------------ontology missing imports--------------------------------------\n");
		 try {
			 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
				
				assertNotNull(this.model);
				this.genPro = new GenerateProject(this.model);

				this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"ontologyFileMissingImports/universidad.owl");

				assertNull(this.ontology);
		} catch (Exception e) {
			// TODO: handle exception
		}
//		 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
//		
//		assertNotNull(this.model);
//		this.genPro = new GenerateProject(this.model);
//
//		this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"ontologyFileMissingImports/universidad.owl");
//
//		assertNull(this.ontology);

	}
	/**
	 * Method to test ontology loaded from invalid URL
	 */
	@Test

	public void InexistentOnlineOntology() {
		 System.out.println("\n------------------------------ontology online inexistent--------------------------------------\n");
		 try {
			 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
				
				assertNotNull(this.model);
				this.genPro = new GenerateProject(this.model);
				this.ontology = this.ontologyLoader.loadOntology(this.inexistentOnlineOnt);
				assertNull(this.ontology);
		} catch (Exception e) {
			// TODO: handle exception
		}
//		 this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
//		
//		assertNotNull(this.model);
//		this.genPro = new GenerateProject(this.model);
//		this.ontology = this.ontologyLoader.loadOntology(this.inexistentOnlineOnt);
//		assertNull(this.ontology);
	}

	/**
	 * Method to test ontology loaded from valid URL
	 */
	@Test
	public void existentOnlinetOntology() {
		System.out.println("\n------------------------------ontology online existent--------------------------------------\n");
		
		try {
			this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
			
			assertNotNull(this.model);
			this.genPro = new GenerateProject(this.model);
			this.ontology = this.ontologyLoader.loadOntology(this.existentOnlineOnt);
			System.out.println(ontology.getOntologyID().getOntologyIRI().get().getShortForm().toString());
			assertNotNull(this.ontology);
		} catch (Exception e) {
			// TODO: handle exception
		}
//		this.model= this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
//		
//		assertNotNull(this.model);
//		this.genPro = new GenerateProject(this.model);
//		this.ontology = this.ontologyLoader.loadOntology(this.existentOnlineOnt);
//		System.out.println(ontology.getOntologyID().getOntologyIRI().get().getShortForm().toString());
//		assertNotNull(this.ontology);
	}
	
	/**
	 * Test with existent and working ontology
	 */
	@Test
	public void existentLocalOntology() {
		 System.out.println("\n------------------------------ontology local existent--------------------------------------\n");

		 try {
			 this.model=  this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
				
				this.genPro = new GenerateProject(this.model);
				this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");

				assertNotNull(this.ontology);

		 } catch (Exception e) {
			// TODO: handle exception
		}
//		 this.model=  this.parser.generateXMLCoordinator(this.templatesBasePath+"simple.xml");
//	
//		this.genPro = new GenerateProject(this.model);
//		this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");
//
//		assertNotNull(this.ontology);

	}
	
	
	
	
	
	
	
}
