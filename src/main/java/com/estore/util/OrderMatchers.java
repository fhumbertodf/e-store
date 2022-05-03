package com.estore.util;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;

import org.hamcrest.Matcher;

import com.estore.domain.LineItem;
import com.estore.domain.Order;
import com.estore.domain.Product;

public class OrderMatchers {

	/**
	 * Matches if the source {@link Iterable} has an {@link Order} that matches the given {@link Matcher}.
	 * 
	 * @param matcher must not be {@literal null}.
	 * @return
	 */
	public static <T> Matcher<Iterable<? super T>> containsOrder(Matcher<? super T> matcher) {
		return hasItem(matcher);
	}

	/**
	 * Matches if the {@link Order} has a {@link LineItem} matching the given {@link Matcher}.
	 * 
	 * @param matcher must not be {@literal null}.
	 * @return
	 */
	public static Matcher<Order> LineItem(Matcher<LineItem> matcher) {
		return hasProperty("lineItems", hasItem(matcher));
	}

	/**
	 * Matches if the {@link LineItem} refers to a {@link Product} that matches the given {@link Matcher}.
	 * 
	 * @param matcher must not be {@literal null}.
	 * @return
	 */
	public static Matcher<LineItem> Product(Matcher<Product> matcher) {
		return hasProperty("product", matcher);
	}
}
