package com.nube.api.core.email.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.nube.api.core.email.dao.EmailDao;
import com.nube.api.core.email.vo.Email;
import com.nube.core.dao.common.ContentProcessor;
import com.nube.core.dao.mongo.AbstractMongoDao;
import com.nube.core.dao.mongo.MongoConnection;
import com.nube.core.exception.NubeException;
import com.nube.core.util.date.DateUtil;

/**
 * Email content insert to database
 * @author kamoorr
 *
 */
@Repository
@Profile("default")
public class EmailDaoImpl extends AbstractMongoDao<BasicDBObject, Email> implements EmailDao {

	@Autowired
	MongoConnection mongoConnection;

	private static final String COLLECTION = "emails";

	static Logger logger = Logger.getLogger(EmailDaoImpl.class);

	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION);
	}
	
	
	

	/**
	 * insert a menu
	 */
	public void insert(Email email) throws NubeException {
		email.setSendDate(DateUtil.getSysDateString());
		getCollection().save(this.serialize(email));

	}
	


	/**
	 * Parse email content
	 */
	public Email parse(BasicDBObject dbObject) {
		
		Email email = new Email(dbObject.getString("to"), dbObject.getString("subject"), dbObject.getString("content"));
		email.setFrom(dbObject.getString("from"));
		email.setStatus(dbObject.getString("status"));
		email.setContentType(dbObject.getString("cType"));
		return email;
		
	}

	/**
	 * Serialize email content
	 */
	public BasicDBObject serialize(Email pojo) {
		return new BasicDBObject()
				.append("from", pojo.getFrom())
				.append("to", pojo.getTo())
				.append("subject", pojo.getSubject())
			    .append("content", pojo.getContent())
				.append("cType", pojo.getContentType())
				.append("status", pojo.getStatus())
				.append("sendDt", pojo.getSendDate()
				);
	}

	

}
