package com.dam.person.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.dam.person.types.Gender;
import com.dam.person.types.PersonType;

@Entity
@Component
@Table(name = "Person", uniqueConstraints= {@UniqueConstraint(columnNames = {"userId", "givenName", "lastName"})})
public class Person {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long personId;

	@Column(nullable=false)
	private Long userId;
	
	@Column(nullable=false)
	private String givenName;
	@Column(nullable=false)
	private String lastName;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private PersonType personType;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;

	private Date birthdate;
	
	public Person () {
		
	}
	
	public Person (Long userId, String givenName, String lastName) {
		setUserId(userId);
		setGivenName(givenName);
		setLastName(lastName);
	}
	
	public Person(Long userId, String givenName, String lastName, String personType, String gender, Date birthdate) {
		setUserId(userId);
		setGivenName(givenName);
		setLastName(lastName);
		setPersonType(PersonType.valueOf(personType));
		setGender(Gender.valueOf(gender));
		setBirthdate(birthdate);
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * Updates Entity values.
	 * userId is not changeable
	 * @param updateUser
	 */
	public void updateEntity (Person updatePerson) {
		if (null == updatePerson) {
			return;
		}
		
		setGivenName(updatePerson.getGivenName());
		setLastName(updatePerson.getLastName());
		setGender(updatePerson.getGender());
		setPersonType(updatePerson.getPersonType());
		setBirthdate(updatePerson.getBirthdate());
		
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}
}
