package es.upm.tfo.lst.CodeGenerator.model;
/**
 * Class who represent a <variable> item in given XML file
 * 
 * @author Buhid Eduardo 
 * @version 1
 * 
 */
public class Variable {
	private String name,defaultValue,required;
	
	/**
	 * 
	 * @param {@link String }name name of variable
	 * @param {@link String } required: True if variable is required, false if variable is optional
	 * @param {@link String } defaultValue: Conent of variable
	 */
	public Variable(String name, String required, String defaultValue) {	
		this.name = name;
		this.required = required;
		this.defaultValue = defaultValue;
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
	public String getRequired() {
		return this.required;
	}
	/**
	 * 
	 * @return {@link String} of default value of 
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String toString() {
		return "name "+this.name+" required "+this.required+" defaultValue "+this.defaultValue;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return name.equals(((Variable)obj).getName());
	}


	
		
}
