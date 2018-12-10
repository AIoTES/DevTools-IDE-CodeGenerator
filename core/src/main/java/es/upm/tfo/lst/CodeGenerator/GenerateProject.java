package es.upm.tfo.lst.CodeGenerator;


import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.upm.tfo.lst.CodeGenerator.exception.MissingRequiredVariableValueException;
import es.upm.tfo.lst.CodeGenerator.model.MacroModel;
import es.upm.tfo.lst.CodeGenerator.model.Project;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import uk.ac.manchester.cs.jfact.JFactFactory;
/**
 * Main class whos read {@link OWLOntology} and XML, and generate code.
 * In this class the tool has all the methods to read ontologies and explore all of it aspects to generate code.
 * <br><br>
 *  <br><u><b>information to velocity template developer</b></u><br>
 *  To develop templates, user need some information to data disponibility into velocity context.
 *  The developer have access to ontology into velocity macro from the next keys
 *  <ul>
 *  <li>{@link Set}<{@link Variable}> under key variables: to access to all variables given in XML file  </li>
 *  <li>{@link OWLOntology} under key ontology: to accecs to the current processing ontology</li>
 *  <li>{@link OWLClass } under key class: to acces to current processing class</li>
 *  <li>{@link Set}<{@link OWLNamedIndividual} under key instances: to acces to all of instances to processing class></li>
 *  <li>{@link Set}<{@link NodeSet}<{@link OWLNamedIndividual}>> under tag propertieValues: the Set contains a NodeSet of OWLNAmedIndividual for each instance of actual processing class </li>
 *  </ul>
 *
 * @author Buhid Eduardo
 *
 */
public class GenerateProject {


	private TemplateDataModel mainModel=null;
	private VelocityContext context,baseContext;
	private VelocityEngine vel_eng;
	private FileWriter fr;
	private Template template;
	private OWLReasonerFactory reasonerFactory;
	private OWLReasoner reasoner;
	private Map<String, Variable> variables;
	private  String jarFullPath=null,localBaseLoaderPath=null,outputFolder=null;
	private URL urlBasePath=null;
	private Properties props;
	private Set<OWLOntology> ontologies2BProcesed = new HashSet<>();

	private final static Logger log = Logger.getLogger(GenerateProject.class);

	/**
	 * The class constructor initialize the {@link OWLReasonerFactory}, {@link Properties} and some needed objects to use in velocity macro
	 *
	 * @param p {@link Project} object who give the list of ontologies to be process.
	 * @param model {@link TemplateDataModel} object builded from XML given file.
	 */
	public GenerateProject(TemplateDataModel model) {
		this(model,defaultVelocityProperties());
	}
	public GenerateProject(TemplateDataModel model, Properties velocityProperties) {


		this.mainModel = model;
		this.reasonerFactory = new JFactFactory();
		this.props = velocityProperties;
		this.variables= new HashMap<String,Variable>();
		// TODO add all default variables in the model.


	}

	/**
	 * Initialize the process to generate code
	 * before use this method, check if main ontology is loaded correctly and you have  all macros to process each element of the ontology, otherwise
	 * the program will not continue and send an error notification
	 *
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 */
	public boolean process() throws Exception{
		boolean flag=false;
		if(this.control()) {
			try {
				this.initVelocity();
				this.baseContext.put("allOntologies",this.ontologies2BProcesed.stream().collect(Collectors.toList()));
				flag =  this.processProject();
			}catch(Exception a){
				flag=false;
				log.fatal("error",a);
				throw a;
			}
		}else {
			flag = false;
		}



		return flag;
	}

	/**
	 * Mehod to process the project itself.
	 * @return @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private boolean processProject()throws Exception {
		boolean flag=true;
		String text="";
		List<MacroModel> projectModelArray = this.mainModel.getProjectMacro();
		if(!projectModelArray.isEmpty()) {
			for (MacroModel projectModel : projectModelArray) {
				if(this.fileControl(this.localBaseLoaderPath+projectModel.getTemplateName())) {

					text = this.processName(projectModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+text);
						if(!outputFolder.getParentFile().exists())
							outputFolder.getParentFile().mkdir();
					this.context= new VelocityContext(this.baseContext);
					this.baseContext.put("ontologyCompleteList", this.ontologies2BProcesed);
					if(!text.equals("")) {
						template = vel_eng.getTemplate(projectModel.getTemplateName());
						this.fr = new FileWriter(this.outputFolder+text,true);
						template.merge(context,fr);
						fr.close();
					}else {
						log.warn("output for project is empty and the program will not generate any output file to project");
					}

					for (OWLOntology ontology : this.ontologies2BProcesed) {
						//este reasoner se para esta ontologia, de aqui hacia abajo el reasoner no va a cambiar de ontologia
						this.reasoner = this.reasonerFactory.createReasoner(ontology);

						this.baseContext.put("reasoner", this.reasoner);
						if (! this.processOntology(ontology)) {
							flag = false;
						};
					}

				}else {
					flag=false;
					log.fatal("cant find velocity macro in given XML: "+this.localBaseLoaderPath+projectModel.getTemplateName());
				}
			}
		}else {
			log.warn("doesn't exist macro to project");
			for (OWLOntology ontology : this.ontologies2BProcesed) {
				//este reasoner se para esta ontologia, de aqui hacia abajo el reasoner no va a cambiar de ontologia
				this.reasoner = this.reasonerFactory.createReasoner(ontology);
				//System.out.println(this.reasoner);
				this.baseContext.put("reasoner", this.reasoner);
				if (! this.processOntology(ontology)) {
					flag = false;
				};
			}

		}

		return flag;
	}
	/**
	 * Process all macros for ontology
	 * @param ontology {@link OWLOntology} to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private boolean processOntology(OWLOntology ontology) throws Exception{
		boolean flag=true,state=true;
		List<MacroModel> ontologyModelArray = this.mainModel.getOntologyMacro();
		String name="";

		this.baseContext.put("ontology", ontology);
		//this.context= new VelocityContext(this.baseContext);

		if(!ontologyModelArray.isEmpty()) {
			//this.context= new VelocityContext(this.baseContext);
			for (MacroModel ontologyModel : ontologyModelArray) {
				if(this.fileControl(this.localBaseLoaderPath+ontologyModel.getTemplateName())) {
					//read xml output tag and parse to velocity
					name = this.processName(ontologyModel.getOutput(), baseContext);
					//control directory existance for result of velocity process of output
					File outputFolder = new File(this.outputFolder+name);
					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();
					//merge base context to actual context
					this.context= new VelocityContext(this.baseContext);

					//this.context.put("ontology",ontology);
					if(!name.equals("")) {
						template = vel_eng.getTemplate(ontologyModel.getTemplateName());
						this.fr = new FileWriter(this.outputFolder+name,true);
						template.merge(context,fr);
						fr.close();
					}else {
						log.warn("output for ontology is empty and the program will not generate any output file to ontology");
					}

					//iterate over classes into actual ontology  and process each one
					for(OWLClass c : ontology.getClassesInSignature()) {
						if (!this.processClass(c,ontology)){
							flag = false;
						}
					}

					flag=true;
				}else {
					log.fatal("velocity template for ontology doesn't exist "+ontologyModel.getTemplateName());
					flag=false;
				}
			}
		}else{
			log.warn("ontology macro for ontology isn't exists");
			for(OWLClass c : ontology.getClassesInSignature()) {
				if (!this.processClass(c,ontology)){
					flag = false;
				}

			}
		}
		return flag && state;
	}

	/**
	 * process macro for classes
	 * @param c {@link OWLClass} to process
	 * @param ontology {@link OWLOntology } to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private boolean processClass(OWLClass c,OWLOntology ontology) throws Exception  {
		boolean flag=true;
		String directoryName="";
		List<MacroModel> classModelArray = this.mainModel.getClassMacro();
		if(!classModelArray.isEmpty()) {
			this.context = new VelocityContext(this.baseContext);
			for (MacroModel macroModel : classModelArray) {
				c.getAnnotationPropertiesInSignature();
				if(this.fileControl(this.localBaseLoaderPath+macroModel.getTemplateName())) {
					this.context.put("ontology",ontology);
					this.context.put("class",c);
					directoryName=this.processName(macroModel.getOutput(),this.context);
					File outputFile = new File(this.outputFolder+directoryName);
					if(!outputFile.getParentFile().exists())
						outputFile.getParentFile().mkdirs();
					if(!macroModel.getOutput().equals("")) {
						template = vel_eng.getTemplate(macroModel.getTemplateName());
						this.fr = new FileWriter(this.outputFolder+directoryName,true);
						template.merge(context, fr);
						fr.close();
					}else {
						log.warn("output for class is empty and the program will not generate any output file to class");
					}

					for(OWLClass cls : ontology.getClassesInSignature() ) {
						if(! this.processInstances(cls,ontology) ) {
							flag=false;
						}
					}
				}else {
					log.fatal("inexistent template: "+macroModel.getTemplateName());
					flag=false;
				}
		   }
		}else{

			for(OWLClass cls : ontology.getClassesInSignature() ) {
				if(this.processInstances(cls,ontology)==false ) {
					flag=false;
				}
			}

		}

		return flag;
	}

	/**
	 * Needs OWLReasoner to process data
	 * @param clase {@link OWLClass} to process
	 * @param ontology {@link OWLOntology} to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private boolean processInstances(OWLClass c,OWLOntology ontology) throws Exception{
		String name;
		boolean flag=true,state=true;
		Set<OWLNamedIndividual> instances = new HashSet<>();
		//ontology.getOntologyID().getOntologyIRI().get().getShortForm().replace("\\.","");
		List<MacroModel> instancesModelArray = this.mainModel.getInstanceMacro();
		instances.addAll(reasoner.getInstances(c, true).getFlattened());
		if(!instancesModelArray.isEmpty()) {
			for (MacroModel macroModel : instancesModelArray) {
				if(this.fileControl(this.localBaseLoaderPath+macroModel.getTemplateName())) {
					template = vel_eng.getTemplate(macroModel.getTemplateName());
					//instances = reasoner.getInstances(c, true).getFlattened();
					this.context= new VelocityContext(this.baseContext);
					this.context.put("ontology",ontology);
					this.context.put("class",c);
					this.context.put("instances",instances);
					name = this.processName(macroModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+name);
					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();

					this.fr = new FileWriter(this.outputFolder+name,true);
					template.merge(context, fr);
					fr.close();
					state = this.processObjectProperties(c,instances,ontology);
				}else {
					log.fatal("template file not exists: "+macroModel.getTemplateName());
					flag=false;
				}
			}
		}else{
			//log.warn("output for instances is empty and the program will not generate any output file to instances");
			state = this.processObjectProperties(c,instances,ontology);

		}
		return flag && state;
	}
	/**
	 * Method to get Object Properties of  class instances.
	 * @param c {@link OWLClass}: Class to be processed.
	 * @param instances {@link Set}<{@link OWLNamedIndividual}>: Individuals of given class.
	 * @param ontology {@link OWLOntology}: Ontology to process.
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception.
	 */
	/*
	 * este metodo cuenta mas clases tenga la ontologia mas grande se hace el Set principal
	 * */
	private boolean processObjectProperties(OWLClass c,Set<OWLNamedIndividual> instances,OWLOntology ontology  ) throws Exception{
		boolean flag = true,state = true;
		//System.out.println("processPropertyValues");
		List<MacroModel> propertyModelArray = this.mainModel.getObjectProperties();
		String name;
		Set< NodeSet<OWLNamedIndividual> > aux = new HashSet<>();

		Set<OWLObjectProperty> op = ontology.getObjectPropertiesInSignature();
		for (OWLNamedIndividual ind : instances) {
			for (OWLObjectProperty owlObjectProperty : op) {
				aux.add(this.reasoner.getObjectPropertyValues(ind, owlObjectProperty));

			}
		}

		if(!propertyModelArray.isEmpty()) {
			for (MacroModel macroModel : propertyModelArray) {

				if(this.fileControl(this.localBaseLoaderPath+macroModel.getTemplateName())) {

					this.context= new VelocityContext(this.baseContext);
					name = this.processName(macroModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+name);

					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();

					this.context.put("class",c);
					this.context.put("classesInstances",macroModel);
					this.context.put("superClasses", this.reasoner.getSuperClasses(c, true).getFlattened());
					this.context.put("propertyValues", aux);

					if(!macroModel.getOutput().equals("")){
						this.fr = new FileWriter(this.outputFolder+name,true);
					 	template = vel_eng.getTemplate(macroModel.getTemplateName());
						template.merge(context, fr);
						fr.close();
					}


					}else {
						flag=false;
					}
			  }
			}else{
				//log.warn("output for class is empty and the program will not generate any output file to class");
				flag=false;

			}



		return flag;
	}


	/**
	 * Control of velocity macro existance.
	 * @param path to file
	 * @return True if the file exists, False if isn't exist
	 */
	private boolean fileControl(String path)  {
		return (new File(path).exists());
	}


	private static Properties defaultVelocityProperties() {
		Properties props = new Properties();
		props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
	    //props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.LogChute");
	    //props.setProperty("runtime.log.logsystem.class", NullLogChute.class.getName());
	    //props.setProperty("runtime.log.logsystem.log4j.logger","velocity");
	    //props.setProperty("runtime.log","/target/loggVelocity.log");
		return props;
	}

	/**
	 * Initialize VelocityEngine and base context who hold base data to use in all contexts.
	 * In this method is initialized the base context who have all data wich is needed for all macros
	 * like variables added into XML file.
	 * @throws Exception
	 */
	private void initVelocity() throws Exception{
		vel_eng = new VelocityEngine();
	    this.baseContext = new VelocityContext();
	    this.baseContext.put("variables", this.variables);
	   	vel_eng.init(props);

	}
	/**
	 * Set path to load velocity templates from specific jar file.
	 * @param jarFullPath path to jar who contains needed velocity macros.
	 */
	public void setBaseJarFullPath(String jarFullPath) {
		this.jarFullPath = jarFullPath;
		//jar.resource.loader.path = jar:file:/myjarplace/myjar.jar, jar:file:/myjarplace/myjar2.jar
	}
	/**
	 * Set the url to load remote velocity macros.
	 * @param urlBasePath.
	 */
	public void setUrlBasePath(URL urlBasePath) {
		this.urlBasePath = urlBasePath;
		 props.put("url.resource.loader.root", this.urlBasePath);

	}
	/**
	 * Set path to load velocity macros from local filesystem.
	 * @param localBaseLoaderPath {@link String } path to load velocity templates from local file system.
	 */
	public void setLocalBaseLoaderPath(String localBaseLoaderPath) {
		this.localBaseLoaderPath=localBaseLoaderPath;
		 props.put("file.resource.loader.path", this.localBaseLoaderPath);
	}

	/**
	 *  Sets the output folder to generated files.
	 * @param outputFolder path to output folder. If the folder not exists, the plugin creates it into given path.
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder=outputFolder;
	}

	/**
	 * control if sources loader path is set
	 * @return
	 */
	private boolean resourcesLoaderCOntrol() {
		if(this.jarFullPath == null &&  this.urlBasePath==null && this.localBaseLoaderPath == null) {
			return false;
		}
		return true;
	}

	/**
	 * all of controls before ejecute project:
	 * <ul>
	 * 	<li>Control if is loaded main ontology</li>
	 *  <li>Control if XML file is loaded correctly</li>
	 *  <li>Control if velocity macros directory loader is setted </li>
	 *  <li>Control required variables count </li>
	 * </ul>
	 * @return boolean value indicating result of control
	 * @throws MissingRequiredVariableValueException
	 */
	private boolean control() throws MissingRequiredVariableValueException {
		boolean flag=false;
		if(this.ontologies2BProcesed.size() > 0 ) {
			if(this.mainModel!=null) {
				if(this.resourcesLoaderCOntrol()) {
					if(this.mainModel.getRequiredVariables().size()==0) {
						flag=true;
					}else {
						Set<String> requierdVars = mainModel.getArrayVars().stream().filter(h->h.isRequired()).map(l->l.getName()).collect(Collectors.toSet());


						if(  variables.keySet().containsAll(requierdVars)  ){
							flag=true;
						}else {
							requierdVars.removeAll(variables.keySet());
							String msg = "Required variables are not set:";
							for (String var : requierdVars) {
								msg += var + ", ";
							}
							log.fatal(msg);
							throw new MissingRequiredVariableValueException(msg);
						}
					}
				}else {
					log.fatal("Resources folder isn't set, program will stop");
				}
			}else {
				log.fatal("please be shure if method generateXMLCoordinator() is called from XmlParser object");
			}
		}else {
			log.fatal("main ontology couldnt be added");
		}
		return flag;
	}

	/**
	 * Process the velocity code given in xml file. The purpose of this method is process velocity code and
	 * return string value of result of the process.
	 *
	 * @param toProcess String to parse to velocity to process
	 * @param VelocityContext   containig needed references
	 * @return {@link String } value, result of the process
	 */
	private String processName(String toProcess,VelocityContext context) {
		String t="--";
		try {
			StringWriter fw = new StringWriter();
			StringResourceRepository rep= StringResourceLoader.getRepository();
			//VelocityEngine ve = new VelocityEngine();
			Template te = new Template();
			RuntimeServices rs=RuntimeSingleton.getRuntimeServices();
			StringReader sr = new StringReader(toProcess);
			SimpleNode sn = rs.parse(sr,"s");
			rep = new StringResourceRepositoryImpl();
			StringResourceLoader.setRepository(StringResourceLoader.REPOSITORY_NAME_DEFAULT, rep);


			te.setRuntimeServices(rs);
		    te.setData(sn);
		    te.initDocument();

			te.merge(context, fw);
			t=fw.toString();
			fw.close();
		}catch(Exception a) {
			log.fatal("cant process name ",a);
		}
		//System.out.println(t);
		return t;
	}

	/**
	 * Method to add variable into project. Replaces any existing value.
	 * @param varName the name of the {@link Variable} (as in {@link Variable#getName()})
	 * @param varValue the run value of the variable, if null, removes variable.
	 * @return boolean
	 */
	public boolean setVariable(String varName, String varValue) {
		if (varName == null) {
			return false;
		}
		variables.get(varName).setValue(varValue);
		return true;
	}
	/**
	 * Method to return {@link Set}<{@link Variable}> of all variables given in xml file
	 * @return {@link set}<{@link Variable}> of {@link  Variable} loaded from user
	 */
	/*
	public Set<Variable> getVariablesArray(){
		return this.mainModel.getArrayVars();
	}
	*/
	/**
	 * Method to return {@link Set}<{@link Variable}> of all variables given in xml file
	 * @return {@link set}<{@link Variable}> of {@link  Variable} loaded from user
	 */
	public Map<String, Variable> getVariablesArray(){
		return this.variables;
	}

	/**
	 * Load recursivesly (if is allowed) given ontology. If given ontology is null, {@link #getOntologies()}
	 * returns a empty Set of {@link OWLOntology}
	 *
	 * @param ont {@link OWLOntology} to be load
	 * @param recursive {@link Boolean} value indicating recursive load
	 */
	public void addOntology( OWLOntology ont, boolean recursive){
		ontologies2BProcesed.addAll(ont.getImports());
		ontologies2BProcesed.add(ont);
		//System.out.println("GENERATE PROJECT 590 "+this.ontologies2BProcesed.size());
		/*
		Set<OWLOntology> aux;
		if(ont != null) {
			boolean consistency = OntologyLoader.checkConsistency(ont);
			System.out.println("GENERATE PROJECT 590 "+OntologyLoader.checkConsistency(ont));
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
		*/
	}

	public String getOutputDir() {
		return outputFolder;
	}

}
