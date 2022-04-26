package com.estore.component;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.estore.domain.Address;
import com.estore.domain.Customer;
import com.estore.domain.EmailAddress;
import com.estore.repository.CustomerRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public void run(String... strings) throws Exception {
		
		customerRepository.dropCollection();
		customerRepository.createCollection();
		
		Customer dave = new Customer("Dave", "Matthews");
		dave.setEmailAddress(new EmailAddress("dave@dmband.com"));
		dave.add(new Address("Broadway","New York","United States"));

		customerRepository.save(dave);
			
		Customer alicia = new Customer("Alicia", "Keys");
		alicia.setEmailAddress(new EmailAddress("alicia@keys.com"));
		alicia.add(new Address("27 Broadway", "New York", "United States"));

		Customer result = customerRepository.save(alicia);
		assertThat(result.getId(), is(notNullValue()));
		
		result = customerRepository.findByEmailAddress(result.getEmailAddress());
		assertThat(result.getId(), is(notNullValue()));
		
		Customer anotherDave = new Customer("Dave", "Matthews");
		anotherDave.setEmailAddress(dave.getEmailAddress());
		anotherDave.add(new Address("Broadway","New York","United States"));

		try {
			customerRepository.save(anotherDave);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
}
