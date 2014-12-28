package com.nube.api.core.keyval.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.nube.api.core.keyval.dao.KeyValDao;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.mongo.AbstractMongoDao;
import com.nube.core.dao.mongo.MongoConnection;
import com.nube.core.exception.NubeException;
import com.nube.core.vo.system.Property;

/**
 * Store and retrieve key value pairs for context in mongo, "properties" collection
 * @author kamoorr
 *
 */
@Repository
@Profile("default")
public class KeyValDaoImpl extends AbstractMongoDao<BasicDBObject, Property> implements KeyValDao{

	@Autowired
	MongoConnection mongoConnection;

	private static final String COLLECTION = "properties";

	static Logger logger = Logger.getLogger(KeyValDaoImpl.class);

	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION);
	}
	
	
	

	/**
	 * insert a menu
	 */
	public void insert(Property property) throws NubeException {
		getCollection().save(this.serialize(property));

	}
	
	/**
	 * Update value based on context and key
	 */
	public void update(Property property) throws NubeException {
			logger.info("Updating "+ property.toString());
			BasicDBObject newDoc = new BasicDBObject().append("$set", this.serialize(property));
			getCollection().update(new BasicDBObject().
					append("context", property.getContext()).append("key", property.getKey()), newDoc);
	}

	/**
	 * Read the whole props for a context
	 */
	public List<Property> read(String context) throws NubeException {

		DBCursor cursor = getCollection().find(new BasicDBObject().append("context", context));
		if (cursor.count() == 0) {
			throw new NubeException(ErrorCodes.RECORD_NOT_FOUND, "key_val_not_found");
		}
		List<Property> results = new ArrayList<Property>();
		while (cursor.hasNext()) {
			results.add(this.parse((BasicDBObject) cursor.next()));
		}
		return results;

	}

	/**
	 * Read the one prop
	 */
	public Property read(String context, String key) throws NubeException {

		BasicDBObject dbObject = (BasicDBObject) getCollection().findOne(
				new BasicDBObject().append("context", context).append("key", key));
		if (dbObject == null || dbObject.isEmpty()) {
			throw new NubeException(ErrorCodes.RECORD_NOT_FOUND, "key_val_not_found");
		}
		return this.parse(dbObject);
	}
	
	

	public void delete(String context, String key) throws NubeException {
		getCollection().remove(new BasicDBObject().append("context", context).append("key", key));

	}

	
	public Property parse(BasicDBObject dbObject) {
		
		Property prop = new Property();
		prop.setContext(dbObject.getString("context"));
		prop.setKey(dbObject.getString("key"));
		prop.setValue(dbObject.getString("val"));
		return prop;
		
	}

	public BasicDBObject serialize(Property pojo) {
		BasicDBObject dbObject = new BasicDBObject().append("context", pojo.getContext())
				.append("key", pojo.getKey())
				.append("val", pojo.getValue());
		
		return dbObject;
	}

	

}
