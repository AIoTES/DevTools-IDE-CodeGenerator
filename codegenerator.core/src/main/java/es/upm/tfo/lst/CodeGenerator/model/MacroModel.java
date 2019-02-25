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
package es.upm.tfo.lst.CodeGenerator.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class represents macro element in the XML file
 *
 * @author Buhid Eduardo
 * @version 1
 *
 */

public class MacroModel {
	private String template, output, templateFor;
	private Map<String, String> imports;

	/**
	 * 
	 * @param template    template source
	 * @param output      output filename
	 * @param templateFor type of iteration
	 * @param imports {@link Map} of imports 
	 */
	public MacroModel(String template, String output, String templateFor,Map<String, String> imports) {
		this.template = template;
		this.output = output;
		this.templateFor = templateFor;
		this.imports = imports;
	}
	
	/**
	 *
	 * @param template    template source
	 * @param output      output filename
	 * @param templateFor type of iteration
	 */
	public MacroModel(String template, String output, String templateFor) {
		this.template = template;
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
	 * This method adds to the model all the given imports given in xml.
	 *
	 * @param fullQualifiedClassName The complete package name including the class to be
	 *        added. The class will be accessible as class name in the velocity
	 *        macro file.
	 */
	public void setImport(String fullQualifiedClassName) {
		// TODO: detect special characters
		this.imports.put(fullQualifiedClassName.substring(fullQualifiedClassName.lastIndexOf(".")), fullQualifiedClassName);
	}

	/**
	 * This method adds in the model all the given imports in the xml. T
	 *
	 * @param fullQualifiedClassName : The complete package name including the class to be added
	 * @param alias : Alias to refer the class into velocity templates. If it is
	 *        setted null, empty or have invalid character, the program will use the
	 *        the last part of fullQualifiedClassName variable
	 */
	public void setImport(String fullQualifiedClassName, String alias) {
		// TODO: detect special characters
		if (alias.equals("") | alias == null) {
			this.setImport(fullQualifiedClassName);
		} else
			this.imports.put(alias, fullQualifiedClassName);
	}

	/**
	 * @return {@link Map}< {@link String}, {@link String}> of impors
	 */
	public Map<String, String> getImports() {
		return imports;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "output " + this.output + " template " + this.template + " template for " + this.templateFor;
	}

}
