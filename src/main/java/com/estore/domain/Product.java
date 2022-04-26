package com.estore.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class Product {
	
	private BigInteger id;
	
	private String name, description;
	private BigDecimal price;
	private Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * Creates a new {@link Product} with the given name.
	 * 
	 * @param name  must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 */
	public Product(String name, BigDecimal price) {
		this(name, price, null);
	}

	/**
	 * Creates a new {@link Product} from the given name and description.
	 * 
	 * @param name        must not be {@literal null} or empty.
	 * @param price       must not be {@literal null} or less than or equal to zero.
	 * @param description
	 */
	public Product(String name, BigDecimal price, String description) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.isTrue(BigDecimal.ZERO.compareTo(price) < 0, "Price must be greater than zero!");

		this.name = name;
		this.price = price;
		this.description = description;
	}

	/**
	 * Sets the attribute with the given name to the given value.
	 * 
	 * @param name  must not be {@literal null} or empty.
	 * @param value
	 */
	public void setAttribute(String name, String value) {

		Assert.hasText(name, "");

		if (value == null) {
			this.attributes.remove(value);
		} else {
			this.attributes.put(name, value);
		}
	}

	/**
	 * Returns the {@link Product}'s name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link Product}'s description.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns all the custom attributes of the {@link Product}.
	 * 
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * Returns the price of the {@link Product}.
	 * 
	 * @return
	 */
	public BigDecimal getPrice() {
		return price;
	}
	
	/**
	 * Returns the identifier of the document.
	 * 
	 * @return the id
	 */
	public BigInteger getId() {
		return id;
	}
	
	public Product setId(BigInteger id) {
		this.id = id;	
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
			return false;
		}

		Product that = (Product) obj;

		return this.id.equals(that.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}
