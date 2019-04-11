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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to represent the given XML in java code.
 *
 *
 * @author Buhid Eduardo
 * @version 1
 */

public class TemplateDataModel {

	private String name, version, description, baseTemplatePath;

	private Map<String, Variable> arrayVars;
	private List<MacroModel> macroList;

	public TemplateDataModel() {
		this.arrayVars = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desription) {
		this.description = desription;
	}

	/**
	 * List of all macros given in XML.
	 *
	 * @return {@link List } of {@link MacroModel}
	 */
	public List<MacroModel> getMacroList() {
		return macroList;
	}

	/**
	 * Sets list of {@link MacroModel}.
	 *
	 * @param macroList {@link List } of {@link MacroModel}
	 */
	public void setMacroList(List<MacroModel> macroList) {
		this.macroList = macroList;
	}

	/**
	 *
	 * @return @{@link List}<{@link MacroModel}> of class iteration macros, Null if
	 *         not defined
	 */
	public List<MacroModel> getClassMacros() {
		return this.getMacroList("Class");
	}

	/**
	 *
	 * @return {@link List}<{@link MacroModel}> object for ontology iteration
	 *         macros, Null if not defined
	 */
	public List<MacroModel> getOntologyMacros() {
		return this.getMacroList("Ontology");
	}

	/**
	 *
	 * @return {@link List}<{@link MacroModel}> object for instances iteration
	 *         macros, Null if not defined
	 */
	public List<MacroModel> getInstanceMacros() {
		return this.getMacroList("NamendIndividual");
	}

	/**
	 *
	 * @return {@link List}<{@link MacroModel}> object for enumerations iteration
	 *         macros, Null if not defined
	 */
	public List<MacroModel> getEnumerationMacros() {
		return this.getMacroList("Enumeration");
	}

	/**
	 *
	 * @return {@link List}<{@link MacroModel}> object for project iteration macros,
	 *         Null if not defined
	 */
	public List<MacroModel> getProjectMacros() {
		return this.getMacroList("Project");
	}

	/**
	 *
	 * @return {@link List}<{@link MacroModel}> object for object propeties
	 *         iteration macros, Null if not defined
	 */
	public List<MacroModel> getObjectProperties() {
		return this.getMacroList("objectProperties");
	}

	public List<MacroModel> getDataProperties(){
		return this.getMacroList("dataProperties");
	}
	
	/**
	 * If in XML file exists two or more variables with same name (optional or
	 * required), they will be ignored.
	 *
	 * @return {@link Set} of {@link Variable} containing all variables given in XML
	 *         file
	 */
	public Map<String, Variable> getArrayVars() {
		return this.arrayVars;
	}

	/**
	 *
	 * @return {@link Set} <{@link Variable}> of variables configured as required from
	 *         XML file
	 */
	public Map<String, Variable> getRequiredVariables() {
		Set<String> aux = this.arrayVars.keySet();
		Map<String, Variable> aux_map = new HashMap<String, Variable>();
		for (String t : aux) {
			if (!this.arrayVars.get(t).isRequired()) {
				aux_map.put(t, this.arrayVars.get(t));
			}
		}
		return aux_map;
	}

	/**
	 *
	 * @return {@link Set} <{@link Variable}> of variables configured as optional from
	 *         XML file
	 */
	public Map<String, Variable> getOptionalVariables() {
		Set<String> aux = this.arrayVars.keySet();
		Map<String, Variable> aux_map = new HashMap<String, Variable>();
		for (String t : aux) {
			if (!this.arrayVars.get(t).isRequired()) {
				aux_map.put(t, this.arrayVars.get(t));
			}
		}
		return aux_map;
	}

	/**
	 *
	 * @param vars {@link Set} <{@link Variable}> of all variables to be set.
	 */
	public void setVars(Map<String, Variable> vars) {
		this.arrayVars = vars;
	}

	/**
	 * Print all macros into console.
	 */
	public void showMacros() {
		this.macroList.stream().forEach(r -> System.out.println(r.getTemplateFor()));
	}

	/**
	 * Modify existing variable value.
	 */
	public void modifyVariable(String name, String value) {
		this.arrayVars.get(name).setValue(value);
	}

	/**
	 * Setter to add local base loader path to project.
	 *
	 * @param baseTemplatePath
	 */
	public void setBaseTemplatePath(String baseTemplatePath) {
		this.baseTemplatePath = baseTemplatePath;
	}

	/**
	 * Get base path, for example to load all relative template files.
	 *
	 * @return {@link String}
	 */
	public String getBaseTemplatePath() {
		return baseTemplatePath;
	}

	/**
	 * @return Boolean value indicating id the template is or not web
	 */
	public boolean isWebTemplate() {
		boolean flag = true;
		try {
			URL aux = new URL(this.baseTemplatePath);
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * Print variables on console.
	 */
	public void showVariables() {
		Set<String> aux = this.arrayVars.keySet();
		for (String t : aux) {
			System.out.println(this.arrayVars.get(t).getValue());
		}
	}

	/**
	 * @param type iteration type of macros.
	 * @return Null if given macro not exist
	 */
	private List<MacroModel> getMacroList(String type) {

		return this.macroList.stream().filter(t -> t.getTemplateFor().equalsIgnoreCase(type)).collect(Collectors.toList());

	}
	
	

	@Override
	public String toString() {
		return "TemplateName=" + this.name + "\n template version=" + this.version + "\n template description="
				+ this.description + "\n variables count=" + this.macroList.size();

	}

}
