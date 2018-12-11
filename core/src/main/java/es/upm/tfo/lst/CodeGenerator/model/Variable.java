package es.upm.tfo.lst.CodeGenerator.model;
/**
 * Class who represent a <variable> item in given XML file
 *
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class Variable {
	private String name,defaultValue;
	private boolean required;
	private String value,description;

	/**
	 *
	 * @param {@link String }name name of variable
	 * @param {@link String } required: True if variable is required, false if variable is optional
	 * @param {@link String } defaultValue: Conent of variable
	 */
	public Variable(String name,String description, boolean required, String defaultValue) {
		this.name = name;
		this.required = required;
		this.defaultValue = defaultValue;
		this.value = null;
		this.description = description;
	}
	public Variable(String name,boolean required, String defaultValue) {
		this.name = name;
		this.required = required;
		this.defaultValue = defaultValue;
		this.value = null;
		
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

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		if (value == null && defaultValue != null) {
			return defaultValue;
		}
		return value;
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
		return name.equals(this.getName());
	}




}
