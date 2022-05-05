package com.estore.domain;

import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class EmailAddress {
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

	private final String email;

	/**
	 * Creates a new {@link EmailAddress} from the given {@link String}
	 * representation.
	 * 
	 * @param emailAddress must not be {@literal null} or empty.
	 */
	public EmailAddress(String email) {
		Assert.isTrue(isValid(email), "Invalid email address!");
		this.email = email;
	}

	/**
	 * Returns whether the given {@link String} is a valid {@link EmailAddress}
	 * which means you can safely instantiate the class.
	 * 
	 * @param candidate
	 * @return
	 */
	public static boolean isValid(String candidate) {
		return candidate == null ? false : PATTERN.matcher(candidate).matches();
	}

	/**
	 * Returns the email.
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return email;
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

		if (!(obj instanceof EmailAddress)) {
			return false;
		}

		EmailAddress that = (EmailAddress) obj;
		return this.email.equals(that.email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return email.hashCode();
	}
}
