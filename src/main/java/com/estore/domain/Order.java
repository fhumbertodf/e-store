package com.estore.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

/**
 * @author Oliver Gierke
 */
@Document(collection = "orders")
public class Order {
	
	@Id
	private String id;

	@DBRef
	private Customer customer;
	private Address billingAddress;
	private Address shippingAddress;
	private Set<LineItem> lineItems = new HashSet<LineItem>();

	/**
	 * Creates a new {@link Order} for the given {@link Customer}.
	 * 
	 * @param customer        must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 */
	public Order(Customer customer, Address shippingAddress) {
		this(customer, shippingAddress, null);
	}

	/**
	 * Creates a new {@link Order} for the given {@link Customer}, shipping and
	 * billing {@link Address}.
	 * 
	 * @param customer        must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 * @param billingAddress  can be {@literal null}.
	 */
	@PersistenceConstructor
	public Order(Customer customer, Address shippingAddress, Address billingAddress) {

		Assert.notNull(customer, "");
		Assert.notNull(shippingAddress, "");

		this.customer = customer;
		this.shippingAddress = shippingAddress;
		this.billingAddress = billingAddress;
	}

	/**
	 * Adds the given {@link LineItem} to the {@link Order}.
	 * 
	 * @param lineItem
	 */
	public void add(LineItem lineItem) {
		this.lineItems.add(lineItem);
	}

	/**
	 * Returns the {@link Customer} who placed the {@link Order}.
	 * 
	 * @return
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Returns the billing {@link Address} for this order.
	 * 
	 * @return
	 */
	public Address getBillingAddress() {
		return billingAddress != null ? billingAddress : shippingAddress;
	}

	/**
	 * Returns the shipping {@link Address} for this order;
	 * 
	 * @return
	 */
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Returns all {@link LineItem}s currently belonging to the {@link Order}.
	 * 
	 * @return
	 */
	public Set<LineItem> getLineItems() {
		return Collections.unmodifiableSet(lineItems);
	}

	/**
	 * Returns the total of the {@link Order}.
	 * 
	 * @return
	 */
	public BigDecimal getTotal() {

		BigDecimal total = BigDecimal.ZERO;

		for (LineItem item : lineItems) {
			total = total.add(item.getTotal());
		}

		return total;
	}
	
	/**
	 * Returns the identifier of the document.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the {@link Customer}.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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

		Order that = (Order) obj;

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
