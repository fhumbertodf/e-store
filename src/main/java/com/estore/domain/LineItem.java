package com.estore.domain;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.Assert;

/**
 * @author Oliver Gierke
 */
public class LineItem { 
	
	@Id
	private String id;

	@DBRef
	private Product product;
	private BigDecimal price;
	private int amount;

	/**
	 * Creates a new {@link LineItem} for the given {@link Product}.
	 * 
	 * @param product must not be {@literal null}.
	 */
	public LineItem(Product product) {
		this(product, 1);
	}

	/**
	 * Creates a new {@link LineItem} for the given {@link Product} and amount.
	 * 
	 * @param product must not be {@literal null}.
	 * @param amount
	 */
	public LineItem(Product product, int amount) {

		Assert.notNull(product, "The given Product must not be null!");
		Assert.isTrue(amount > 0, "The amount of Products to be bought must be greater than 0!");

		this.product = product;
		this.amount = amount;
		this.price = product.getPrice();
	}

	public LineItem() {

	}

	/**
	 * Returns the {@link Product} the {@link LineItem} refers to.
	 * 
	 * @return
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * Returns the amount of {@link Product}s to be ordered.
	 * 
	 * @return
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Returns the price a single unit of the {@link LineItem}'s product.
	 * 
	 * @return the price
	 */
	public BigDecimal getUnitPrice() {
		return price;
	}

	/**
	 * Returns the total for the {@link LineItem}.
	 * 
	 * @return
	 */
	public BigDecimal getTotal() {
		return price.multiply(BigDecimal.valueOf(amount));
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

		LineItem that = (LineItem) obj;

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
