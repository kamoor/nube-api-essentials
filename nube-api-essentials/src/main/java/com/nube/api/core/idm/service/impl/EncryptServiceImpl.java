package com.nube.api.core.idm.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import com.nube.core.service.security.EncryptService;

@Service
@Profile("default")
public class EncryptServiceImpl  implements EncryptService{

	
	@Value("${nube.security.text.encryption.key:1love4}")
	public String key;
	
	@Value("${nube.security.text.encryption.salt:nube}")
	public String salt;
	
	TextEncryptor encryptor;
	
	@PostConstruct
	public void  init() {
		//encryptor = Encryptors.text(key, salt);
		//AES 256 is giving JDK error without upgrade , needs to use AES 128 or 3DES
	}
	
	/**
	 * Encrypt data
	 */
	public String encrypt(String input) {
		//return encryptor.encrypt(input);
		return input;
	}

	/**
	 * Decrypt data
	 */
	public String decrypt(String input) {
		// return encryptor.decrypt(input);
		return input;
		
	}

}
