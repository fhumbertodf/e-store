package com.estore.repository;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.estore.codec.ProductCodec;
import com.estore.domain.Product;
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

	public List<Product> findByAttributes(String fieldName, String value) {
		openConnection();

		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		MongoCursor<Product> results = products.find(Filters.eq(fieldName, value.toString())).iterator();
		List<Product> result = new ArrayList<Product>();
		while (results.hasNext()) {
			result.add(results.next());
		}

		closeConnection();
		return result;
	}

	public Page<Product> findByDescriptionContaining(String fieldName, Pageable pageable) {

		Pattern pattern = Pattern.compile(fieldName, Pattern.CASE_INSENSITIVE);

		Sort sort = pageable.getSort();
		Direction direction = null;
		List<String> names = new ArrayList<String>();
		for (Order order : sort.toList()) {
			direction = order.getDirection();
			names.add(order.getProperty());
		}

		openConnection();

		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		MongoCursor<Product> results;
		if (Direction.ASC.equals(direction)) {
			results = products.find(Filters.eq("description", pattern)).sort(ascending(names))
					.limit(pageable.getPageSize()).skip(pageable.getPageNumber()).iterator();
		} else {
			results = products.find(Filters.eq("description", pattern)).sort(descending(names))
					.limit(pageable.getPageSize()).skip(pageable.getPageNumber()).iterator();
		}
		Long count = products.countDocuments(Filters.eq("description", pattern));
		List<Product> result = new ArrayList<Product>();
		while (results.hasNext()) {
			result.add(results.next());
		}

		closeConnection();
		return new PageImpl<Product>(result, pageable, count);
	}

	public Product insert(Product product) {
		openConnection();
		
		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		products.insertOne(product);
		
		closeConnection();
		return product;
	}
	
	public void deleteAll() {
		openConnection();
		MongoCollection<Product> products = this.database.getCollection("products", Product.class);
		products.deleteMany(new Document());
		closeConnection();
	}
}
