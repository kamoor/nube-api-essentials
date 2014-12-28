package com.nube.api.core.idm.service.impl;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.nube.core.constants.ErrorCodes;
import com.nube.core.exception.NubeException;
import com.nube.core.service.idm.AuthService;
import com.nube.core.util.security.SecurityUtil;
import com.nube.core.vo.idm.User;

@Service("nubeAuthService")
@Profile("default")
public class AuthServiceImpl extends CommonIDMServiceImpl implements AuthService {


	static Logger logger = Logger.getLogger(AuthServiceImpl.class);

	/**
	 * Logout, expire token TODO
	 */
	public void logout(String username) throws NubeException {
		return;

	}

	/**
	 * Generate session token for browser login
	 */
	public String generateSessionToken(User user) throws NubeException {
		return "todo-sess-token";

	}

	/**
	 * Generate remember me token
	 */
	public String generateRememberMeToken(User user) throws NubeException {
		return "todo-remember-token";

	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		try{
			User user = super.findUserWithUserNameAndPwd(authentication.getName(), authentication.getCredentials().toString());
			Authentication auth = new UsernamePasswordAuthenticationToken(user, authentication.getCredentials().toString(), user.getRoles(User.INTERNAL_APP));
			return auth;
		}catch(NubeException e){
			if(e.getErrorCode() == ErrorCodes.IDM_REG_USER_NOT_FOUND){
				throw new BadCredentialsException("username_pwd_does_not_match");
			}else{
				throw new BadCredentialsException("unknown");
			}
		}

	}

	/**
	 * Send forgot password link to user, no db update
	 */
	public boolean forgotPassword(String username) throws NubeException {
		User user  = userDao.findByEmail(username);
		String resetKey = super.generateResetPwdKey(user);
		//Send email from here
		
		return true;
		
	}
	
	
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public String generateOAuthToken(User user) throws NubeException {
		return super.generateSecurityKey(user);
	}

}
