package com.estore.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import com.estore.domain.Product;

public class ProductCodec implements CollectibleCodec<Product> {

	private Codec<Document> codec;

	public ProductCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Product value, EncoderContext encoderContext) {
		BigInteger id = value.getId();
		String name = value.getName();
		String description = value.getDescription();
		BigDecimal price = value.getPrice();		

		Document document = new Document();
		document.put("_id", new ObjectId(id.toString(16)));
		document.put("name", name);
		if(description != null) {
			document.put("description", description);
		}
		document.put("price", price.doubleValue());

		Map<String, String> attributes = value.getAttributes();
		if (!attributes.isEmpty()) {
			Document attributesDocument = new Document();
			for (String atribute : attributes.keySet()) {
				attributesDocument.append(atribute, attributes.get(atribute));
			}
			document.put("attributes", attributesDocument);
		}
		codec.encode(writer, document, encoderContext);
	}

	@Override
	public Class<Product> getEncoderClass() {
		return Product.class;
	}

	@Override
	public Product decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = codec.decode(reader, decoderContext);

		Product product = new Product(document.getString("name"), BigDecimal.valueOf(document.getDouble("price")),
				document.getString("description"));
		product.setId(new BigInteger(document.getObjectId("_id").toHexString(), 16));

		Document attributesDocument = (Document) document.get("attributes");				
		if (attributesDocument != null) {
			for (String attribute : attributesDocument.keySet()) {
				product.setAttribute(attribute, attributesDocument.getString(attribute));				
			}
		}
		return product;
	}

	@Override
	public Product generateIdIfAbsentFromDocument(Product document) {
		ObjectId objectId = new ObjectId();
		return documentHasId(document) ? document.setId(new BigInteger(objectId.toHexString(), 16)) : document;
	}

	@Override
	public boolean documentHasId(Product document) {
		return document.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Product document) {
		if (!documentHasId(document)) {
			throw new IllegalStateException("Esse Document nao tem id");
		}
		return new BsonString(document.getId().toString(16));
	}

}
