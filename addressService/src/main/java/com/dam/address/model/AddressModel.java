package com.dam.address.model;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.address.model.entity.Address;
import com.dam.address.types.AddressType;

@Transactional
public interface AddressModel extends Repository<Address, Long>, CrudRepository<Address, Long> {

	Optional<Address> findByPersonId(Long personId);
	Optional<Address> findByUserId(Long userId);

	@Query("SELECT address FROM Address address where address.personId = :personId")
	List<Address> getAddressesByPersonId(@Param("personId") Long personId);
	
	@Query("SELECT address FROM Address address where address.userId = :userId")
	List<Address> getAddressesByUserId(@Param("userId") Long userId);
	
	/*
	 * Delivers only one Address because AddressType + PersonId are a constraint
	 */
	@Query("SELECT address FROM Address address where address.personId = :personId "
			+ "AND address.addressType = :addressType" )
	Address getAddressByPerson_Type(@Param("personId") Long personId, @Param("addressType") AddressType addressType);
	
	/*
	 * Delivers only one Address because AddressType + PersonId are a constraint
	 */
	@Query("SELECT address FROM Address address where address.personId = :personId "
			+ "AND address.userId = :userId "
			+ "AND address.addressType = :addressType" )
	Address getAddressByUser_Person_Type(@Param("userId") Long userId, @Param("personId") Long personId, @Param("addressType") AddressType addressType);
	

	@Modifying
	@Transactional
	@Query("Update Address address set "
	        + "address.userId = :#{#address.userId}, "
	        + "address.personId = :#{#address.personId}, "
	        + "address.addressType = :#{#address.addressType}, "
	        + "address.city = :#{#address.city}, "
	        + "address.countryCode = :#{#address.countryCode}, "
			+ "address.houseNo = :#{#address.housNo}, "
			+ "address.postCode = :#{#address.postCode}, "
			+ "address.street = :#{#address.street} "
			+ "Where address.addressId = :#{#address.addressId}")
	Integer updateAddress(@Param("address") Address address);
	
}
