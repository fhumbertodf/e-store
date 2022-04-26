package com.estore.codec;

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
import com.estore.domain.EmailAddress;

public class CustomerCodec implements CollectibleCodec<Customer> {

	private Codec<Document> codec;

	public CustomerCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Customer value, EncoderContext encoderContext) {
		BigInteger id = value.getId();
		String firstname = value.getFirstname();
		String lastname = value.getLastname();

		EmailAddress emailAddress = value.getEmailAddress();
		Set<Address> addresses = value.getAddresses();

		Document document = new Document();

		document.put("_id", new ObjectId(id.toString(16)));		
		document.put("firstname", firstname);
		document.put("lastname", lastname);
		document.put("email", new Document("email", emailAddress.toString()));

		if (addresses != null) {
			List<Document> addressesDocument = new ArrayList<>();
			for (Address addresse : addresses) {
				addressesDocument.add(new Document("street", addresse.getStreet()).append("city", addresse.getCity())
						.append("country", addresse.getCountry()));

			}
			document.put("addresses", addressesDocument);
		}

		codec.encode(writer, document, encoderContext);
	}

	@Override
	public Class<Customer> getEncoderClass() {
		return Customer.class;
	}

	@Override
	public Customer decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = codec.decode(reader, decoderContext);

		Customer customer = new Customer(document.getString("firstname"), document.getString("lastname"));
		customer.setId(new BigInteger(document.getObjectId("_id").toHexString(), 16));
		
		Document emailDocument = (Document) document.get("email");		
		customer.setEmailAddress(new EmailAddress(emailDocument.getString("email")));
		
		List<Document> addresses = document.getList("addresses", Document.class);
		if (addresses != null) {			
			for (Document documentAddress : addresses) {
				customer.add(new Address(documentAddress.getString("street"), documentAddress.getString("city"),
						documentAddress.getString("country")));				
			}			
		}

		return customer;
	}

	@Override
	public Customer generateIdIfAbsentFromDocument(Customer document) {
		ObjectId objectId = new ObjectId();		
		return documentHasId(document) ? document.setId(new BigInteger(objectId.toHexString(), 16)) : document;
	}

	@Override
	public boolean documentHasId(Customer document) {
		return document.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Customer document) {
		if (!documentHasId(document)) {
			throw new IllegalStateException("Esse Document nao tem id");
		}
		return new BsonString(document.getId().toString(16));
	}

}
