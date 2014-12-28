package com.nube.api.core.idm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nube.core.service.security.EncodeService;

/**
 * Encode a password or text based on preconfigured algorithm
 * 
 * @author kamoorr
 * 
 */
@Service
@Profile("default")
public class EncodeServiceImpl implements EncodeService {

	@Autowired
	MessageDigestPasswordEncoder passwordEncoder;

	@Value("${nube.security.pwd.encoding.salt}")
	public String salt;

	/**
	 * Encode a password based on declared hash Default MD5
	 */
	public String encode(String text) {
		return passwordEncoder.encodePassword(text, salt);
	}

	/**
	 * Validate a given hash with generated hash return true if valid
	 */
	public boolean validate(String text, String hash) {
		return hash.equals(passwordEncoder.encodePassword(text, salt));
	}

}
