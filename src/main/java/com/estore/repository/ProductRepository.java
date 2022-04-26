package com.estore.repository;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.stereotype.Repository;

import com.estore.codec.ProductCodec;
import com.estore.domain.Product;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

@Repository
public class ProductRepository {
	
	private MongoClient client;
	private MongoDatabase database;

	private void openConnection() {
		
		Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);

		ProductCodec ProductCodec = new ProductCodec(codec);

		CodecRegistry registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(ProductCodec));

		MongoClientOptions opcoes = MongoClientOptions.builder().codecRegistry(registro).build();

		this.client = new MongoClient("localhost:27017", opcoes);
		this.database = client.getDatabase("e-store");
	}

	private void closeConnection() {
		this.client.close();
	}
	
	public void createCollection() {
		openConnection();
		database.createCollection("products");		
		closeConnection();
	}
	
	public void dropCollection() {
		openConnection();
		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		products.drop();		
		closeConnection();		
	}

	public Product findByAttributes(String fieldName, String value) {
		openConnection();
		
		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		MongoCursor<Product> results = products.find(Filters.eq(fieldName, value.toString())).iterator();
		Product product = results.hasNext() ? results.next() : null;
		
		closeConnection();
		
		return product;
	}

	public Product save(Product product) {

		openConnection();
		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		MongoCursor<Product> results = products.find(Filters.eq("name", product.getName().toString())).iterator();
		Product result = results.hasNext() ? results.next() : null;
		if (result == null) {
			products.insertOne(product);
		} else {
			throw new DuplicateKeyException(null, null, null);
		}

		closeConnection();
		return product;
	}
	
}
