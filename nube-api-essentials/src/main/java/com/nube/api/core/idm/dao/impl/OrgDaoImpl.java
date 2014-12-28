package com.nube.api.core.idm.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.idm.OrgDao;
import com.nube.core.dao.mongo.AbstractMongoDao;
import com.nube.core.dao.mongo.MongoConnection;
import com.nube.core.exception.NubeException;
import com.nube.core.util.collections.CollectionsUtil;
import com.nube.core.vo.common.Hours;
import com.nube.core.vo.common.Hours.Day;
import com.nube.core.vo.common.Location;
import com.nube.core.vo.idm.Org;

/**
 * Store and retrieve org information
 * @author kamoorr
 *
 */
@Repository
@Profile("default")
public class OrgDaoImpl extends AbstractMongoDao<BasicDBObject, Org> implements OrgDao {

	@Autowired
	MongoConnection mongoConnection;
	
	@Autowired
	OrgIdCounter orgIdCounter;

	private static final String COLLECTION = "orgs";

	static Logger logger = Logger.getLogger(OrgDaoImpl.class);

	public DBCollection getCollection() {
		return mongoConnection.getCollection(COLLECTION);
	}
	
	
	

	/**
	 * insert an org
	 */
	public int insert(Org org) throws NubeException {
		int nextId = orgIdCounter.nextVal();
		org.setOrgId(nextId);
		getCollection().save(this.serialize(org));
		return nextId;

	}
	
	/**
	 * Update org by orgId
	 */
	public void update(Org org) throws NubeException {
			logger.info("Updating "+ org.toString());
			BasicDBObject newDoc = new BasicDBObject().append("$set", this.serialize(org));
			getCollection().update(new BasicDBObject().
					append("orgId", org.getOrgId()).append("context", org.getContext()), newDoc);
	}

	/**
	 * Read org info
	 */
	public Org readByContext(String context) throws NubeException {

		BasicDBObject dbObject = (BasicDBObject) getCollection().findOne(
				new BasicDBObject().append("context", context));
		if (dbObject == null || dbObject.isEmpty()) {
			throw new NubeException(ErrorCodes.RECORD_NOT_FOUND, "org_not_found");
		}
		return this.parse(dbObject);

	}
	
	/**
	 * Read org info
	 */
	public Org readByOrgId(int orgId) throws NubeException {

		BasicDBObject dbObject = (BasicDBObject) getCollection().findOne(
				new BasicDBObject().append("orgId", orgId));
		if (dbObject == null || dbObject.isEmpty()) {
			throw new NubeException(ErrorCodes.RECORD_NOT_FOUND, "org_not_found");
		}
		return this.parse(dbObject);

	}
	
	

	public void delete(String context, int orgId) throws NubeException {
		getCollection().remove(new BasicDBObject().append("context", context).append("orgId", orgId));

	}

	
	/**
	 * TODO: child orgs
	 */
	public Org parse(BasicDBObject dbObject) {
		
		Org org= new Org();
		org.setContext(dbObject.getString("context"));
		org.setOrgId(dbObject.getInt("orgId"));
		org.setTitle(dbObject.getString("title"));
		org.setDescription(dbObject.getString("descr"));
		org.setExternalId(dbObject.getString("extId"));
		org.setPhone(dbObject.getString("phone"));
		org.setEmail(dbObject.getString("email"));
		org.setFax(dbObject.getString("fax"));
		org.setPrimary(dbObject.getBoolean("primary"));
		
		BasicDBObject locObj = (BasicDBObject)dbObject.get("loc");
		
		if(locObj !=null){
			Location loc = new Location();
			loc.setLocationId(locObj.getString("locId"));
			loc.setAddrLn1(locObj.getString("aLn1"));
			loc.setAddrLn2(locObj.getString("aLn2"));
			loc.setCity(locObj.getString("city"));
			loc.setZip(locObj.getString("zip"));
			loc.setState(locObj.getString("state"));
			loc.setCountry(locObj.getString("cntry"));
			loc.setLatitude(locObj.getDouble("ltt"));
			loc.setLongitude(locObj.getDouble("lgt"));
			loc.setTimeZone(locObj.getString("tZone"));
			
			BasicDBList hrsListObj = (BasicDBList)locObj.get("hrs");
			if(hrsListObj != null){
				for(Object obj: hrsListObj){
					BasicDBObject hrsObj = (BasicDBObject)obj;
					loc.addHours(new Hours(Day.valueOf(hrsObj.getString("day")), 
													hrsObj.getDouble("from"), 
													hrsObj.getDouble("to"), 
													hrsObj.getBoolean("closed"), 
													hrsObj.getBoolean("o24Hrs")));
					
				}
			}
			org.setLocation(loc);
		}
		
		BasicDBObject attrObj = (BasicDBObject) dbObject.get("attr");
		if(attrObj != null){
			for(String key: attrObj.keySet()){
				org.addAttr(key, attrObj.getString(key));
			}
		}
		
		return org;
		
		
	}

	public BasicDBObject serialize(Org org) {
		BasicDBObject dbObject = new BasicDBObject().append("context", org.getContext())
				.append("orgId", org.getOrgId())
				.append("title", org.getTitle())
				.append("descr", org.getDescription())
				.append("extId", org.getExternalId())
				.append("phone", org.getPhone())
				.append("email", org.getEmail())
				.append("fax", org.getFax())
				.append("primary", org.isPrimary());
		//store location info
		if(org.getLocation() != null){
			
				BasicDBObject locObject = new BasicDBObject().append("locId", org.getLocation().getLocationId())
				.append("aLn1", org.getLocation().getAddrLn1())
				.append("aLn2", org.getLocation().getAddrLn2())
				.append("city", org.getLocation().getCity())
				.append("zip", org.getLocation().getZip())
				.append("state", org.getLocation().getState())
				.append("cntry", org.getLocation().getCountry())
				.append("ltt", org.getLocation().getLatitude())
				.append("lgt", org.getLocation().getLongitude())
				.append("tZone", org.getLocation().getTimeZone());
			//store hours
			if(org.getLocation().getHours() != null){
				BasicDBList hrsList = new BasicDBList();
				for(Hours hr: org.getLocation().getHours()){
					hrsList.add(new BasicDBObject().append("day", hr.getDay().toString())
							.append("from", hr.getFrom())
							.append("to", hr.getTo())
							.append("closed", hr.isClosed())
							.append("o24Hrs", hr.isOpen24Hrs()));
				}
				locObject.append("hrs", hrsList);
			}
			
			dbObject.append("loc", locObject);
		}
		
		
		if(!CollectionsUtil.isEmpty(org.getAttr())){
			dbObject.append("attr", org.getAttr());
			
		}
		
		
		
		return dbObject;
	}

	

}
