package com.nube.api.core.idm.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.common.ContentProcessor;
import com.nube.core.dao.idm.UserDao;
import com.nube.core.dao.mongo.AbstractMongoDao;
import com.nube.core.dao.mongo.MongoConnection;
import com.nube.core.exception.NubeException;
import com.nube.core.vo.apps.App;
import com.nube.core.vo.idm.Role;
import com.nube.core.vo.idm.User;

/**
 * This implementation of user dao will persist data in mongo/user collection
 * Use rest api to store in relational db
 * 
 * @author kamoorr
 * 
 */
@Service
@Profile("default")
public class UserDaoImpl extends AbstractMongoDao<BasicDBObject, User> implements UserDao, ContentProcessor<BasicDBObject, User>{

	@Autowired
	MongoConnection mongoConnection;
	
	@Autowired
	UserCounter userCounter;

	static final String COLLECTION_BUNDLE = "users";

	static Logger logger = Logger.getLogger(UserDaoImpl.class);

	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION_BUNDLE);
	}

	
	
	/**
	 * Create new user account. Must provide password as digest,
	 */
	public int save(User user, String password, User createdByUser) {
		
		BasicDBObject userObject = this.serialize(user);
		userObject.append("userId",userCounter.nextVal())
				  .append("pwd", password);
		getCollection().save(userObject);
		
		return 1;
	}
	
	
	/**
	 * Grant a role
	 */
	public int authorize(User user, User updatedByUser, String context, Role role){
		user.addRole(context, role);
		return this.update(user, updatedByUser);
	}
	
	
	/**
	 * revoke a role
	 */
	public int revoke(User user, User updatedByUser, String context, Role role){
		user.getRoles().get(context).remove(role);
		return this.update(user, updatedByUser);
	}
	

	/**
	 * Update user info except password, must pass entire user object to update
	 */
	public int update(User user, User updatedByUser) {
		user.setLastUpdatedBy(updatedByUser.getId());
		logger.info("Updating "+ user.toString());
		BasicDBObject newDoc = new BasicDBObject().append("$set", this.serialize(user));
		getCollection().update(new BasicDBObject().append("userId", user.getId()), newDoc);
		return 1;
	}

	public boolean forgotPassword(String username) throws NubeException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean resetPassword(String passKey, String newPassword)
			throws NubeException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * All the checks should be done at service layer
	 * @param user
	 * @param newPassword
	 * @return
	 * @throws NubeException
	 */
	public boolean changePassword(User user, String newPassword) throws NubeException {
		BasicDBObject userObject = this.serialize(user);
		userObject.append("pwd", newPassword);
		this.update(user, user);
		return true;
	}

	/**
	 * Check if exists, return true if exists
	 */
	public boolean isExists(String email) {
		try {
			this.findByEmail(email);
			return true;
		} catch (NubeException e) {
			return false;
		}
	}

	/**
	 * find a user by user id
	 */
	public User findByUserId(int userId) throws NubeException {
		return this.find(new BasicDBObject().append("userId", userId), true)
				.get(0);
	}

	/**
	 * Find user by email addresss
	 */
	public User findByEmail(String email) throws NubeException {
		return this.find(
				new BasicDBObject().append("email", email.toLowerCase()), true)
				.get(0);
	}

	/**
	 * In default implementation , user name is same as email
	 * 
	 */
	public User findUserForLogin(String username, String password)
			throws NubeException {
		return this.find(
				new BasicDBObject().append("email", username.toLowerCase())
						.append("pwd", password), true).get(0);
	}

	/**
	 * Nothing much to do here
	 */
	public boolean logout(String username) throws NubeException {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Find objects with any query
	 * 
	 * @param searchQuery
	 * @param returnFirst
	 * @return
	 * @throws NubeException
	 */
	private List<User> find(BasicDBObject searchQuery, boolean returnFirst)
			throws NubeException {
		DBCursor result = getCollection().find(searchQuery);

		List<User> userList = new ArrayList<User>();
		for (DBObject row : result) {
			BasicDBObject basicDBObject = (BasicDBObject) row;
			userList.add(this.parse(basicDBObject));
			if (returnFirst)
				break;
		}

		if (userList.isEmpty()) {
			throw new NubeException(ErrorCodes.IDM_REG_USER_NOT_FOUND,
					"Unable to find user for " + searchQuery.toString());
		}
		return userList;

	}

	/**
	 * Parse JSON to user object
	 */
	public User parse(BasicDBObject object) {
		User user = new User();
		user.setId(object.getInt("userId"));
		user.setFirstName(object.getString("firstName"));
		user.setLastName(object.getString("lastName"));
		user.setEmail(object.getString("email"));
		user.setPrimaryContext(object.getString("primaryContext"));
		
		//Check to see roles to context/role map exists 
		BasicDBObject contextToRoleMap = (BasicDBObject) object.get("roles");
		if(contextToRoleMap != null){
			for(String context: contextToRoleMap.keySet()){
				BasicDBList roleList = (BasicDBList)contextToRoleMap.get(context);
				for(Object role: roleList){
					user.addRole(context, new Role(role.toString()));
				}
			}
		}
		return user;
	}



	@Override
	public BasicDBObject serialize(User user) {
		
		BasicDBObject dbObject = new BasicDBObject()
								.append("firstName", user.getFirstName())
								.append("lastName", user.getLastName())
								.append("email", user.getEmail().toLowerCase())
								.append("invitedBy", user.getLastUpdatedBy())
								.append("updatedBy", user.getLastUpdatedBy())
								.append("primaryContext", user.getPrimaryContext());
		
		//context to list of role storage.
		if(!user.getRoles().isEmpty()){
			BasicDBObject roles = new BasicDBObject();
			for(String context: user.getRoles().keySet()){
				BasicDBList roleList = new BasicDBList();
				for(Role role: user.getRoles().get(context)){
					roleList.add(role.getName());
				}
				roles.put(context, roleList);
			}
			dbObject.append("roles", roles);
		}
		
		return dbObject;
		
	
	}



	
}
