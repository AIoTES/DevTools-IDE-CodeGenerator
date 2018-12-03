package es.upm.tfo.lst.CodeGenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Class who represent the given XML in java code.
 *
 *
 * @author Buhid Eduardo
 * @version 1
 */

public class TemplateDataModel {
		private String name,version,description;
		private Author author;
		private Set<Variable> arrayVars;
		private List <MacroModel> macroList;
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
		public Author getAuthor() {
			return author;
		}
		public void setAuthor(Author author) {
			this.author = author;
		}


		/**
		 * give a complete list of all macros given in XML
		 * @return {@link List } of {@link MacroModel}
		 */
		public List<MacroModel> getMacroList() {
			return macroList;
		}

		/**
		 * sets list of {@link MacroModel}
		 * @param macroList {@link List } of {@link MacroModel}
		 */
		public void setMacroList(List<MacroModel> macroList) {
			this.macroList = macroList;
		}

		/**
		 *
		 * @return @{@link List}<{@link MacroModel}> object for classs, Null if class macro not exist
		 */
		public List<MacroModel> getClassMacro() {
			return this.getMacro("class");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for ontology, Null if ontology macro not exist
		 */
		public List<MacroModel> getOntologyMacro() {
			return this.getMacro("ontology");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for instances ,Null if instance macro not exist
		 */
		public List<MacroModel> getInstanceMacro() {
			return this.getMacro("instance");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for enumerations, Null if enumerations macro not exist
		 */
		public List<MacroModel> getEnumerationMacro() {
			return this.getMacro("enumeration");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for project, Null if project macro not exist
		 */
		public List<MacroModel> getProjectMacro() {
			return this.getMacro("project");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for superclasses, Null if super class macro not exist
		 */
		public List<MacroModel> getSuperclassMacro() {
			return this.getMacro("superclass");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for subclasses, Null if sub property values macro not exist
		 */
		public List<MacroModel> getSPropertyValues() {
			return this.getMacro("propertyValues");
		}

		/**
		 *
		 * @return {@link List}<{@link MacroModel}> object for object propeties, Null if sub property values macro not exist
		 */
		public List<MacroModel> getObjectProperties(){
			return this.getMacro("objectProperties");
		}

		/**
		 * If in XML file exists two or more variables with same name (optional or required), the program
		 * automatically dont add it
		 * @return {@link Set} of {@link Variable} containing all variables given in XML file
		 */
		public Set<Variable> getArrayVars() {
			return this.arrayVars;
		}

		/**
		 *
		 * @return {@link Set} <{@link Variable}> of variables setted as required from XML file
		 */
		public Set<Variable> getRequiredVariables(){
			return this.arrayVars.stream().filter(t->t.isRequired()).collect(Collectors.toSet());
		}
		/**
		 *
		 * @return {@link Set} <{@link Variable}> of variables setted as optional from XML file
		 */
		public Set<Variable> getOptionalVariables(){
			return this.arrayVars.stream().filter(t->t.isRequired()).collect(Collectors.toSet());
		}

		/**
		 *
		 * @param vars {@link Set} <{@link Variable}> of all variables given in XML file
		 */
		public void setVars(Set<Variable> vars) {
			this.arrayVars = vars;
		}

		/**
		 * print all macros into console
		 */
		public void showMacros() {
			this.macroList.stream().forEach(r->System.out.println(r.getTemplateFor()));
		}


	 /**
	 * @param type
	 * @return Null if given macro not exist
	 */
		private List<MacroModel> getMacro(String type) {
			//List<MacroModel> aux;
			return this.macroList.stream().filter(t->t.getTemplateFor().equals(type)).collect(Collectors.toList());
			/*
			for(MacroModel m : this.macroList) {
				if(m.getTemplateFor().compareTo(type)==0) {
					aux.add(m);
				}

			}
			return aux;
			*/

		}


		@Override
		public String toString() {
			return 		"TemplateName="+
					this.name+
						"\n template version="+
					this.version+
						"\n template description="+
					this.description+
						"\n variables count="+
					this.macroList.size()+
						"\n author name="+
					this.author.getName()+
						"\n author phone="+
					this.author.getPhone()+
						"\n author email="+
					this.author.getEmail();
		}

}


