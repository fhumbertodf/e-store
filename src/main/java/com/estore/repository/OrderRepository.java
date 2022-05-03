package com.estore.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.estore.domain.Customer;
import com.estore.domain.Order;

/**
 * Repository to access {@link Order}s.
 * 
 * @author Oliver Gierke
 */
public interface OrderRepository extends MongoRepository<Order, Long> {

	/**
	 * Returns all {@link Order}s of the given {@link Customer}.
	 * 
	 * @param customer
	 * @return
	 */
	List<Order> findByCustomer(Customer customer);
}
