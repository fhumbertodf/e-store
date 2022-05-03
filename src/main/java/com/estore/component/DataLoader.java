package com.estore.component;

import static com.estore.util.CoreMatchers.named;
import static com.estore.util.CoreMatchers.with;
import static com.estore.util.OrderMatchers.LineItem;
import static com.estore.util.OrderMatchers.Product;
import static com.estore.util.OrderMatchers.containsOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matcher;
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
import com.estore.domain.LineItem;
import com.estore.domain.Order;
import com.estore.domain.Product;
import com.estore.repository.CustomerRepository;
import com.estore.repository.OrderRepository;
import com.estore.repository.ProductRepository;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Override
	public void run(String... strings) throws Exception {
		
		customerRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();

		Address address1 = new Address("28 Broadway", "New York", "United States");
		Address address2 = new Address("28 Broadway", "New York", "United States");

		Customer dave = new Customer("Dave", "Matthews");
		dave.setEmailAddress(new EmailAddress("dave@dmband.com"));
		dave.add(address1);
		dave.add(address2);

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
		
		List<Product> products = productRepository.findByAttributes("attributes.connector", "plug");
		assertThat(products, Matchers.<Product>hasItems(named("Dock")));
		
		Order order = new Order(dave, address1);
		order.add(new LineItem(iPad));
		
		orderRepository.save(order);
		
		assertThat(order.getId(), is(notNullValue()));
		
		List<Order> orders = orderRepository.findByCustomer(dave);
		Matcher<Iterable<? super Order>> hasOrderForiPad = containsOrder(with(LineItem(with(Product(named("iPad"))))));

		assertThat(orders, hasSize(1));
		assertThat(orders, hasOrderForiPad);

	}
}
