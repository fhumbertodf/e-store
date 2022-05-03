package com.estore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.estore.domain.Customer;
import com.estore.domain.EmailAddress;

/**
 * Repository interface to access {@link Customer}s.
 * 
 * @author Oliver Gierke
 */
public interface CustomerRepository extends MongoRepository<Customer, String> {

	/**
	 * Returns the {@link Customer} with the given {@link EmailAddress}.
	 * 
	 * @param string
	 * @return
	 */
	Customer findByEmailAddress(EmailAddress emailAddress);
}
