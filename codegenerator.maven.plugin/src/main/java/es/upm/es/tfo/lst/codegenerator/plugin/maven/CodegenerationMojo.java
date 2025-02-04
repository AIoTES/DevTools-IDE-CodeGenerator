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
package es.upm.es.tfo.lst.codegenerator.plugin.maven;


import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.apache.log4j.BasicConfigurator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.owl.OntologyLoader;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;

/**
 * Goal which touches a generates code from the given configuration.
 */
@Mojo(
		name = "generate",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.COMPILE,
		requiresProject = true)
public class CodegenerationMojo
    extends AbstractMojo
{
    /**
     * Location of the output.
     */
    @Parameter( defaultValue = "${project.build.directory}/generated-sources/owl-gen", property = "outputDir", required = true )
    private File outputDirectory;

    /**
     * Location of the XML defining the template system.
     */
    @Parameter(property="template", required = true)
    private String xmlTemplate;


    /**
     * Location of the folder containing local ontologies to process. Only files with .owl extension will be processed.
     * If it refers to a single file, it will be processed as given ontology.
     * Ontologies added through this method will be processed as well as any added through {@link CodegenerationMojo#remoteOntologies}
     */
    @Parameter(property = "local-Ontologies", defaultValue = "${project.basedir}/src/main/owl/")
    private File localOntologies;

    //private List<String> includes;
    //XXX maybe if needed

    //private List<String> excludes;
    //XXX maybe if needed

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

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
    	Properties props = null;
        org.apache.maven.plugin.logging.Log mavenLogger = getLog();
        BasicConfigurator.configure(new MavenLoggerLog4jBridge(mavenLogger));
    	

    	if (xmlTemplate == null) {
    		throw new MojoExecutionException("Invalid XML Template referece (null).");
    	}

    	getLog().info("Generating code from Ontologies");
    	getLog().info("xml template: "+ xmlTemplate);

    	// set template & init project
		XmlParser parser = new XmlParser();
		TemplateDataModel model=null;
		try {
			 
			//http://velocity.apache.org/engine/1.7/developer-guide.html#configuring-resource-loaders
		    //https://velocity.apache.org/engine/2.0/apidocs/org/apache/velocity/runtime/resource/loader/JarResourceLoader.html
			if(xmlTemplate.startsWith("jar")) {
				props  = new Properties();
				props.setProperty("url.resource.loader.description", "Velocity JAR Resource Loader");
				props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
				props.setProperty(RuntimeConstants.RESOURCE_LOADER, "jar");
				props.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
				props.setProperty("jar.resource.loader.path", xmlTemplate.substring(0, xmlTemplate.lastIndexOf("!")));
			}
			if(xmlTemplate.startsWith("url")) {
				//TODO implement
			}
			if(xmlTemplate.startsWith("file")) {
				//TODO implement
			}
			model = parser.generateXMLCoordinator(xmlTemplate);
		} catch (Exception e1) {
        	throw new MojoExecutionException("Invalid XML coordinator template: " + xmlTemplate, e1);
		}

		if (model == null) {
			throw new MojoExecutionException("Invalid XML coordinator template: " + xmlTemplate);
		}
		
		GenerateProject gp = new GenerateProject();
		gp.setMainModel(model);
		if(props != null) 
			gp.setProperties(props);
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
        gp.setOutputFolder(outputDirectory);
        // generate
        try {
        	//result = gp.process();
        	 gp.process();
        	 addSourceRoot(outputDirectory);
        } catch (Exception e) {
        	getLog().debug(e);
        	throw new MojoExecutionException("unable to generate code.");
        }

        getLog().info("generation completed.");

    }

    void addSourceRoot(File outputDir) {
    	project.addCompileSourceRoot(outputDir.getPath());
    }
}
