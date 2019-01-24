/*******************************************************************************
 * Copyright 2019 Universidad Politécnica de Madrid UPM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package es.upm.tfo.lst.CodeGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
import uk.ac.manchester.cs.jfact.JFactFactory;

/**
 * @author amedrano
 *
 */
public class DeveloperTests {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	//----constants
	private final String templateBasePath="src/test/resources/template-complex/";
	private final String webTemplatePath="http://localhost/template/complexXml.xml";
	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String sql="src/test/resources/template/SQL/sql.vm";
	private final String sqlCoordinator="src/test/resources/SQL/coordinator.xml";
	private final String sqlOutput="src/test/resources/SQL/output/";
	private final String baseOutput="target/completeTest/";
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
	}
	@Test
	public void classAccessExample() {
		OWLClass cls;
	}

	@Test
	public void ontologyAccessExample() {
		OWLOntology ontology;
		OWLClass c;
		OWLNamedIndividual v;
		
	}

	@Test
	public void sqltest() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
		 OWLReasonerFactory reasonerFactory= new JFactFactory();
;
		 OWLOntology t = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");
		 Set <OWLDataProperty> m=new HashSet<>();

		 OWLReasoner reasoner = reasonerFactory.createReasoner(t);
		 for (OWLClass g  : t.getClassesInSignature()) {
			 	System.out.println(reasoner.getInstances(g, true).getNodes());
			 	for(OWLNamedIndividual ni : reasoner.getInstances(g, true).getFlattened()) {
			 		//System.out.println(ni);
			 	}
			 	
		 }
//		
		 
//		for ( OWLDataPropertyDomainAxiom g :  t.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
//			System.out.println("OWLDataPropertyDomainAxiom "+g.getProperty());
//		}
		
//		for (OWLDataProperty b : m) {
//			System.out.println(b);
//		}
//		 
		 try {
				//get instance of TemplateDataModel,giving to method the local file path or URL of the xml location
				this.model=this.parser.generateXMLCoordinator(this.sqlCoordinator);
				//set XML model to generate project 
				this.genPro.setMainModel(this.model);
				//set the ontology to project and recursive state
				this.genPro.addOntology(this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl"), true);
				//set output directory
				this.genPro.setOutputFolder(this.sqlOutput);
				//add value to variables		
				
				genPro.process();
				assertTrue(genPro.getErrors().isEmpty());
				
		} catch (Exception e) {
			// TODO: handle exception
		}
	

	}


}
