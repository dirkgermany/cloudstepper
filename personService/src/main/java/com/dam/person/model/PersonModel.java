package com.dam.person.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.person.model.entity.Person;

@Transactional
public interface PersonModel extends Repository<Person, Long>, CrudRepository<Person, Long> {

	List<Person> findByUserId(Long userId);
	long count();

	@Query("SELECT person FROM Person person where person.givenName = :givenName "
			+ "AND person.lastName = :lastName")
	List<Person> getPersonByNames(@Param("givenName") String givenName, @Param("lastName") String lastName);
	
	@Modifying
	@Transactional
	@Query("Update Person person set "
	        + "person.userId = :#{#person.userId}, "
	        + "person.givenName = :#{#person.givenName}, "
	        + "person.personType = :#{#person.personType}, "
	        + "person.gender = :#{#person.gender}, "
			+ "person.lastName = :#{#person.lastName} "
			+ "Where person.personId = :#{#person.personId} ")
	Integer updatePerson(@Param("person") Person person);

}
