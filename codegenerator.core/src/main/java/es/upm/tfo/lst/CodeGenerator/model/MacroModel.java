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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

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
	private Node node = null;
	private Set <Map<String, String> >imports;

	/**
	 * 
	 * @param template    template source
	 * @param output      output filename
	 * @param templateFor type of iteration
	 * @param imports {@link Set} of {@link Map} 
	 */
	public MacroModel(String template, String output, String templateFor,Set<Map<String, String>> imports) {
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
		this.imports = new HashSet<>();
	}
	/**
	 * 
	 * @param template    template source
	 * @param output      output filename
	 * @param templateFor type of iteration
	 * @param imports {@link Map} of imports 
	 * @param node
	 */
	public MacroModel(String template, String output, String templateFor,Set < Map<String, String> > imports,Node node) {
		this(template,output,template, imports);
		this.node=node;
	}


	


	public Set<Map<String, String>> getImports() {
		return imports;
	}



	public void setImports(Set<Map<String, String>> imports) {
		this.imports = imports;
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
		return "output " + this.output + " template " + this.template + " template for " + this.templateFor;
	}

}
