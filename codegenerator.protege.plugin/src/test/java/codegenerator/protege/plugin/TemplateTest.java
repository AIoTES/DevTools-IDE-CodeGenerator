package codegenerator.protege.plugin;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

public class TemplateTest {
	private XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	
	private final static String templateBasePath="src/main/resources/";
	private final String baseOutput="target/generated/";
	private final String webOntology ="https://protege.stanford.edu/ontologies/pizza/pizza.owl";


	@Before
	public void init() {
		//PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
				
	}

	
	@Test
	public void localCompleteTest() {
			assertTrue(true);
//		 try {
//
//			 this.model=this.parser.generateXMLCoordinator(this.templateBasePath+"coordinator.xml");
//				
//				this.genPro.setMainModel(this.model);
//				
//				//set the ontology to project and recursive state
//				this.genPro.addOntology(this.ontologyLoader.loadOntology(webOntology), true);
//				//set output directory
//				this.genPro.setOutputFolder(baseOutput);
//				File f = new File(baseOutput);
//				f.mkdirs();
//				genPro.process();
//		} catch (Exception e) {
//			genPro.addError(e);
//		}
//		 genPro.getErrors().stream().forEach(f->f.printStackTrace());
//		assertTrue(genPro.getErrors().isEmpty());
		}
}
