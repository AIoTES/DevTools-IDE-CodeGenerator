package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * Class to tes xml
 * @author Buhid Eduardo
 *
 */
public class XmlTest {
	private  XmlParser parser;
	private TemplateDataModel model;
	//----constants
	private final String basePath="src/test/resources/";
	
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		
	}	
	/**
	 * Method to test an xml file with incorrect schema
	 */
	@Test
	public void testBadSchema() {
		System.out.println("\n------------------------------bad schema XML --------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"template-XMLbadSchema/badSchema.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNull(this.model);
	}
	
	/**
	 * Test with XML placed in wrong directory
	 */
	@Test
	public void testInexistentXML() {
		System.out.println("\n------------------------------inexistent XML--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"template-XMLinexistent");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNull(this.model);
	}
	
	/**
	 * Test with xml with correct schema
	 */
	@Test
	public void testWorkingXML() {
		System.out.println("\n------------------------------working XML--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"template-XMLwithoutMacros/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		assertNotNull(this.model);
	}
	
}

