package es.upm.tfo.lst.CodeGenerator.model;

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


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "output "+this.output+" template "+this.template+" template for "+this.templateFor ;
	}
	
}
