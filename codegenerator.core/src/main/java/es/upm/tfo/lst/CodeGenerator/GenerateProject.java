package es.upm.tfo.lst.CodeGenerator;


import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.upm.tfo.lst.CodeGenerator.exception.MissingRequiredVariableValueException;
import es.upm.tfo.lst.CodeGenerator.model.MacroModel;
import es.upm.tfo.lst.CodeGenerator.model.Project;
import es.upm.tfo.lst.CodeGenerator.model.ReasonerWrapper;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
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


	public interface ProgessCallbackPublisher {
		void updateProgress(int done, int total);
	}
	private ReasonerWrapper wrapper=null;
	private TemplateDataModel mainModel=null;
	private VelocityContext context,baseContext;
	private VelocityEngine vel_eng;
	private FileWriter fr;
	private Template template;
	private OWLReasonerFactory reasonerFactory;
	private OWLReasoner reasoner=null;
	private Map<String, Variable> variables;
	private  String jarFullPath=null,localBaseLoaderPath=null,outputFolder=null;
	private String text="";
	private List<Exception> arrayOfExceptions=null;
	private URL urlBasePath=null;
	private Properties props;
	private Set<OWLOntology> ontologies2BProcesed = new HashSet<>();
	//private List<ProgessCallback> progressCallbacs = new ArrayList<>();
	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private int total2Process;
	public  ProgessCallbackPublisher GenConf;
	
	public GenerateProject(TemplateDataModel model, Properties velocityProperties) {
		this.mainModel = model;
		this.reasonerFactory = new JFactFactory();
		this.props = velocityProperties;
		this.variables= new HashMap<String,Variable>();
		if(this.mainModel!=null) 
			this.variables = this.mainModel.getArrayVars();
		this.wrapper = new ReasonerWrapper();
		this.arrayOfExceptions = new ArrayList<>();
	}
	
	/**
	 * The class constructor initialize the {@link OWLReasonerFactory}, {@link Properties} and some needed objects to use in velocity macro
	 *
	 * @param p {@link Project} object who give the list of ontologies to be process.
	 * @param model {@link TemplateDataModel} object builded from XML given file.
	 */
	public GenerateProject(TemplateDataModel model) {
		this(model,defaultVelocityProperties());
		//this.mainModel = model;
		//this.variables = this.mainModel.getArrayVars();
	}

	/**
	 * if user initialize project without parsing the XmlTemplateModel, user must set it later
	 * variables array will be empty
	 */
	public GenerateProject() {
		this(null,defaultVelocityProperties());
//		this.reasonerFactory = new JFactFactory();
//		this.props=defaultVelocityProperties();
//		this.reasonerFactory = new JFactFactory();
//		this.variables= new HashMap<String,Variable>();
//		this.wrapper = new ReasonerWrapper();

	}

	/**
	 * Initialize the process to generate code
	 * before use this method, check if main ontology is loaded correctly, otherwise
	 * the program will not continue and send an error notification
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception if some problem occur in the process
	 */
	public void process()  throws Exception {
		total2Process = 4; // TODO calculate
		if(this.control()) {
			try {
				this.initVelocity();
				this.baseContext.put("allOntologies",this.ontologies2BProcesed.stream().collect(Collectors.toList()));
				this.baseContext.put("output", this.outputFolder);
				this.baseContext.put("date",new Date());
				this.baseContext.put("object",Object.class);
				this.addVariablesToBaseContext();
				
				if (this.mainModel.getProjectMacro().isEmpty()) log.warn("doesn't exist macro to project");
				if (this.mainModel.getOntologyMacro().isEmpty()) log.warn("doesn't exist macro to ontology");
				if (this.mainModel.getClassMacro().isEmpty()) log.warn("doesn't exist macro to class");
				if (this.mainModel.getInstanceMacro().isEmpty()) log.warn("doesn't exist macro to instances");
				if (this.mainModel.getObjectProperties().isEmpty()) log.warn("doesn't exist macro to object properties");

				this.processProject();
			}catch(Exception a){
				log.fatal("fatal error ",a);
				this.arrayOfExceptions.add(a);
				throw a ;
			}
		}


		
	}

	/**
	 * Mehod to process the project itself.
	 * @return @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private void processProject() throws Exception {
		
		List<MacroModel> projectModelArray = this.mainModel.getProjectMacro();
		if(!projectModelArray.isEmpty()) {
			
			for (MacroModel projectModel : projectModelArray) {
				
					this.text = this.processName(projectModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+text);
						if(!outputFolder.getParentFile().exists())
							outputFolder.getParentFile().mkdir();
					this.context= new VelocityContext(this.baseContext);

					if(!this.text.equals("")) {
						template = vel_eng.getTemplate(projectModel.getTemplateName());
						//throws IOE
						try {
							this.fr = new FileWriter(this.outputFolder+text,true);
							template.merge(context,fr);
							fr.close();
						}catch (Exception e) {
							log.fatal("cant merge velocity template with velocity context",e);
							this.arrayOfExceptions.add(e);
							throw e;	
						}
					}
//					else {
//						log.warn("output for project is empty and the program will not generate any output file to project");
//					}
				    update(1);

					for (OWLOntology ontology : this.ontologies2BProcesed) {
						//este reasoner es para esta ontologia, de aqui hacia abajo el reasoner no va a cambiar de ontologia
						this.reasoner = this.reasonerFactory.createReasoner(ontology);
						this.wrapper.setReasoner(this.reasoner);
						this.baseContext.put("reasoner", this.wrapper);
						this.processOntology(ontology);
					}
			}
		}else {	
			for (OWLOntology ontology : this.ontologies2BProcesed) {
				this.reasoner = this.reasonerFactory.createReasoner(ontology);
				this.wrapper.setReasoner(this.reasoner);
				this.baseContext.put("reasoner", this.wrapper);
				this.processOntology(ontology);
			}

		}

	}
	/**
	 * Process all macros for ontology
	 * @param ontology {@link OWLOntology} to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private void processOntology(OWLOntology ontology) throws Exception{
		//local variables
		this.text="";
		boolean flag=true,state=true;
		List<MacroModel> ontologyModelArray = this.mainModel.getOntologyMacro();
		
		//adding to base context the actual ontology
		this.baseContext.put("ontology", ontology);
	
		//do work for any macro for ontology
		if(!ontologyModelArray.isEmpty()) {
			//this.context= new VelocityContext(this.baseContext);
			for (MacroModel ontologyModel : ontologyModelArray) {
					//read xml output tag and parse to velocity
					this.text = this.processName(ontologyModel.getOutput(), baseContext);
					//control directory existance for result of velocity process of output
					File outputFolder = new File(this.outputFolder+this.text);
					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();
					//merge base context to actual context
					this.context= new VelocityContext(this.baseContext);
					//this.context.put("ontology",ontology);
					if(!this.text.equals("")) {
						template = vel_eng.getTemplate(ontologyModel.getTemplateName());
						//throws IOE
						try {
							this.fr = new FileWriter(this.outputFolder+this.text,true);
							template.merge(context,fr);
							fr.close();
						}catch (Exception e) {
							this.arrayOfExceptions.add(e);
							log.fatal("cant merge velocity template with velocity context",e);
							throw e; 
						}
						
					}
//					else {
//						log.warn("output for ontology is empty and the program will not generate any output file to ontology");
//					}
					update(2);
					//iterate over classes into actual ontology  and process each one
					for(OWLClass c : ontology.getClassesInSignature()) {
						this.processClass(c,ontology);
					}
			}
		}else{
			update(2);
			for(OWLClass c : ontology.getClassesInSignature()) {
				this.processClass(c,ontology);
			}
		}
		
	}

	/**
	 * process macro for classes
	 * @param c {@link OWLClass} to process
	 * @param ontology {@link OWLOntology } to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private void processClass(OWLClass c,OWLOntology ontology)  throws Exception{
		boolean flag=true;
		this.text="";
		List<MacroModel> classModelArray = this.mainModel.getClassMacro();
		this.baseContext.put("class",c);
		if(!classModelArray.isEmpty()) {
			this.context = new VelocityContext(this.baseContext);
			for (MacroModel macroModel : classModelArray) {
					this.text =this.processName(macroModel.getOutput(),this.context);
					File outputFile = new File(this.outputFolder+this.text);
					if(!outputFile.getParentFile().exists())
						outputFile.getParentFile().mkdirs();
					if(!macroModel.getOutput().equals("")) {
						template = vel_eng.getTemplate(macroModel.getTemplateName());
						//throw IOE
						try {
							this.fr = new FileWriter(this.outputFolder+this.text,true);
							template.merge(context, fr);
							fr.close();
						} catch (Exception e) {
							this.arrayOfExceptions.add(e);
							log.fatal("cant merge velocity template with velocity context",e);
							throw e;
						}	
					}
//					else {
//						log.warn("output for class is empty and the program will not generate any output file to class");
//					}
					
					for(OWLClass cls : ontology.getClassesInSignature() ) {
						this.processInstances(cls,ontology);
					}
		   }
		}else{
			update(3);
			for(OWLClass cls : ontology.getClassesInSignature() ) {
				this.processInstances(cls,ontology);
			}

		}
		update(3);
		
	}

	/**
	 * Needs OWLReasoner to process data
	 * @param clase {@link OWLClass} to process
	 * @param ontology {@link OWLOntology} to process
	 * @return boolean value.True if the process is sucessfull.False if any problem occur.
	 * @throws Exception
	 */
	private void processInstances(OWLClass c,OWLOntology ontology) throws Exception {
		this.text ="";
		Set<OWLNamedIndividual> instances = new HashSet<>();
		//ontology.getOntologyID().getOntologyIRI().get().getShortForm().replace("\\.","");
		List<MacroModel> instancesModelArray = this.mainModel.getInstanceMacro();
		instances.addAll(reasoner.getInstances(c, true).getFlattened());
		this.baseContext.put("instances",instances);
		if(!instancesModelArray.isEmpty()) {
			for (MacroModel macroModel : instancesModelArray) {

					template = vel_eng.getTemplate(macroModel.getTemplateName());
					this.context= new VelocityContext(this.baseContext);
					this.text = this.processName(macroModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+this.text);
					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();
					//throws IOE
					try {
						this.fr = new FileWriter(this.outputFolder+this.text,true);
						template.merge(context, fr);
						fr.close();
						
					} catch (Exception e) {
						this.arrayOfExceptions.add(e);
						log.fatal("cant merge velocity template with velocity context",e);
						throw e;
					}
					
					this.processObjectProperties(c,instances,ontology);
				
			}
		}else{
			
			//log.warn("output for instances is empty and the program will not generate any output file to instances");
			this.processObjectProperties(c,instances,ontology);

		}
		update(4);
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
	private void processObjectProperties(OWLClass c,Set<OWLNamedIndividual> instances,OWLOntology ontology  ) throws Exception{
		
		this.text ="";
		//System.out.println("processPropertyValues");
		List<MacroModel> propertyModelArray = this.mainModel.getObjectProperties();
		if(!propertyModelArray.isEmpty()) {
			for (MacroModel macroModel : propertyModelArray) {
				
					this.context= new VelocityContext(this.baseContext);
					this.text = this.processName(macroModel.getOutput(), this.context);
					File outputFolder = new File(this.outputFolder+this.text);

					if(!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();

					//this.context.put("class",c);
					//this.context.put("classesInstances",instances);
					//this.context.put("ontology", ontology);
					//to access superclasses must use reasoner into context from wrapper class
					//this.context.put("superClasses", this.reasoner.getSuperClasses(c, true).getFlattened());
					//this.context.put("propertyValues", aux);
					update(5);
					if(!macroModel.getOutput().equals("")){
						try {
							this.fr = new FileWriter(this.outputFolder+this.text ,true);
						 	template = vel_eng.getTemplate(macroModel.getTemplateName());
							template.merge(context, fr);
							fr.close();
						} catch (Exception e) {
							this.arrayOfExceptions.add(e);
							log.fatal("cant merge velocity template with velocity context",e);
							throw e;
						}			
					
					}
	
			  }
			}


		update(5);
		
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
	    //adding separately each variable to velocity context
	    for (String s : this.mainModel.getArrayVars().keySet()) {
			//this.baseContext.put(s,this.mainModel.getArrayVars().get(s).getValue());
	    	this.baseContext.put(s,this.mainModel.getArrayVars().get(s));
		}
	    //this.baseContext.put("variables", this.mainModel.getArrayVars());
	    
	    try{
			URL source =  new URL(this.mainModel.getBaseTemplatePath());
//			props.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.URLResourceLoader");
//			props.put("url.resource.loader.root", this.mainModel.getBaseTemplatePath());
//			props.put("url.resource.loader.cache", "true");
			
		    props.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
	        props.setProperty("url.resource.loader.description", "Velocity URL Resource Loader");
	        props.setProperty("url.resource.loader.class", URLResourceLoader.class.getName());
	        props.setProperty("url.resource.loader.root", source.toString());
		}catch(Exception a) {
			log.warn(a);
			 props.put("file.resource.loader.path", this.mainModel.getBaseTemplatePath());
		}

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
	 *  Sets the output folder to generated files.
	 * @param outputFolder path to output folder. If the folder not exists, the plugin creates it into given path.
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder=outputFolder;
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
	private boolean control() {
		boolean flag=false;
		if(this.ontologies2BProcesed.size() > 0 ) {
			if(this.mainModel!=null) {
				flag=true;
				if(this.localBaseLoaderPath == null) {

						flag=true;
				}
			}else {
				
				log.fatal("Seems the Xml Parser object is not set, please check it Editar");
			}
		}else {
			log.fatal("Main ontology couldn't be loaded");
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
			log.warn("Reference to null variable.");
			return false;
		}

		if (this.mainModel.getArrayVars().get(varName) == null) {
			log.warn("Undeclared variable: "+ varName + ", adding variable.");
			this.mainModel.getArrayVars().put(varName, new Variable(varName, "Undeclared variable, added automatically.",false, varValue));
		}

		this.mainModel.getArrayVars().get(varName).setValue(varValue);
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
	 * Method to return {@link Set}<{@link Variable}> of all variables given in xml file.
	 * @return {@link set}<{@link Variable}> of {@link  Variable} loaded from user
	 */
	public Map<String, Variable> getVariablesArray(){
		return this.mainModel.getArrayVars();
	}

	/**
	 * Load recursivesly (if is allowed) given ontology. If given ontology is null, {@link #getOntologies()}
	 * returns a empty Set of {@link OWLOntology}
	 *
	 * @param ont {@link OWLOntology} to be load
	 * @param recursive {@link Boolean} value indicating recursive load
	 */
	public void addOntology( OWLOntology ont, boolean recursive){
		if (recursive) {
		ontologies2BProcesed.addAll(ont.getImports());
		}
		ontologies2BProcesed.add(ont);

	}

	public String getOutputDir() {
		return outputFolder;
	}

	public Properties getProps() {
		return props;
	}
	public void setProps(Properties props) {
		this.props = props;
	}
	public TemplateDataModel getMainModel() {
		return mainModel;
	}
	public void setMainModel(  TemplateDataModel mainModel) {
		this.mainModel = mainModel;
	}
	
	/**
	 * method to add each variable defined into XML coordinator to velocity context
	 */
	private void addVariablesToBaseContext() {
		log.debug("adding variables into base context");
		this.variables.keySet().stream().forEach(t ->this.baseContext.put(t, this.variables.get(t)));

	}

	private void update(int done) {
		if(GenConf!=null) {
			GenConf.updateProgress(4, done);
		}
	}
	
	public List<Exception> getErrors(){
		return this.arrayOfExceptions;
	}
	
	public void addError(Exception a) {
		this.arrayOfExceptions.add(a);
	}

}
