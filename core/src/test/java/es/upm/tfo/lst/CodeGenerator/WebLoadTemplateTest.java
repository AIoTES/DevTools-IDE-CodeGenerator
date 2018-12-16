package es.upm.tfo.lst.CodeGenerator;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import es.upm.tfo.lst.webprocess.ProcessWebContent;


public class WebLoadTemplateTest {

	private ProcessWebContent webContent=null;
	private String templateDir="http://localhost/templates/simple.xml";
	private String templateDirIncorrect="http://localhost/templates/empty/simple.xml";
	private String testOutputDir="target/webTemplateTest/";
	@Before
	public void init(){
		try {
			File f = new File(this.testOutputDir);
			if (!f.exists()) f.mkdir();
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		webContent = new ProcessWebContent();
		webContent.setOutput(this.testOutputDir);
	}
	
	@Test
	public void loadFromValidURL() {
		String resp="null";
		try {
			resp=this.webContent.processURL(new URL(templateDir));
			assertNotNull(resp);
			
		}catch (Exception e) {
				
		}
	}
	@Test
	public void loadFromInvalidURL() {
		String resp="null";
		try {
			resp=this.webContent.processURL(new URL(templateDirIncorrect));
			assertNull(resp);
		}catch (Exception e) {
				
		}
	}

}
