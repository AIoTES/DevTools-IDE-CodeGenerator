package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

public class JsonContextTest {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	
	private final String templatePath="src/test/resources/context/jsonld.vm";
	private final String oordinatorPath="src/test/resources/context/coordinator.xml";
	private final String ontologyPath="src/test/resources/ontologies/universaal.owl";
	private final String outputPath="target/generatedContext/";
	
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
	}
	@Test
	public void jsonContextTest(){
		 try {
			//get instance of TemplateDataModel,giving to method the local file path or URL of the xml location
				this.model=this.parser.generateXMLCoordinator(this.oordinatorPath);
				//set XML model to generate project 
				this.genPro.setMainModel(this.model);
				//set the ontology to project and recursive state
				this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyPath), false);
				//set output directory
				this.genPro.setOutputFolder(outputPath);
				genPro.process();
		} catch (Exception e) {
			genPro.addError(e);
			//System.out.println(e.getMessage());
		}

		assertTrue(genPro.getErrors().isEmpty());
		
	
		
		}
	}

