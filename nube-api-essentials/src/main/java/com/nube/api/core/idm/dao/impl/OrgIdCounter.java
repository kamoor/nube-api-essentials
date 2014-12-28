package com.nube.api.core.idm.dao.impl;

import org.springframework.stereotype.Repository;

import com.nube.core.dao.mongo.MongoCounter;

@Repository
public class OrgIdCounter extends MongoCounter {

	@Override
	public String getCounterType() {
		return "orgid";
	}

	@Override
	public int getMin() {
		return 1;
	}

}
