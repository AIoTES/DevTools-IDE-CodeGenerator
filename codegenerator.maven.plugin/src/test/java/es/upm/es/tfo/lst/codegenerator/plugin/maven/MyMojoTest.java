package es.upm.es.tfo.lst.codegenerator.plugin.maven;


import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class MyMojoTest 
{
    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable
        {
        }

        @Override
        protected void after()
        {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testSomething()
            throws Exception
    {
        File pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        CodegenerationMojo myMojo = ( CodegenerationMojo ) rule.lookupConfiguredMojo( pom, "generate" );
//        CodegenerationMojo myMojo = ( CodegenerationMojo ) lookupMojo("generate", pom);
        assertNotNull( myMojo );
//        URL xmlTemplate = (URL) rule.getVariableValueFromObject( myMojo, "xmlTemplate" );
//        assertNotNull(xmlTemplate);
//        InputStream in = xmlTemplate.openStream();
//        assertNotNull(in);
//        int size = 0;
//        byte[] buffer = new byte[1024];
//        while ((size = in.read(buffer)) != -1) System.out.write(buffer, 0, size);
        PlexusConfiguration config = rule.extractPluginConfiguration("codegenerator.maven.plugin", new File(pom, "pom.xml"));
        rule.configureMojo(myMojo, config);
        myMojo.execute();

//        File outputDirectory = ( File ) rule.getVariableValueFromObject( myMojo, "outputDirectory" );
//        assertNotNull( outputDirectory );
//        assertTrue( outputDirectory.exists() );
//
//        File touch = new File( outputDirectory, "touch.txt" );
//        assertTrue( touch.exists() );

    }



}

