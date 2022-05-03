package com.estore.repository;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.stereotype.Repository;

import com.estore.codec.CustomerCodec;
import com.estore.domain.Customer;
import com.estore.domain.EmailAddress;
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
	
	public void createCollection() {
		openConnection();
		
		database.createCollection("customers");
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		IndexOptions indexOptions = new IndexOptions().unique(true);
	    customers.createIndex(Indexes.ascending("email.email"), indexOptions);
	    	    
	    closeConnection();
	}
	
	public void dropCollection() {
		openConnection();
		
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		customers.drop();	
		
		closeConnection();		
	}

	public Customer findByEmailAddress(EmailAddress email) {
		openConnection();
		
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		MongoCursor<Customer> results = customers.find(Filters.eq("email.email", email.toString())).iterator();
		Customer customer = results.hasNext() ? results.next() : null;
		
		closeConnection();		
		return customer;
	}
	
	public Customer save(Customer customer) {
		openConnection();
		
		MongoCollection<Customer> customers = this.database.getCollection("customers", Customer.class);
		customers.insertOne(customer);
		
		closeConnection();
		return customer;
	}
}
