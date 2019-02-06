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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.kernel.Axiom;

/**
 * @author amedrano
 *
 */
public class DeveloperTests {

	private  XmlParser parser=null;
	private TemplateDataModel model=null;
	private GenerateProject genPro=null;
	private OntologyLoader ontologyLoader=null;
	OWLReasoner reasoner=null;
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
		//c.getIRI().getNamespace()
		
	}
//OWLDataProperty
	@Test
	public void sqltest() {
		 System.out.println("\n------------------------------complete  test--------------------------------------\n");
		 OWLOntology t = this.ontologyLoader.loadOntology(this.ontologyBasePath+"universidad.owl");
		 OWLReasonerFactory reasonerFactory= new JFactFactory();
		 this.reasoner = reasonerFactory.createReasoner(t);
//		 for (OWLClass cls : t.getClassesInSignature()) {
//			 for (Node<OWLNamedIndividual> individual: 	this.reasoner.getInstances(cls, true)) {
//				System.out.println(individual.getRepresentativeElement().getSignature());
//				 //individual.getRepresentativeElement().getIndividualsInSignature();
//			}
//		
//		}
		// System.out.println(t.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN));
		 //System.out.println("lastindex "+t.getOntologyID().getOntologyIRI().get().getNamespace());
//		 for (OWLDataPropertyRangeAxiom axiom : t.getAxioms(AxiomType.DATA_PROPERTY_RANGE) ) {
//			 System.out.println(t.getAnnotationPropertiesInSignature());
			 //System.out.println(axiom.getDatatypesInSignature());
			 //System.out.println(axiom.getProperty().asOWLDataProperty().getIRI().getFragment());
//			 for (OWLEntity ent : axiom.getSignature()) {
//				System.out.println(ent);
//				
//			}
			 //System.out.println();
			// System.out.println(axiom);
			 
//		}
		 for (OWLClass cls : t.getClassesInSignature()) {
			System.out.println(cls.getIRI());
		}
		 System.out.println("\n");
		 for (OWLDataPropertyAssertionAxiom axiom : t.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION) ) {
//			 System.out.println(axiom.getIndividualsInSignature());
			// System.out.println(axiom.getSignature());
			 	
			 for (OWLEntity y : axiom.getSignature()) {
				 System.out.println(y);
				//IRI example = IRI.create(y.toString());
//				System.out.println(example);
//				System.out.println(y.getIndividualsInSignature());
//				System.out.println(y.getDatatypesInSignature().size());
//				System.out.println(y.getDataPropertiesInSignature().size());
			}
			 System.out.println("\n");
		//	System.out.println(axiom.asOWLSubClassOfAxiom());//to print subclasses of
		//	System.out.println(axiom.getSignature());//return the individual name, the propertie and the propertie type
			//System.out.println(axiom.asOWLSubClassOfAxiom().getDatatypesInSignature());
//			 System.out.println("---");
			// System.out.println(axiom.getDataPropertiesInSignature());
//			for (OWLDatatype n : axiom.asOWLSubClassOfAxiom().getDatatypesInSignature()) {
//			System.out.println(axiom.getDataPropertiesInSignature().iterator().next().getIRI());
//			System.out.println(axiom.getDataPropertiesInSignature().iterator().next().getIRI().getFragment());

			//System.out.println(n.getIRI().getFragment());
//				System.out.println(n.getSignature());
//			}
		}
		 //System.out.println(t.getAxioms());


	
}
}