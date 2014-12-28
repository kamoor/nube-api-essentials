package com.nube.api.core.idm.request;

import com.nube.core.vo.idm.User;

/**
 * Create User Request: create update delete login logout
 * 
 * @author kamoorr
 * 
 */
public class UserRequest extends User {

	String password;

	boolean rememberMe;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
