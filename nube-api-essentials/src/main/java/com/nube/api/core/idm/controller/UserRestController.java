package com.nube.api.core.idm.controller;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nube.api.core.idm.request.PasswordRequest;
import com.nube.api.core.idm.request.UserRequest;
import com.nube.api.core.idm.service.impl.UserServiceImpl;
import com.nube.api.core.idm.util.SessionContext;
import com.nube.core.exception.NubeException;
import com.nube.core.vo.idm.User;
import com.nube.core.vo.response.Response;
import com.nube.core.vo.response.ValidResponse;

/**
 * Admin controller
 * 
 * @author kamoorr
 */
@Controller("adminUserController")
public class UserRestController {

	@Autowired
	UserServiceImpl userService;

	@PostConstruct
	public void postConstruct() {
		logger.info("user rest api initialized");
	}

	static Logger logger = Logger.getLogger(UserRestController.class);

	/**
	 * Create a new user, Content-Type should be application/json
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/users/create", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody
	Response<Void> create(@RequestBody UserRequest userRequest) {

		try {
			logger.info("Create user "+ userRequest.toString());
			userService.create((User) userRequest, userRequest.getPassword(),
					(User) userRequest);
			return new ValidResponse<Void>((Void) null);
		} catch (NubeException nubeException) {
			return new ValidResponse<Void>(nubeException);
		}

	}
	
	/**
	 * Create a new user, Content-Type should be application/json
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/users/my", method = RequestMethod.GET, consumes = "application/json")
	public @ResponseBody
	Response<User> my() {
		try{
			//Do not trust user object in session
			return new ValidResponse<User>(userService.loadUserByUserId(SessionContext.getUser().getId()));
		}catch (NubeException nubeException) {
			return new ValidResponse<User>(nubeException);
		}
	}

	/**
	 * Create a new user, Content-Type should be application/json
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/users/change-password", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody
	Response<Void> changePassword(@RequestBody PasswordRequest pwdRequest) {

		try {
			userService.changePassword(SessionContext.getUser(), pwdRequest.getCurrentPassword(), pwdRequest.getNewPassword());
			return new ValidResponse<Void>((Void) null);
		} catch (NubeException nubeException) {
			logger.error(nubeException.getMessage());
			return new ValidResponse<Void>(nubeException);
		}

	}
	
	/**
	 * Update user, Content-Type should be application/json
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/v1/users/update", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody
	Response<Void> updateUser(@RequestBody UserRequest userRequest) {

		try {
			//TODO How to allow admin to update someone else info?
			userRequest.setId(SessionContext.getUser().getId());
			userService.update(userRequest, SessionContext.getUser());
			return new ValidResponse<Void>((Void) null);
		} catch (NubeException nubeException) {
			logger.error(nubeException.getMessage());
			return new ValidResponse<Void>(nubeException);
		}

	}

}
