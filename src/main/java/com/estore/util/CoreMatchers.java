package com.estore.util;

import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;

import com.estore.domain.Product;

public class CoreMatchers {

	/**
	 * Syntactic sugar to make Matchers more readable.
	 * 
	 * @param matcher must not be {@literal null}.
	 * @return
	 */
	public static <T> Matcher<T> with(Matcher<T> matcher) {
		return matcher;
	}

	/**
	 * Matches if the {@link Product} has the given name.
	 * 
	 * @param name must not be {@literal null}.
	 * @return
	 */
	public static Matcher<Product> named(String name) {
		return hasProperty("name", is(name));
	}
}
