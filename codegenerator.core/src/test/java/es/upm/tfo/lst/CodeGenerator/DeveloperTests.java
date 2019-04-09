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

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * @author amedrano
 *
 */
public class DeveloperTests {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	private OWLReasoner reasoner=null;
	//----constants
	private final String templateBasePath="src/test/resources/template-complex/";
	private final String webTemplatePath="http://localhost/template/complexXml.xml";
	private final String ontologyBasePath="src/test/resources/ontologies/";
	private final String sql="src/test/resources/template/SQL/sql.vm";
	private final String sqlCoordinator="src/test/resources/SQL/coordinator.xml";
	private final String sqlOutput="src/test/resources/SQL/output/";
	private final String baseOutput="target/completeTest/";
	private String[] array;
	
	
	@Before
	public void init() {
		PropertyConfigurator.configure("src/test/resources/log4jConfigFile/log4j.properties");
		this.parser = new XmlParser();
		this.ontologyLoader = new OntologyLoader();
		this.genPro = new GenerateProject();
	}


	@Test
	public void imports() throws Exception {
		OWLOntology ontology=null;
		OWLOntologyManager ontManager = OWLManager.createOWLOntologyManager();		
		ontology= ontManager.loadOntologyFromOntologyDocument(this.getClass().getClassLoader().getResource("ontologies/Equimetrix.owl").openStream());
		
		Set<OWLDataPropertyDomainAxiom> ax = ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
		Set<OWLDataPropertyRangeAxiom> rng = ontology.getAxioms(AxiomType.DATA_PROPERTY_RANGE);
		System.out.println(rng.size());
		
		for (OWLAxiom axiom : ontology.getAxioms()) {
			if(axiom.isOfType(AxiomType.DECLARATION) && axiom.getSignature().iterator().next().isOWLClass() ) {
				OWLClass cls=axiom.getSignature().iterator().next().asOWLClass();
				System.out.println("clase "+cls.getIRI().getFragment());
				for (OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom : ax) {
					if(owlDataPropertyDomainAxiom.getDomain().asOWLClass().equals(cls)) {
						System.out.println("prop name: "+owlDataPropertyDomainAxiom.getProperty().asOWLDataProperty().getIRI());
						for (OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom : rng) {
								System.out.println("range of prop "+owlDataPropertyRangeAxiom.getRange());
						}
						
					}
					
				}
			}
		}
	}


	

		
}

