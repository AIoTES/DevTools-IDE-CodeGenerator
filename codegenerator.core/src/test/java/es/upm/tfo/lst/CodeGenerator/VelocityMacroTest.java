package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
 * Class to test velocity macros
 * @author edu
 *
 */
public class VelocityMacroTest {

	private  XmlParser parser;
	private TemplateDataModel model;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLOntology ontology;

	//----constants

	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String templatesBasePath="src/test/resources/";
	private final String templatesWithErrors="src/test/resources/template-macroWithErrors/";
	private final String existentOnlineOnt="https://protege.stanford.edu/ontologies/pizza/pizza.owl";
	private final String inexistentOnlineOnt="https://www.protege.stanford.edu/ontologies/pizza/pizza.owl";


	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		parser = new XmlParser();
		ontologyLoader = new OntologyLoader();
	}



	/**
	 * Test templates with code errors located in src/test/simpleTest/templates-test/templatesWithErrors/templates/
	 */
	@Test
	public void templateWithErrors(){
		System.out.println("\n------------------------------templates with errors --------------------------------------\n");

		try {
			File f = new File("target/simple-test/templates-Test");
			if(!f.exists()) f.mkdirs();
		}catch(Exception a) {

		}
		this.model= this.parser.generateXMLCoordinator(this.templatesWithErrors+"workingXML.xml");
		this.genPro = new GenerateProject(this.model);
		this.ontology = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");
		this.genPro.addOntology(this.ontology,false);
		//adding variables
		genPro.setVariable("outputBaseDir","/exampleFolder");//required
		genPro.setVariable("cardinality","/exampleFolder");//optional
		genPro.setVariable("templateCount","/exampleFolder");//optional
		genPro.setVariable( "ontologyCount","/exampleFolder");//optional

		String out="target/ontology-test";
		try {
			File f = new File(out);
			if(!f.exists()) {
				boolean g = f.mkdirs();
				System.out.println(g);
			}
		}catch(Exception a){
			//a.printStackTrace();
		}

		//genPro.setLocalBaseLoaderPath(this.templatesWithErrors);
		genPro.setOutputFolder(out);

		try {
			assertFalse(genPro.process());
		}catch(Exception e) {
			e.printStackTrace();
		}


	}


}
