package es.upm.tfo.lst.CodeGenerator.model;
/**
 * Class to represent an author and his details in given XML file 
 * 
 * @author Buhid Eduardo
 * @version 1
 *
 */
public class Author {
	private String name,email,phone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	

}
