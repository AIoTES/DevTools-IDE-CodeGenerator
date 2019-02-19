/*******************************************************************************
 * Copyright 2018 Universidad Politécnica de Madrid UPM
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
package es.upm.tfo.lst.CodeGenerator.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;

/**
 * This class is in charge of loading the main ontology and load all of imports of
 * it recursively or not (according user choice).
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
	 * Load recursively (if indicated) given ontology. If given ontology is null,
	 * {@link #getOntologies()} returns a empty Set of {@link OWLOntology}
	 *
	 * @param ont       {@link OWLOntology} to be load
	 * @param recursive true to indicate recursive load
	 */
	public void addOntology(OWLOntology ont, boolean recursive) {
		if (ont != null) {
			boolean consistency = OntologyLoader.checkConsistency(ont);
			Set<OWLOntology> aux = null;
			aux = ont.getImportsClosure();
			ontologies2BProcesed.add(ont);
			if (aux.size() == 1)
				recursive = false;

			if (recursive) {
				if (consistency) {
					int y = 1;
					for (; y <= aux.size(); y++) {
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
