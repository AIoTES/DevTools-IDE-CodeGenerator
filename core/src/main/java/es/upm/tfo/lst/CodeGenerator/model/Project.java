package es.upm.tfo.lst.CodeGenerator.model;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
/**
 * This class is in charge to load the main ontology and load all of imports of it recursively or not (according user choice).
 * 
 * @author Buhid Eduardo
 * @version 1
 */
public class Project {
	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private Set<OWLOntology> ontologies2BProcesed = new HashSet<>();
	
	private Set<Variable> variables;

	
	public Project() {
		
	}
	/**
	 * Load recursivesly (if is allowed) given ontology. If given ontology is null, {@link #getOntologies()}
	 * returns a empty Set of {@link OWLOntology}
	 * 
	 * @param ont {@link OWLOntology} to be load
	 * @param recursive {@link Boolean} value indicating recursive load
	 */
	public void addOntology( OWLOntology ont, boolean recursive){
		if(ont != null) {
			boolean consistency = OntologyLoader.checkConsistency(ont);
			Set<OWLOntology> aux=null;
			aux=ont.getImportsClosure();
			ontologies2BProcesed.add(ont);
			if(aux.size()==1)
				recursive=false;
			
			if(recursive) {
				if(consistency) {
					int y=1;
						for(;y<=aux.size();y++) {
							addOntology(aux.iterator().next(), recursive);
						}	
					}
				}
		}
		
	}

	/**
	 * @return a Set<OWLOntology> getting all ontologies if recursive is enabled 
	 */
	public Set<OWLOntology> getOntologies() {
		return ontologies2BProcesed;
	} 
	


}


