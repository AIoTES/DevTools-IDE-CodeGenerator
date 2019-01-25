package es.upm.tfo.lst.CodeGenerator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * This class represent <macro></macro> content into XML file
 * 
 * @author Buhid Eduardo
 * @version 1
 *
 *
 *
 */

public class MacroModel {
	private String template,output,templateFor;
	private  Map<String, String>  imports;
	/**
	 * 
	 * @param template template source
	 * @param output output name directory
	 * @param templateFor type of output
	 */
	public MacroModel(String template, String output,String templateFor) {
		this.template=template;
		this.output = output;
		this.templateFor = templateFor;
		this.imports = new HashMap<>();
	}
	
	/**
	 * 
	 * @return {@link String} name of template
	 */
	public String getTemplateName() {
		return template;
	}

	/**
	 * 
	 * @return {@link String} of name to the output
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * 
	 * @return {@link String} name to the template 
	 */
	public String getTemplateFor() {
		return templateFor;
	}
	
	
	/**
	 * 
	 * @param packageName: The complete package name including the class to be added
	 * @param alias: Alias to refer the class into velocity templates. If it is setted null, empty or have invalid character, the program will use the class name provided into packageName variable
	 */
	public void setImport(String packageName, String alias) {
		//TODO: detect special characters
		if(alias.equals("") | alias==null) {
			this.imports.put(alias, packageName.substring(packageName.lastIndexOf(".")));
		}else
			this.imports.put(alias, packageName);
	}
	
	private boolean stringControl(String toctrl) {
		
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "output "+this.output+" template "+this.template+" template for "+this.templateFor ;
	}
	
}
