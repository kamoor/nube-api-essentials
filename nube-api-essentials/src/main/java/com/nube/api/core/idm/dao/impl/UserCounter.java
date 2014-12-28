package com.nube.api.core.idm.dao.impl;

import org.springframework.stereotype.Repository;

import com.nube.core.dao.mongo.MongoCounter;


/**
 * User counter
 * @author kamoorr
 *
 */
@Repository
public class UserCounter extends MongoCounter {

	@Override
	public String getCounterType() {
		return "userid";
	}

	@Override
	public int getMin() {
		return 1;
	}

}
