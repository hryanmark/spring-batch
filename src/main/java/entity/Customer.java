package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Customer {
	
	@Id
	@Column(name = "id")
	private int customerId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "contact")
	private String contactNo;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "dob")
	private String dob;
}
