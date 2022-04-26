package com.estore.repository;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.stereotype.Repository;

import com.estore.codec.CustomerCodec;
import com.estore.domain.Customer;
import com.estore.domain.EmailAddress;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

@Repository
public class CustomerRepository {

	private MongoClient client;
	private MongoDatabase database;

	private void openConnection() {
		
		Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);

		CustomerCodec CustomerCodec = new CustomerCodec(codec);

		CodecRegistry registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(CustomerCodec));

		MongoClientOptions opcoes = MongoClientOptions.builder().codecRegistry(registro).build();

		this.client = new MongoClient("localhost:27017", opcoes);
		this.database = client.getDatabase("e-store");
	}

	private void closeConnection() {
		this.client.close();
	}

	public Customer findByEmailAddress(EmailAddress email) {
		openConnection();
		
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		MongoCursor<Customer> results = customers.find(Filters.eq("email.email", email.toString())).iterator();
		Customer customer = results.hasNext() ? results.next() : null;
		
		closeConnection();
		
		return customer;
	}
	
	public void createCollection() {
		openConnection();
		database.createCollection("customers");
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		IndexOptions indexOptions = new IndexOptions().unique(true);
	    String resultCreateIndex = customers.createIndex(Indexes.ascending("email.email"), indexOptions);
	    System.out.println(resultCreateIndex);
	    closeConnection();
	}

	public Customer save(Customer customer) {

		openConnection();
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		MongoCursor<Customer> results = customers.find(Filters.eq("email", customer.getEmailAddress().toString())).iterator();
		Customer result = results.hasNext() ? results.next() : null;
		if (result == null) {
			customers.insertOne(customer);
		} else {
			throw new DuplicateKeyException(null, null, null);
		}

		closeConnection();
		return customer;
	}
	
	public void dropCollection() {
		openConnection();
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		customers.drop();		
		closeConnection();		
	}
}
