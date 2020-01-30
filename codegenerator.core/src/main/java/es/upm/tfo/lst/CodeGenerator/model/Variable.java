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

import javax.sound.sampled.Line;

import com.google.common.reflect.Parameter;

/**
 * Class to represent a variable item in given XML file.
 *
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class Variable {
	private String name, defaultValue,description;
	private boolean required;
	
	/**
	 * 
	 * @param name
	 * @param required
	 * @param defaultValue
	 */
	public Variable(String name, boolean required, String defaultValue) {
		this.name = name;
		this.required = required;
		this.defaultValue = defaultValue;

	}

	/**
	 *
	 * @param {@link String } name name of variable
	 * @param {@link String } required: True if variable is required, false if variable is optional
	 * @param {@link String } defaultValue: default content of variable
	 * @param {@link String}  description: a little description from the purpose of this variable
	 */
	public Variable(String name, String description, boolean required, String defaultValue) {
		this(name,required,defaultValue);
		this.description = description;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 *
	 * @return {@link String} name of variable
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return {@link String} if variable is required or not
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 *
	 * @return {@link String} of default value of
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String getValue() {
		return this.getDefaultValue();
	}
	public void setDefaultValue(String value) {
		this.defaultValue = value;
	}


	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(this.getName());
	}

	/**
	 * to print all variable content as string
	 * @return
	 */
	public String print() {
		return "name="+this.name+" default value="+this.defaultValue+" required="+this.required+" ";
	}
}
