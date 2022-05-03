package com.estore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.estore.domain.Product;

/**
 * Repository interface to access {@link Product}s.
 * 
 * @author Oliver Gierke
 */
public interface ProductRepository extends MongoRepository<Product, Long> {

	/**
	 * Returns a {@link Page} of {@link Product}s having a description which contains the given snippet.
	 * 
	 * @param description
	 * @param pageable
	 * @return
	 */
	Page<Product> findByDescriptionContaining(String description, Pageable pageable);

	/**
	 * Returns all {@link Product}s having the given attribute.
	 * 
	 * @param attribute
	 * @return
	 */
	@Query("{ ?0 : ?1 }")
	List<Product> findByAttributes(String key, String value);
}
