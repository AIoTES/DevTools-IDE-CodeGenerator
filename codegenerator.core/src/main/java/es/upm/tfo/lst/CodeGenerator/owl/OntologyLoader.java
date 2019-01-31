package es.upm.tfo.lst.CodeGenerator.owl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import uk.ac.manchester.cs.jfact.JFactFactory;
/**
 * 
 * This class provide methods to load an ontology from different sources
 * 
 * @author Buhid Eduardo 
 * @version 1
 * 
 * 
 */
public class OntologyLoader {
	
	private final static Logger log = Logger.getLogger(GenerateProject.class);
	
	private File ontologyFile=null;
	private OWLOntology ontology = null;
	private OWLOntologyManager ontologyManager = null;
    private static OWLReasonerFactory reasonerFactory;
    /**
     * Load main ontology to be processed
     * 
     * @param url location from ontology
     * @return Reference to loaded ontology, or null if exist some problem
     */
 	public  OWLOntology loadOntology(String url)  {
 		try {
		 		boolean state;
		 		OWLOntology ont=null; 
		 		//allow to laod recursivesly OWLOntologyCreationIOException
		 		
		 		this.ontologyManager = OWLManager.createOWLOntologyManager();
		 		
		 		state = this.isLocalFile(url);
		
				if(state) {
						log.debug("web ontology");
						//if the method gets a inexistent url throws OWLOntologyCreationIOException
						try {
							ont = this.ontologyManager.loadOntology(IRI.create(url));
						} catch (OWLOntologyCreationException e) {
							log.fatal("error loading ontology, the given URL isn't valid "+e);
						}
						return ont;
				}else {
					log.debug("...loading local ontology...");
					this.ontologyFile = new File(url);
					if(this.ontologyFile.exists()) {
						log.debug("reading ontology from file "+url);
						ArrayList <String> aux= new ArrayList<>();
						String k =new String();
						aux.addAll(Arrays.asList(url.split("/")));
						aux.remove(aux.size()-1);
						for (String string : aux) {
							k+=string+"/";
						} 
						//this.ontologyManager.getIRIMappers().add(new AutoIRIMapper(new File("src/test/resources/ontologies/"), true));
						this.ontologyManager.getIRIMappers().add(new AutoIRIMapper(new File(k), true));
						try {
							ont=this.ontologyManager.loadOntologyFromOntologyDocument(this.ontologyFile);
						} catch (OWLOntologyCreationException e) {
							log.fatal("error loading ontology",e);
						}
						return ont;
					}else {
						log.fatal("problems to load ontology from: "+this.ontologyFile.getPath()+". Seems ontology doesn't exist");
					}
		
				}
			return null;
 		}catch (Exception e) {
			//log.fatal("error",e);
 			log.fatal("error importing ontologies "+e.getClass());
		}
 		return null;
	} 

 	private boolean isLocalFile( String urlString) {
 		try {
 			URL url = new URL(urlString);
 		}catch (MalformedURLException e) {
 			log.warn("parsed string isn't a valid URL "+urlString);
 			return false;
		}
 		return true;
 	}
 	
 	/**
 	 * checks if given ontology is consistent
 	 */
	public static boolean checkConsistency(OWLOntology u){
		return  reasonerFactory.createReasoner(u).isConsistent();
	}
	/**
	 * 
	 * @return {@link OWLOntology} object referencing given ontology in {@link loadOntology } method.
	 * Null if the ontology cant be loade 
	 *
	 * 
	 */
	public OWLOntology getOntology() {
		return ontology;
	}
	
	
	
	
	
}
