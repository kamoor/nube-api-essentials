package com.nube.api.core.email.dao;

import com.nube.api.core.email.vo.Email;
import com.nube.core.exception.NubeException;

public interface EmailDao {

	/**
	 * Insert email
	 * 
	 * @param email
	 * @throws NubeException
	 */
	public void insert(Email email) throws NubeException;

	
	
}
