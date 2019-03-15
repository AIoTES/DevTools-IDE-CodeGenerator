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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

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
	public void classAccessExample() {
		OWLClass cls;
	}

	@Test
	public void ontologyAccessExample() {
		OWLOntology ontology;
		OWLClass c;
		OWLNamedIndividual v;
		
		 
		//c.getIRI().getNamespace()
		
	}

	@Test
	public void JsonGeneratorTests() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
		 OWLOntology t = this.ontologyLoader.loadOntology(this.ontologyBasePath+"games.owl");
		 OWLReasonerFactory reasonerFactory= new JFactFactory();
		 this.reasoner = reasonerFactory.createReasoner(t);
		 Map <String, String> data = new HashMap<String, String>();
		 Set< Map <String, String>  > lista = new HashSet<Map<String,String>>();
		 for (OWLClass cls : t.getClassesInSignature()) {
			 System.out.println(cls.getIRI().getFragment());
			  Set<OWLDataPropertyRangeAxiom> g = t.getAxioms(AxiomType.DATA_PROPERTY_RANGE); //para obtener data  type con el nombre de la propiedad
			  Set<OWLDataPropertyDomainAxiom> k = t.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
		  
//		  System.out.println("DATA_PROPERTY_RANGE "+g.size());
//		  System.out.println("DATA_PROPERTY_DOMAIN "+k.size());

			 for (OWLDataPropertyDomainAxiom axiom : k) {
//					axiom.getDataPropertiesInSignature().forEach(r->{if (axiom.getClassesInSignature().contains(cls) ) System.out.println("dataprops "+r.getIRI().getFragment());});
				 if (axiom.getClassesInSignature().contains(cls) ) {
					 for (OWLDataProperty map : axiom.getDataPropertiesInSignature()) {
						 for (OWLDataPropertyRangeAxiom item : g) {
							 if(item.containsEntityInSignature(map )) {
								 System.out.println(item.getDataPropertiesInSignature().toString().replace("[", " ").replace("]", " ")+":"+item.getDatatypesInSignature().toString().replace("[", " ").replace("]", " "));
							 }
							}

//						 System.out.println(map);	
					}
					
				 }
//				 System.err.println(axiom);
			 }
			 

		 }
	}
	
	@Test
	public void DefaultVelocityTemplateTest() {
		
	}
	
	@Test
	public  void SQLTemplateTest() {
		
	}
	
	
	
	
	
}

