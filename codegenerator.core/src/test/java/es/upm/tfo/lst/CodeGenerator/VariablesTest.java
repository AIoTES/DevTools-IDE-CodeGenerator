package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * Class to test variables given in xml file
 *
 * @author Buhid Eduardo
 *
 */
public class VariablesTest {

	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	//----constants
	private final String basePath="src/test/resources/";

	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();

	}


	/**
	 * Method to test when user miss some required variables. The relevant variables to this method is only required
	 */
	@Test
	public void testMissingVariables() {
		System.out.println("\n------------------------------missing variables--------------------------------------\n");
		parser.generateXMLCoordinator(this.basePath+"template-XMLwithoutMacros/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);

		//adding variables

		//genPro.setVariable("outputBaseDir","/exampleFolder");//required
		genPro.setVariable("cardinality","/exampleFolder");//optional
		genPro.setVariable("templateCount","/exampleFolder");//optional
		genPro.setVariable( "ontologyCount","/exampleFolder");//optional
		assertTrue(this.genPro.getVariablesArray().keySet().containsAll(this.model.getArrayVars().keySet()));

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
		parser.generateXMLCoordinator(this.basePath+"template-XMLwithoutMacros/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		System.out.println(model!=null);
		this.genPro = new GenerateProject(this.model);

		genPro.setVariable( "INEXISTENT","/exampleFolder");//inexistent
		genPro.setVariable( "ANOTHER INEXISTENT","/exampleFolder");//inexistent
		genPro.setVariable( "cardinality","/exampleFolder");//optional
		genPro.setVariable( "templateCount","/exampleFolder");//optional
		genPro.setVariable( "ontologyCount","/exampleFolder");//optional
		//this.model.getRequiredVariables().stream().forEach(b->System.out.println(b.getName()));
		for (String t  : genPro.getVariablesArray().keySet()) {
			System.out.println(t);
		}
		System.out.println("--------------------");
		for (String t  : this.model.getArrayVars().keySet()) {
			System.out.println(t);
		}
		assertTrue(this.genPro.getVariablesArray().keySet().containsAll(this.model.getArrayVars().keySet()));
	}

	/**
	 * Method to test when user add all variables correctly. If user try to include variables undefined in XML file,
	 * the tool don't add it
	 */
	@Test
	public void testCorrectVariables() {
		 System.out.println("\n------------------------------Correct variables--------------------------------------\n");
		this.parser.generateXMLCoordinator(this.basePath+"template-XMLwithoutMacros/workingXML.xml");
		this.model=parser.getXmlCoordinatorDataModel();
		this.genPro = new GenerateProject(this.model);


		//adding variables
		genPro.setVariable("outputBaseDir","/exampleFolder");//required
		genPro.setVariable("cardinality","/exampleFolder");//optional
		genPro.setVariable("templateCount","/exampleFolder");//optional
		genPro.setVariable( "ontologyCount","/exampleFolder");//optional
		assertTrue(this.genPro.getVariablesArray().keySet().containsAll(this.model.getArrayVars().keySet()));

	}


}
