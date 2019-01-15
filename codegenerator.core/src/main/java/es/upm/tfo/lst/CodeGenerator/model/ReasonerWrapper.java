package es.upm.tfo.lst.CodeGenerator.model;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class ReasonerWrapper {
	private OWLReasoner reasoner;

	public OWLReasoner getReasoner() {
		return reasoner;
	}

	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
		
	}


	
}
