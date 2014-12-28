package com.nube.api.core.email.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nube.api.core.email.dao.EmailDao;
import com.nube.api.core.email.service.EmailService;
import com.nube.api.core.email.vo.Email;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.exception.NubeException;
import com.nube.core.util.string.StringUtil;

@Service
public class EmailServiceImpl implements EmailService {

	static Logger logger = Logger.getLogger(EmailServiceImpl.class);

	@Autowired
	EmailDao emailDao;
	
	
	@Value("${email.from.default: admin@nube.com}")
	String from;
	
	

	/**
	 * send email
	 */
	public void send(Email email) throws NubeException {
			this.validate(email);
			emailDao.insert(email);
		
	}
	
	
	/**
	 * Validate email content and attributes
	 * @param email
	 * @throws NubeException
	 */
	private void validate(Email email)throws NubeException{
		if(StringUtil.isEmpty(email.getTo()) || StringUtil.isEmpty(email.getSubject()) || StringUtil.isEmpty(email.getContent())){
			throw new NubeException(ErrorCodes.INVALID_INPUT,"key, context are mandatory");
		}
		
		if(StringUtil.isEmpty(email.getFrom())){
			email.setFrom(from);
		}
	
	}

	

}
