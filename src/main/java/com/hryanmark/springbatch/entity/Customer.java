package com.hryanmark.springbatch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="customer")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	
	@Id
	@Column(name = "id")
	private Long id;
	
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
