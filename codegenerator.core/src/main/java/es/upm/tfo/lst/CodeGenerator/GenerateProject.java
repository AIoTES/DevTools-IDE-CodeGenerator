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
package es.upm.tfo.lst.CodeGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.upm.tfo.lst.CodeGenerator.exception.MissingRequiredVariableValueException;
import es.upm.tfo.lst.CodeGenerator.exception.OntologyException;
import es.upm.tfo.lst.CodeGenerator.exception.XmlCoordinatorException;
import es.upm.tfo.lst.CodeGenerator.model.MacroModel;
import es.upm.tfo.lst.CodeGenerator.model.Project;
import es.upm.tfo.lst.CodeGenerator.model.ReasonerWrapper;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;
import uk.ac.manchester.cs.jfact.JFactFactory;

/**
 * Main class.  Reads {@link OWLOntology} and XML, to generate code. In this
 * class the tool has all the methods to read ontologies and explore all of it
 * aspects to generate code. <br>
 * <br>
 * <br>
 * <u><b>information to velocity template developer</b></u><br>
 * To develop templates, user need some information to data disponibility into
 * velocity context. The developer have access to ontology into velocity macro
 * from the next keys
 * <ul>
 * <li>{@link Set}<{@link Variable}> under key variables: to access to all
 * variables given in XML file</li>
 * <li>{@link OWLOntology} under key ontology: to accecs to the current
 * processing ontology</li>
 * <li>{@link OWLClass } under key class: to acces to current processing
 * class</li>
 * <li>{@link Set}<{@link OWLNamedIndividual} under key instances: to acces to
 * all of instances to processing class></li>
 * <li>{@link Set}<{@link NodeSet}<{@link OWLNamedIndividual}>> under tag
 * propertieValues: the Set contains a NodeSet of OWLNAmedIndividual for each
 * instance of actual processing class</li>
 * </ul>
 *
 * @author Buhid Eduardo
 *
 */
public class GenerateProject {

	public interface ProgessCallbackPublisher {
		void updateProgress(int done);
	}

	private ReasonerWrapper wrapper = null;
	private TemplateDataModel mainModel = null;
	private VelocityContext  baseContext;
	private VelocityEngine vel_eng;
	private FileWriter fr;
	private Template template;
	private OWLReasonerFactory reasonerFactory;
	private OWLReasoner reasoner = null;
	private Map<String, Variable> variables;
	private String outputFolder = null;
	private String text = "";
	private List<Exception> arrayOfExceptions = null;
	private URL urlBasePath = null;
	private Properties props;
	private List<OWLOntology> ontologies2BProcesed = new ArrayList<>();
	private final static Logger log = Logger.getLogger(GenerateProject.class);
	private int total2Process = 0;
	public ProgessCallbackPublisher GenConf;

	public GenerateProject(TemplateDataModel model, Properties velocityProperties) {
		this.mainModel = model;
		this.reasonerFactory = new JFactFactory();
		this.props = velocityProperties;
		this.variables = new HashMap<String, Variable>();
		if (this.mainModel != null)
			this.variables = this.mainModel.getArrayVars();
		this.wrapper = new ReasonerWrapper();
		this.arrayOfExceptions = new ArrayList<>();
	}

	/**
	 * The class constructor initialize the {@link OWLReasonerFactory},
	 * {@link Properties} and some needed objects to use in velocity macro
	 *
	 * @param p     {@link Project} object who give the list of ontologies to be
	 *              process.
	 * @param model {@link TemplateDataModel} object builded from XML given file.
	 */
	public GenerateProject(TemplateDataModel model) {
		this(model, defaultVelocityProperties());
	}

	/**
	 * If user initializes project without parsing the XmlTemplateModel, user must
	 * set it later, variables array will be empty.
	 */
	public GenerateProject() {
		this(null, defaultVelocityProperties());

	}

	/**
	 * Initialize the process to generate code before use this method, check if main
	 * ontology is loaded correctly, otherwise the program will not continue and
	 * send an error notification
	 *
	 * @return boolean value.True if the process is sucessfull.False if any problem
	 *         occur.
	 * @throws Exception if some problem occur in the process
	 */
	public void process() throws Exception {
		this.calculateTotal();
		if (this.control()) {
			try {
				this.initVelocity();
				
				//this.baseContext.put("ontologyCompleteList",this.ontologies2BProcesed.stream().collect(Collectors.toList()));
				//this.baseContext.put("output", this.outputFolder);
				this.baseContext.put("date", new Date());
				
				//TODO: control variables to not be null after parse this to context
				this.baseContext.put("project",this);
				this.addVariablesToBaseContext();

				if (this.mainModel.getProjectMacros().isEmpty())
					log.warn("doesn't exist macro to project");
				if (this.mainModel.getOntologyMacros().isEmpty())
					log.warn("doesn't exist macro to ontology");
				if (this.mainModel.getClassMacros().isEmpty())
					log.warn("doesn't exist macro to class");
				if (this.mainModel.getInstanceMacros().isEmpty())
					log.warn("doesn't exist macro to instances");
				if (this.mainModel.getObjectProperties().isEmpty())
					log.warn("doesn't exist macro to object properties");

				this.processProject();
			} catch (Exception a) {
				log.fatal("fatal error ", a);
				this.arrayOfExceptions.add(a);
				throw a;
			}
		}

	}

	/**
	 * Mehod to process the project itself.
	 *
	 * @return @return boolean value.True if the process is sucessfull.False if any
	 *         problem occur.
	 * @throws Exception
	 */
	private void processProject() throws Exception {
		String text=null;
		List<MacroModel> projectModelArray = this.mainModel.getProjectMacros();
		
		if (!projectModelArray.isEmpty()) {
			for (MacroModel projectModel : projectModelArray) {
				VelocityContext context = new VelocityContext(this.baseContext);
				text = new String(this.processOutputString(projectModel.getOutput(),context));
				File outputFolder = new File(this.outputFolder + text);
				
				if (!outputFolder.getParentFile().exists())
					outputFolder.getParentFile().mkdir();
				
				this.addImportsToContext(context,projectModel);

				if (!text.equals("")) {
					try {
						// throw ResourceNotFoundException
						template = vel_eng.getTemplate(projectModel.getTemplateName());
						// throw IOE
						this.fr = new FileWriter(this.outputFolder + text, true);
						template.merge(context, fr);
						fr.close();
						outputFolder=null;
						context=null;
						text=null;
					} catch (Exception e) {
						log.fatal("cant merge velocity template with velocity context", e);
						this.arrayOfExceptions.add(e);
						throw e;
					}
				}else
					log.warn("empty or errors in output tag content in projectModel...skipping...");
			}
		}
			
			for (OWLOntology ontology : this.ontologies2BProcesed) {
				this.reasoner = this.reasonerFactory.createReasoner(ontology);
				this.wrapper.setReasoner(this.reasoner);
				this.baseContext.put("reasoner", this.wrapper);
				this.processOntology(ontology);
			}

		

	}

	/**
	 * Process all macros for ontology.
	 *
	 * @param ontology {@link OWLOntology} to process
	 * @return boolean value.True if the process is sucessfull.False if any problem
	 *         occur.
	 * @throws Exception
	 */
	private void processOntology(OWLOntology ontology) throws Exception {
		
		String text = null;
		List<MacroModel> ontologyModelArray = this.mainModel.getOntologyMacros();
		//this.baseContext.put("ontology", ontology);
		if (!ontologyModelArray.isEmpty()) {
			
			for (MacroModel ontologyModel : ontologyModelArray) {

				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("ontology", ontology);
				this.addImportsToContext(context,ontologyModel);
				text = new String(this.processOutputString(ontologyModel.getOutput(),context));
				File outputFolder = new File(this.outputFolder + text);
				if (!outputFolder.getParentFile().exists())
					outputFolder.getParentFile().mkdirs();

				if (!text.equals("")) {
					template = vel_eng.getTemplate(ontologyModel.getTemplateName());
					// throws IOE
					try {
						this.fr = new FileWriter(this.outputFolder + text, true);
						template.merge(context, fr);
						fr.close();
						text=null;
						context=null;
						outputFolder=null;
					} catch (Exception e) {
						this.arrayOfExceptions.add(e);
						log.fatal("cant merge velocity template with velocity context", e);
						throw e;
					}

				}else 
					log.warn("empty output tag in ontologyModel...skipping...");

				this.processAnnotations(ontology);
				
				for (OWLAxiom axiom : ontology.getAxioms()) {
					if (axiom.isOfType(AxiomType.DECLARATION) && ((OWLDeclarationAxiom) axiom).getEntity().isOWLClass()) {
						OWLClass cls = (OWLClass) ((OWLDeclarationAxiom) axiom).getEntity();
						this.processClass(cls, ontology);
					}
				}

			}
		} else {

			for (OWLAxiom axiom : ontology.getAxioms()) {
				if (axiom.isOfType(AxiomType.DECLARATION) && ((OWLDeclarationAxiom) axiom).getEntity().isOWLClass()) {
					OWLClass cls = (OWLClass) ((OWLDeclarationAxiom) axiom).getEntity();
					this.processClass(cls, ontology);
				}
			}
		}

	}

	/**
	 * Process macros for classes.
	 *
	 * @param c        {@link OWLClass} to process
	 * @param ontology {@link OWLOntology } to process
	 * @throws Exception
	 */
	private void processClass(OWLClass c, OWLOntology ontology) throws Exception {
		int i = 0;
		String text = null;
		List<MacroModel> classModelArray = this.mainModel.getClassMacros();
		//this.baseContext.put("class", c);
		if (!classModelArray.isEmpty()) {
			//initialize context and merge it with base context
			
			for (MacroModel macroModel : classModelArray) {
				
				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("class", c);
				context.put("ontology", ontology);
				this.addImportsToContext(context,macroModel);
				text = new String(this.processOutputString(macroModel.getOutput(),context));
				File outputFile = new File(this.outputFolder + text);
				
				if (!outputFile.getParentFile().exists())
					outputFile.getParentFile().mkdirs();
				if (!text.equals("")) {
					template = vel_eng.getTemplate(macroModel.getTemplateName());
					// TODO heare throws IOE
					try {
						this.fr = new FileWriter(this.outputFolder + text, true);
						template.merge(context, fr);
						fr.close();
						outputFile=null;
						context=null;
						text=null;
					} catch (Exception e) {
						this.arrayOfExceptions.add(e);
						log.fatal("cant merge velocity template with velocity context", e);
						throw e;
					}
				}

			}
		
			
		} else {
			update(i++);
		}
		this.processNamedIndividual(c, ontology);
	}

	/**
	 * 
	 * @param cls {@link OWLClass}
	 * @param ontology {@link OWLOntology} 
	 * @throws Exception
	 */
	private void processNamedIndividual(OWLClass cls,OWLOntology ontology) throws Exception {
		String text = null;

		if (!this.mainModel.getInstanceMacros().isEmpty()) {
			//initialize and merge current context with base context

			for (MacroModel instancesMacro : this.mainModel.getInstanceMacros()) {
				
				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("class", cls);
				context.put("ontology",ontology);
				this.addImportsToContext(context, instancesMacro);
				
				for (OWLDifferentIndividualsAxiom individual : ontology.getAxioms(AxiomType.DIFFERENT_INDIVIDUALS)) {
					//initialize tenplate object with template given in XML file
					template = vel_eng.getTemplate(instancesMacro.getTemplateName()); 
					context.put("NamedIndividual", individual);
					text =new String(this.processOutputString(instancesMacro.getOutput(),context));
					File outputFolder = new File(this.outputFolder +text);
					if (!outputFolder.getParentFile().exists())
						outputFolder.getParentFile().mkdirs();
					// TODO throws IOE
					if(!text.equals("")) {
						try {
							this.fr = new FileWriter(this.outputFolder + text, true);
							template.merge(context, fr);
							fr.close();
							outputFolder=null;
							text=null;
							context=null;
						} catch (Exception e) {
							this.arrayOfExceptions.add(e);
							log.fatal("cant merge velocity template with velocity context", e);
							throw e;
						}
					}else
						log.warn("empty output tag in instancesMacro...skipping...");
					//maybe need class too?
					this.processObjectProperties(individual,  ontology);
					this.processDataPropeties(individual,  ontology);
				}	
			}
			
		} else {
			//maybe need class/individuals too?
			this.processObjectProperties(null,  ontology);
			this.processDataPropeties(null,  ontology);
			

		}
		// update(4);
	}
	/**
	 * 
	 * @param individual {@link OWLNamedIndividual}
	 * @param ontology {@link OWLOntology}
	 * @throws Exception
	 */
	private void processObjectProperties(OWLDifferentIndividualsAxiom individual, OWLOntology ontology)throws Exception {
		String text = null;
		
		if (!this.mainModel.getObjectProperties().isEmpty()) {
			for (MacroModel macroObjectProperties : this.mainModel.getObjectProperties()) {
				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("ontology",ontology);
				context.put("NamedIndividual",individual);
				this.addImportsToContext(context, macroObjectProperties);
				text = new String(this.processOutputString(macroObjectProperties.getOutput(),context));
				File outputFolder = new File(this.outputFolder + text);

				if (!outputFolder.getParentFile().exists())
					outputFolder.getParentFile().mkdirs();

				if (!text.equals("")) {
					try {
						this.fr = new FileWriter(this.outputFolder + text, true);
						template = vel_eng.getTemplate(macroObjectProperties.getTemplateName());
						template.merge(context, fr);
						fr.close();
						outputFolder=null;
						text=null;
						context=null;
					} catch (Exception e) {
						this.arrayOfExceptions.add(e);
						log.fatal("cant merge velocity template with velocity context", e);
						throw e;
					}

				}else
					log.warn("empty output tag in macroObjectProperties...skipping...");

			}
		}


	}
	
	/**
	 * @param cls
	 * @param instances
	 * @param ontology
	 * @throws Exception 
	 */
	private void processDataPropeties(OWLDifferentIndividualsAxiom individual, OWLOntology ontology) throws Exception {
		String text =null;
		if (!this.mainModel.getObjectProperties().isEmpty()) {
			for (MacroModel macroDataPropeties : this.mainModel.getDataProperties()) {
				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("NamedIndividual",individual);
				context.put("ontology",ontology);

				this.addImportsToContext(context,macroDataPropeties);
				text = new String( this.processOutputString(macroDataPropeties.getOutput(),context));
				File outputFolder = new File(this.outputFolder + text);

				if (!outputFolder.getParentFile().exists())
					outputFolder.getParentFile().mkdirs();

				if (!text.equals("")) {
					try {
						this.fr = new FileWriter(this.outputFolder + this.text, true);
						template = vel_eng.getTemplate(macroDataPropeties.getTemplateName());
						template.merge(context, fr);
						fr.close();
						text=null;
						context=null; 
						outputFolder=null;	
					} catch (Exception e) {
						this.arrayOfExceptions.add(e);
						log.fatal("cant merge velocity template with velocity context", e);
						throw e;
					}

				}else
					log.warn("empty output tag in macroDataPropeties...skipping...");

			}
		}
//		
//		for (OWLDataPropertyDomainAxiom item : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
////			System.out.println("DATA_PROPERTY_DOMAIN "+item);
//		}
//		System.out.println("------");
//		for (OWLDataPropertyAssertionAxiom item : ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION)) {
////			System.out.println("DATA_PROPERTY_ASSERTION "+item);
//		}
//		System.out.println("------");
//		for (OWLDataPropertyRangeAxiom item : ontology.getAxioms(AxiomType.DATA_PROPERTY_RANGE)) {
////			System.out.println("DATA_PROPERTY_RANGE "+item);
//		}
		
	}
	
	//TODO: define this step
	private void processAnnotations(OWLOntology ontology) throws Exception{
		System.out.println("processAnnotations");
		String text=null;
		List<MacroModel> annotationsModelArray = this.mainModel.getAnnotationsMacros();
		
		if (!annotationsModelArray.isEmpty()) {
			
			for (MacroModel annotationModel : annotationsModelArray) {
				
				VelocityContext context = new VelocityContext(this.baseContext);
				context.put("ontology", ontology);
				text = new String(this.processOutputString(annotationModel.getOutput(),context));
				File outputFolder = new File(this.outputFolder + text);
				
				if (!outputFolder.getParentFile().exists())
					outputFolder.getParentFile().mkdir();
				
				this.addImportsToContext(context,annotationModel);

				if (!text.equals("")) {
					try {
						// throw ResourceNotFoundException
						template = vel_eng.getTemplate(annotationModel.getTemplateName());
						// throw IOE
						this.fr = new FileWriter(this.outputFolder + text, true);
						template.merge(context, fr);
						fr.close();
						outputFolder=null;
						context=null;
						text=null;
					} catch (Exception e) {
						log.fatal("cant merge velocity template with velocity context", e);
						this.arrayOfExceptions.add(e);
						throw e;
					}
				}else
					log.warn("empty or errors in output tag content in annotationModel...skipping...");

			}
		} 
//		else {
//			for (OWLOntology ont : this.ontologies2BProcesed) {
//				this.reasoner = this.reasonerFactory.createReasoner(ont);
//				this.wrapper.setReasoner(this.reasoner);
//				this.baseContext.put("reasoner", this.wrapper);
//				this.processOntology(ont);
//			}
//
//		}
	
	}
	
	private static Properties defaultVelocityProperties() {
		Properties props = new Properties();
		props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		return props;
	}

	/**
	 * Initialize VelocityEngine and base context who hold base data to use in all
	 * contexts. In this method is initialized the base context who have all data
	 * wich is needed for all macros like variables added into XML file.
	 *
	 * @throws Exception
	 */
	private void initVelocity() throws Exception {
		vel_eng = new VelocityEngine();
		this.baseContext = new VelocityContext();
		// adding separately each variable to velocity context
		for (String s : this.mainModel.getArrayVars().keySet()) {
			this.baseContext.put(s, this.mainModel.getArrayVars().get(s));
		}
		

		try {
			URL source = new URL(this.mainModel.getBaseTemplatePath());
//			props.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.URLResourceLoader");
//			props.put("url.resource.loader.root", this.mainModel.getBaseTemplatePath());
//			props.put("url.resource.loader.cache", "true");

			props.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
			props.setProperty("url.resource.loader.description", "Velocity URL Resource Loader");
			props.setProperty("url.resource.loader.class", URLResourceLoader.class.getName());
			props.setProperty("url.resource.loader.root", source.toString());
		} catch (Exception a) {
			log.warn(a);
			props.put("file.resource.loader.path", this.mainModel.getBaseTemplatePath());
		}

		vel_eng.init(props);

	}


	/**
	 * Sets the output folder to generated files.
	 *
	 * @param outputFolder path to output folder. If the folder not exists, the
	 *                     plugin creates it into given path.
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * all of controls before ejecute project:
	 * <ul>
	 * <li>Control if is loaded main ontology</li>
	 * <li>Control if XML file is loaded correctly</li>
	 * <li>Control if velocity macros directory loader is setted</li>
	 * <li>Control required variables count</li>
	 * </ul>
	 *
	 * @return boolean value indicating result of control
	 * @throws MissingRequiredVariableValueException
	 */
	private boolean control() {
		boolean flag = false;
		if (this.ontologies2BProcesed.size() > 0) {
			if (this.mainModel != null) {

				if (!(this.mainModel.getBaseTemplatePath().equals(""))
						|| !(this.mainModel.getBaseTemplatePath() == null)) {
					if (this.mainModel.getMacroList().size() != 0) {
						if (this.outputFolder != null) {
							flag = true;
						} else {
							log.fatal("The output directory to store generated files is not set. Please check it");
							this.arrayOfExceptions.add(new IOException(
									"The output directory to store generated files is not set. Please check it"));
						}

					} else {
						log.fatal("Seems in the XML coordinator, the macro tag is empty or not exist. Please check it");
						this.arrayOfExceptions.add(new XmlCoordinatorException(
								"Seems in the XML coordinator the maro tag is empty or not exist. Plesase ckeck it"));
					}

				} else {
					log.fatal(
							"Seems the directory to load templates is not set. Please check if the Xml coordinator file is loaded correctly");
					this.arrayOfExceptions.add(new IOException(
							"Seems the directory to load templates is not set. Please check if the Xml coordinator is loaded correctly"));
				}

			} else {
				log.fatal("Seems the Xml Parser object is not set, please check it");
				this.arrayOfExceptions
						.add(new XmlCoordinatorException("Seems the Xml Parser object is not set, please check it"));
			}
		} else {
			log.fatal("Main ontology couldn't be loaded");
			this.arrayOfExceptions.add(new OntologyException("Main ontology couldn't be loaded"));
		}
		return flag;
	}

	/**
	 * Process the velocity code given in xml file. The purpose of this method is
	 * process velocity code and return string value of result of the process.
	 *
	 * @param toProcess       String to parse to velocity to process
	 * @param VelocityContext containig needed references
	 * @return {@link String } value, result of the process
	 */
	private String processOutputString(String toProcess,VelocityContext ctx) {
		String t = "--";
		try {
			StringWriter fw = new StringWriter();
			StringResourceRepository rep = StringResourceLoader.getRepository();
			
			Template te = new Template();
			RuntimeServices rs = RuntimeSingleton.getRuntimeServices();
			StringReader sr = new StringReader(toProcess);
			SimpleNode sn = rs.parse(sr, "s");
			rep = new StringResourceRepositoryImpl();
			StringResourceLoader.setRepository(StringResourceLoader.REPOSITORY_NAME_DEFAULT, rep);

			te.setRuntimeServices(rs);
			te.setData(sn);
			te.initDocument();

			te.merge(ctx, fw);
			t = fw.toString();
			fw.close();
		} catch (Exception a) {
			log.fatal("cant process name ", a);
		}
		return t;
	}

	/**
	 * Method to add variable into project. Replaces any existing value.
	 *
	 * @param varName  the name of the {@link Variable} (as in
	 *                 {@link Variable#getName()})
	 * @param varValue the run value of the variable, if null, removes variable.
	 * @return boolean
	 */
	public boolean setVariable(String varName, String varValue) {
		if (varName == null) {
			log.warn("Reference to null variable.");
			return false;
		}

		if (this.mainModel.getArrayVars().get(varName) == null) {
			log.warn("Undeclared variable: " + varName + ", adding variable.");
			this.mainModel.getArrayVars().put(varName,
					new Variable(varName, "Undeclared variable, added automatically.", false, varValue));
		}

		this.mainModel.getArrayVars().get(varName).setValue(varValue);
		return true;
	}

	/**
	 * Method to return {@link Set}<{@link Variable}> of all variables given in xml
	 * file.
	 *
	 * @return {@link set}<{@link Variable}> of {@link Variable} loaded from user
	 */
	public Map<String, Variable> getVariablesArray() {
		return this.mainModel.getArrayVars();
	}

	/**
	 * Load recursivesly (if is allowed) given ontology. If given ontology is null,
	 * {@link #getOntologies()} returns a empty Set of {@link OWLOntology}
	 *
	 * @param ont       {@link OWLOntology} to be load
	 * @param recursive {@link Boolean} value indicating recursive load
	 */
	public void addOntology(OWLOntology ont, boolean recursive) {

		if (recursive) {
			ontologies2BProcesed.addAll(ont.getImports());
		}
		ontologies2BProcesed.add(ont);
		
		Collections.sort(ontologies2BProcesed, new Comparator<OWLOntology>() {

			@Override
			public int compare(OWLOntology o1, OWLOntology o2) {
				if(o1.getImports().contains(o2)) return -1;
				else if(o1.getImports().contains(o2)) return 1;
				else return 0;
			}
		});
	}
	/**
	 *   
	 * @return {@link String} : path to output directory
	 */
	public String getOutputDir() {
		return outputFolder;
	}

	/**
	 * 
	 * @return {@link Properties} object containing the velocity properties
	 */
	public Properties getProps() {
		return props;
	}
	/**
	 * 
	 * @param props {@link Properties} to add in velocity 
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * 
	 * @return {@link TemplateDataModel} object representing the XML file
	 */
	public TemplateDataModel getMainModel() {
		return mainModel;
	}

	/**
	 * 
	 * @param mainModel {@link TemplateDataModel} object representing the XML file
	 */
	public void setMainModel(TemplateDataModel mainModel) {
		this.mainModel = mainModel;
	}

	/**
	 * method to add each variable defined into XML coordinator to velocity context
	 */
	private void addVariablesToBaseContext() {
		log.debug("adding variables into base context...");
		this.variables.keySet().stream().forEach(t -> this.baseContext.put(t, this.variables.get(t)));

	}

	private void update(int done) {
		if (GenConf != null) {
			GenConf.updateProgress(done);
		}
	}

	/**
	 * Method to obtain a {@link List} of the thrown exceptions during the process.
	 *
	 * @return {@link List}<{@link Exception}}> containing all thrown exceptions
	 */
	public List<Exception> getErrors() {
		return this.arrayOfExceptions;
	}
	/**
	 * method to add all exceptions into a collection.
	 * 
	 * @param a {@link Exception} 
	 */
	public void addError(Exception a) {
		this.arrayOfExceptions.add(a);
	}

	private void calculateTotal() {
		for (OWLOntology o : this.ontologies2BProcesed) {
			this.total2Process += o.getClassesInSignature().size();
		}
		System.out.println("total of classes to process " + this.total2Process);
	}

	public int getTotal2Process() {
		return total2Process;
	}
	
	/**
	 * To add the imports of static classes into context
	 * @param model
	 */

	private void addImportsToContext(VelocityContext context, MacroModel model) {
		for (Map<String, String> key : model.getImports()) {
			for (String k : key.keySet()) {
				context.put(k, new FieldMethodizer(key.get(k)) );
			}

		}
	}
	
	/**
	 * method to get all imported ontologies plus actual ontology
	 * @return {@link Set} < {@link OWLOntology} > of ontologies
	 */
	public List<OWLOntology> getOntologies2BProcesed() {
		return ontologies2BProcesed;
	}
	

	

}
