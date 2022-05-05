package com.estore.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.estore.codec.OrderCodec;
import com.estore.domain.Customer;
import com.estore.domain.Order;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

@Repository
public class OrderRepository {
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	private MongoClient client;
	private MongoDatabase database;

	private void openConnection() {
		Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		OrderCodec OrderCodec = new OrderCodec(codec, customerRepository, productRepository);

		CodecRegistry registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(OrderCodec));

		MongoClientOptions opcoes = MongoClientOptions.builder().codecRegistry(registro).build();

		this.client = new MongoClient("localhost:27017", opcoes);
		this.database = client.getDatabase("e-store");
	}

	private void closeConnection() {
		this.client.close();
	}

	public void createCollection() {
		openConnection();
		database.createCollection("orders");
		closeConnection();
	}

	public List<Order> findByCustomer(Customer customer) {
		openConnection();

		MongoCollection<Order> orders = this.database.getCollection("orders", Order.class);
		// MongoCursor<Order> results = orders.find(Filters.eq("customer.$id",
		// customer.getId().toString(16))).iterator();
		MongoCursor<Order> results = orders.find(Filters.eq("customer", new DBRef("customers", new ObjectId(customer.getId().toString(16))))).iterator();
		List<Order> result = new ArrayList<Order>();
		while (results.hasNext()) {
			result.add(results.next());
		}

		closeConnection();
		return result;
	}

	public Order insert(Order order) {
		openConnection();

		MongoCollection<Order> orders = this.database.getCollection("orders", Order.class);
		orders.insertOne(order);

		closeConnection();
		return order;
	}

	public Order update(Order order) {
		openConnection();

		MongoCollection<Order> orders = this.database.getCollection("orders", Order.class);
		orders.updateOne(Filters.eq("_id", order.getId()), new Document("$set", order));

		closeConnection();
		return order;
	}

	public void deleteAll() {
		openConnection();
		MongoCollection<Order> orders = this.database.getCollection("orders", Order.class);
		orders.deleteMany(new Document());
		closeConnection();
	}
}
