package com.dam.address.model.entity;

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

import com.dam.address.types.AddressType;
import com.dam.address.types.CountryCode;

@Entity
@Component
@Table(name = "Address", uniqueConstraints= {@UniqueConstraint(columnNames = {"personId", "addressType"})})
public class Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long addressId;
	
	@Column(nullable=false)
	private Long personId;

	@Column(nullable=false)
	private Long userId;
	
	@Column(nullable=true)
	private String street;
	
	@Column(nullable=true)
	private String houseNo;

	@Column(nullable=false)
	private String postCode;
	
	@Column(nullable=false)
	private String city;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private CountryCode countryCode;


	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private AddressType addressType;
	
	public Address () {
		
	}
	

	/**
	 * Updates Entity values.
	 * userId is not changeable
	 * @param updateUser
	 */
	public void updateEntity (Address updateAddress) {
		if (null == updateAddress) {
			return;
		}
		
		setAddressType(updateAddress.getAddressType());
		setCity(updateAddress.city);
		setCountryCode(updateAddress.getCountryCode());
		setHouseNo(updateAddress.getHouseNo());
		setPostCode(updateAddress.getPostCode());
		setStreet(updateAddress.getStreet());
	}


	public Long getAddressId() {
		return addressId;
	}


	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}


	public Long getPersonId() {
		return personId;
	}


	public void setPersonId(Long personId) {
		this.personId = personId;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getHouseNo() {
		return houseNo;
	}


	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}


	public String getPostCode() {
		return postCode;
	}


	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public CountryCode getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(CountryCode countryCode) {
		this.countryCode = countryCode;
	}


	public AddressType getAddressType() {
		return addressType;
	}


	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

}
