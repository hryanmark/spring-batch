package com.hryanmark.springbatch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="customer")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
	
	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "first_name")
	private String first_name;
	
	@Column(name = "last_name")
	private String last_name;
	
	@Column(name = "email")
	private String email_;
	
	@Column(name = "gender")
	private String gender_;
	
	@Column(name = "contact")
	private String contact_no;
	
	@Column(name = "country")
	private String country_;
	
	@Column(name = "dob")
	private String dob_;
}
