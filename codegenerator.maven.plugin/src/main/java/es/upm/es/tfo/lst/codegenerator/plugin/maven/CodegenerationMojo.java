package es.upm.es.tfo.lst.codegenerator.plugin.maven;


import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * Goal which touches a timestamp file.
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class CodegenerationMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    /**
     * Location of the XML defining the template system.
     */
    @Parameter(property="template", required = true)
    private URL xmlTemplate;


    /**
     * Location of the folder containing local ontologies to process. Only files with .owl extension will be processed.
     * If it refers to a single file, it will be processed as given ontology.
     * Ontologies added through this method will be processed as well as any added through {@link CodegenerationMojo#remoteOntologies}
     */
    @Parameter(property = "local-Ontologies", defaultValue = "${project.basedir}/src/main/owl/")
    private File localOntologies;

    //private List<String> includes;
    //TODO maybe if needed

    //private List<String> excludes;
    //TODO maybe if needed

    /**
     * list of URL of ontologies to process.
     * Ontologies added through this method will be proccesed as well as any added through {@link CodegenerationMojo#localOntologies}
     */
    @Parameter(property = "remote-Ontologies")
    private List<URL> remoteOntologies = Collections.emptyList();

    /**
     * list of ontologies to process recursively.
     */
    @Parameter(property = "recursive-process-for")
    private List<String> recursiveOntologies = Collections.emptyList();

    /**
     * Variables to add to the Velocity macros.
     */
    @Parameter(property = "variables")
    private Map<String, String> variables= Collections.emptyMap();

    public void execute()
        throws MojoExecutionException
    {
    
    	if (xmlTemplate == null) {
    		throw new MojoExecutionException("Invalid XML Template referece (null).");
    	}
    	getLog().info("Generating code from Ontologies");
    	// set template & init project
		XmlParser parser = new XmlParser();
		parser.generateXMLCoordinator(xmlTemplate);
		TemplateDataModel model = parser.getXmlCoordinatorDataModel();
		if (model == null) {
			throw new MojoExecutionException("Invalid XML coordinator template: " + xmlTemplate.toString());
		}
		
		GenerateProject gp = new GenerateProject();
		gp.setMainModel(model);
		
		//bug in 97...NPE
		gp.setLocalBaseLoaderPath(parser.getTemplateBasePath().getPath());
		OntologyLoader ontologyLoader = new OntologyLoader();
		
		if (localOntologies.exists() && localOntologies.isDirectory()) {
				File[] onts = localOntologies.listFiles(new FilenameFilter() {
	
					@Override
					public boolean accept(File dir, String name) {

						return name.toLowerCase().contains(".owl");
					}
				});
				
				for (int i = 0; i < onts.length; i++) {
					final String url = onts[i].toPath().toString();
					boolean recursive = recursiveOntologies.stream().anyMatch(new Predicate<String>() {
	
						@Override
						public boolean test(String t) {
							return t.contains(url) || url.contains(t);
						}
					});
			    	getLog().info("\t adding Ontology : " + url + (recursive?" rescurively":" ") + "to project");
					gp.addOntology(ontologyLoader.loadOntology(url), recursive);
				}
		}
		
		if (localOntologies.exists() && localOntologies.isFile()) {
			String url = localOntologies.toPath().toString();
			boolean recursive = recursiveOntologies.contains(url);
	    	getLog().info("\t adding Ontology : " + url + (recursive?" rescurively":" ") + "to project");			
			gp.addOntology(ontologyLoader.loadOntology(url), recursive);
		}

	
		
		// add remote ontologies
		for (URL url : remoteOntologies) {
			boolean recursive = recursiveOntologies.contains(url.toString());
	    	getLog().info("\t adding Ontology : " + url + (recursive?" rescurively":" ") + "to project");
			gp.addOntology(ontologyLoader.loadOntology(url.toString()), recursive);
		}
		 
		
		
		// add variables
		for (Entry<String, String> entry : variables.entrySet()) {
			gp.setVariable(entry.getKey(), entry.getValue());
			getLog().info("\tvariable :" + entry.getKey() + "\t\tset to: " + entry.getValue());
		}

		// set output
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }
        getLog().info("\toutputing project to: " + outputDirectory.getAbsolutePath());
        gp.setOutputFolder(outputDirectory.getAbsolutePath());
        // generate
        boolean result;
        try {
        	result = gp.process();
        } catch (Exception e) {
        	throw new MojoExecutionException("unable to generate code.", e);
        }

        if (!result) {
        	throw new MojoExecutionException("Code generation was not successuful.");
        }
        getLog().info("generation completed.");
        
        


    }
}
