package com.estore.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.estore.domain.Address;
import com.estore.domain.Customer;
import com.estore.domain.LineItem;
import com.estore.domain.Order;
import com.mongodb.DBRef;

public class OrderCodec implements CollectibleCodec<Order> {

	private Codec<Document> codec;

	public OrderCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Order value, EncoderContext encoderContext) {
		BigInteger id = value.getId();
		Address shippingAddress = value.getShippingAddress();
		Address billingAddress = value.getBillingAddress();
		Customer customer = value.getCustomer();
		Set<LineItem> lineItems = value.getLineItems();
		BigDecimal total = value.getTotal();

		Document document = new Document();
		document.put("_id", new ObjectId(id.toString(16)));
		document.put("shippingAddress", new Document("street", shippingAddress.getStreet())
				.append("city", shippingAddress.getCity()).append("country", shippingAddress.getCountry()));
		document.put("billingAddress", new Document("street", billingAddress.getStreet())
					.append("city", billingAddress.getCity()).append("country", billingAddress.getCountry()));
		
		document.put("total", total.doubleValue());
		document.put("customer", new DBRef("customers", new ObjectId(customer.getId().toString(16)))); 

		if (lineItems != null) {
			List<Document> lineItemsDocument = new ArrayList<>();
			for (LineItem lineItem : lineItems) {
				lineItemsDocument.add(new Document("price", lineItem.getUnitPrice().doubleValue())
						.append("amount", lineItem.getAmount())
						.append("product", new DBRef("products", new ObjectId(lineItem.getProduct().getId().toString(16)))));
			}
			document.put("lineItems", lineItemsDocument);
		}		

		codec.encode(writer, document, encoderContext);
	}

	@Override
	public Class<Order> getEncoderClass() {
		return Order.class;
	}

	@Override
	public Order decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = codec.decode(reader, decoderContext);

		DBRef dbref = (DBRef) document.get("customer");
		Customer customer = new Customer("-","-").setId(new BigInteger(dbref.getId().toString(), 16));

		Document shippingAddressDocument = (Document) document.get("shippingAddress");

		Address shippingAddress = new Address(shippingAddressDocument.getString("street"),
				shippingAddressDocument.getString("city"), shippingAddressDocument.getString("country"));

		Document billingAddressDocument = (Document) document.get("billingAddress");
		Address billingAddress = null;
		if (billingAddressDocument != null) {
			billingAddress = new Address(billingAddressDocument.getString("street"),
					billingAddressDocument.getString("city"), billingAddressDocument.getString("country"));
		}

		Order order = new Order(customer, shippingAddress, billingAddress);

		return order;
	}

	@Override
	public Order generateIdIfAbsentFromDocument(Order document) {
		ObjectId objectId = new ObjectId();
		return documentHasId(document) ? document.setId(new BigInteger(objectId.toHexString(), 16)) : document;
	}

	@Override
	public boolean documentHasId(Order document) {
		return document.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Order document) {
		if (!documentHasId(document)) {
			throw new IllegalStateException("Esse Document nao tem id");
		}
		return new BsonString(document.getId().toString(16));
	}

}
