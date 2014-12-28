package com.nube.api.core.idm.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nube.api.core.idm.request.UserRequest;
import com.nube.api.core.idm.util.SessionContext;
import com.nube.core.constants.ErrorCodes;
import com.nube.core.exception.NubeException;
import com.nube.core.service.idm.AuthService;
import com.nube.core.vo.idm.AuthResponse;
import com.nube.core.vo.response.Response;
import com.nube.core.vo.response.ValidResponse;


/**
 * authentication controller
 * 
 * @author kamoorr
 */
@Controller("adminAuthController")
public class AuthRestController {

	@PostConstruct
	public void postConstruct() {
		logger.info("auth api initialized");
	}

	@Autowired
	@Qualifier("nubeAuthService")
	AuthService authService;

	@Value("${idm.auth.session.cookie-name:st}")
	String sessionCookieName;

	@Value("${idm.auth.remember-me.cookie-name:rt}")
	String rememberMeCookieName;
	
	
	@Autowired
	Environment env;

	static Logger logger = Logger.getLogger(AuthRestController.class);

	/**
	 * Login, Content-Type should be application/json
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/auth/login", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody
	Response<AuthResponse> login(@RequestBody UserRequest userRequest,
			final HttpServletRequest request, final HttpServletResponse response) {

		try {

			logger.info(String.format("Login request for %s", userRequest.getEmail()));
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword());
			
			//set user session 
			//TODO: This needs to move web filter, rest api should return session token only
			SessionContext.setUserAuthentication(authService.authenticate(token));
			
		    return new ValidResponse<AuthResponse>(new AuthResponse(userRequest.getEmail(), null, null, authService.generateOAuthToken(SessionContext.getUser())));

		}catch (AuthenticationException authException) {
			return new ValidResponse<AuthResponse>(ErrorCodes.IDM_AUTH_WRONG_CREDENTIALS, null);
		}catch (NubeException authException) {
			return new ValidResponse<AuthResponse>(ErrorCodes.SERVER_ERROR, null);
		}

	}
	
	/**
	 * Login, Content-Type should be application/json
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/auth/forgot-password", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody
	Response<AuthResponse> forgotPassword(@RequestBody UserRequest userRequest,
			final HttpServletRequest request, final HttpServletResponse response) {

		try {

			logger.info(String.format("Forgot password request for %s", userRequest.getEmail()));
			authService.forgotPassword(userRequest.getEmail());
		 
		}catch (NubeException nubeException) {
			logger.error(String.format("Forgot password ends up in error [%s], but user will not notified for security for %s",nubeException.getErrorCode(), userRequest.getEmail()));
		}
		return new ValidResponse<AuthResponse>(new AuthResponse(userRequest.getEmail(), null, null, null));

	}

}
