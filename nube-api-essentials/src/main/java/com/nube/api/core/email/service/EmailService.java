package com.nube.api.core.email.service;

import com.nube.api.core.email.vo.Email;
import com.nube.core.exception.NubeException;

public interface EmailService {

	/**
	 * send email
	 * 
	 * @param prop
	 * @throws NubeException
	 */
	public void send(Email email) throws NubeException;
	
	
	
	
	
}
