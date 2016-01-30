package sparkexample;

import java.util.Date;

public class Person {

	private Integer id;
	private String lastname;
	private String firstname;
	private Date birthdate;

	public Person() {
		super();
	}
	
	public Person(Integer id, String lastname, String firstname, Date birthdate) {
		super();
		this.id = id;
		this.lastname = lastname;
		this.firstname = firstname;
		this.birthdate = birthdate;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLastName() {
		return lastname;
	}

	public void setLastName(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String firstname) {
		this.firstname = firstname;
	}
 
	public Date getBirthDate() {
		return birthdate;
	}

	public void setBirthDate(Date birthdate) {
		this.birthdate = birthdate;
	}
}
