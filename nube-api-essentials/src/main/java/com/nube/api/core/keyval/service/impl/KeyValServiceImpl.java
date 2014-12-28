package com.nube.api.core.keyval.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nube.api.core.keyval.dao.KeyValDao;
import com.nube.api.core.keyval.service.KeyValService;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.exception.NubeException;
import com.nube.core.util.string.StringUtil;
import com.nube.core.vo.system.Property;

@Service
public class KeyValServiceImpl implements KeyValService {

	static Logger logger = Logger.getLogger(KeyValServiceImpl.class);

	@Autowired
	KeyValDao keyValDao;

	/**
	 * insert a key val pair
	 */
	public void insert(Property prop) throws NubeException {
		
		
		try{
			this.validate(prop);
			Property existing = keyValDao.read(prop.getContext(), prop.getKey());
			throw new NubeException(ErrorCodes.RECORD_ALREADY_EXISTS, "key_val_exists");
		}catch(NubeException e){
			if(e.getErrorCode() == ErrorCodes.RECORD_NOT_FOUND){
				keyValDao.insert(prop);
			}else{
				throw e;
			}
		}

	}
	
	
	private void validate(Property prop)throws NubeException{
		if(StringUtil.isEmpty(prop.getKey()) || StringUtil.isEmpty(prop.getContext())){
			throw new NubeException(ErrorCodes.INVALID_INPUT,"key, context are mandatory");
		}
		//Make it lower case
		prop.setKey(StringUtil.toKey(prop.getKey()));
	}

	
	/**
	 * update value
	 */
	public void update(Property prop) throws NubeException {
			this.validate(prop);
			Property existing = keyValDao.read(prop.getContext(), prop.getKey());
			//record exists
			keyValDao.update(prop);
	}
	
	
	/**
	 * delete property
	 */
	public void delete(String context, String key) throws NubeException {
		keyValDao.delete(context, key);
	}

	

	/**
	 * Read all key val pairs
	 */
	public List<Property> read(String context) throws NubeException {

		return keyValDao.read(context);

	}

	/**
	 * Read one key val pair
	 */
	public Property read(String context, String key) throws NubeException {

		return keyValDao.read(context, key);
	}

}
