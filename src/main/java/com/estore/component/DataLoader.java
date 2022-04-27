package com.estore.component;

import static com.estore.util.CoreMatchers.named;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.estore.domain.Address;
import com.estore.domain.Customer;
import com.estore.domain.EmailAddress;
import com.estore.domain.Product;
import com.estore.repository.CustomerRepository;
import com.estore.repository.ProductRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public void run(String... strings) throws Exception {

		customerRepository.dropCollection();
		customerRepository.createCollection();
		productRepository.dropCollection();
		productRepository.createCollection();

		Customer dave = new Customer("Dave", "Matthews");
		dave.setEmailAddress(new EmailAddress("dave@dmband.com"));
		dave.add(new Address("28 Broadway", "New York", "United States"));
		dave.add(new Address("29 Broadway", "New York", "United States"));

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
		anotherDave.add(new Address("Broadway", "New York", "United States"));

		try {
			customerRepository.save(anotherDave);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		Product iPad = new Product("iPad", BigDecimal.valueOf(499.0), "Apple tablet device");
		iPad.setAttribute("connector", "plug");
		productRepository.save(iPad);

		Product macBook = new Product("MacBook Pro", BigDecimal.valueOf(1299.0), "Apple notebook");
		productRepository.save(macBook);

		Product dock = new Product("Dock", BigDecimal.valueOf(49.0), "Dock for iPhone/iPad");
		dock.setAttribute("connector", "plug");
		productRepository.save(dock);

		Product product = new Product("Camera bag", BigDecimal.valueOf(49.99));
		product = productRepository.save(product);

		Pageable pageable = PageRequest.of(0, 1, Sort.by(Direction.DESC, "name"));
		Page<Product> page = productRepository.findByDescriptionContaining("Apple", pageable);

		assertThat(page.getContent(), hasSize(1));
		assertThat(page, Matchers.<Product>hasItems(named("iPad")));
		assertThat(page.isFirst(), is(true));
		assertThat(page.isLast(), is(false));
		assertThat(page.hasNext(), is(true));
		
		Pageable pageable2 = PageRequest.of(0, 2, Sort.by(Direction.DESC, "name"));
		Page<Product> page2 = productRepository.findByDescriptionContaining("Apple", pageable2);
		
		assertThat(page2.getContent(), hasSize(2));
		assertEquals(page2.getContent().get(0).getName(), "iPad");		

		List<Product> products = productRepository.findByAttributes("attributes.connector", "plug");
		assertThat(products, Matchers.<Product>hasItems(named("Dock")));

	}
}
