package com.nube.api.core.idm.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.idm.UserDao;
import com.nube.core.exception.NubeException;
import com.nube.core.service.idm.UserService;
import com.nube.core.service.security.EncodeService;
import com.nube.core.service.security.EncryptService;
import com.nube.core.util.string.StringUtil;
import com.nube.core.vo.idm.Role;
import com.nube.core.vo.idm.User;
import com.nube.core.vo.idm.UserDetailsImpl;


/**
 * User service oprations
 * 
 * @author kamoorr
 * 
 */
@Service("nubeUserService")
@Profile("default")
public class UserServiceImpl extends CommonIDMServiceImpl implements UserService{

	
	static Logger logger = Logger.getLogger(UserServiceImpl.class);

	/**
	 * Create a new user
	 * 
	 * @param user
	 * @param password
	 * @param user
	 * @return
	 * @throws NubeException
	 */
	public int create(User user, String password, User createdByUser)
			throws NubeException {

		if (user.getEmail() == null) {
			throw new NubeException(ErrorCodes.INVALID_INPUT,
					"Email address is missing");
		} else if (userDao.isExists(user.getEmail())) {
			logger.error("User record already exists for email "
					+ user.getEmail());
			throw new NubeException(ErrorCodes.IDM_REG_USER_EXISTS,
					"User already exists");
		}
		//provide access to internal app
		user.addRole(User.INTERNAL_APP, new Role(Role.role_admin));

		return userDao
				.save(user, encodeService.encode(password), createdByUser);
	}
	
	/**
	 * Must have user.getId() populated, then 
	 */
	public int update(User user, User updatedByUser)throws NubeException{
		
		User originalUser = userDao.findByUserId(user.getId());
		
		
		if(!StringUtil.isEmpty(user.getFirstName())){
			originalUser.setFirstName(user.getFirstName());
		}
		if(!StringUtil.isEmpty(user.getLastName())){
			originalUser.setLastName(user.getLastName());
		}
		if(!StringUtil.isEmpty(user.getEmail())){
			originalUser.setEmail(user.getEmail());
		}
		if(!StringUtil.isEmpty(user.getPrimaryContext())){
			originalUser.setPrimaryContext(user.getPrimaryContext());
		}
	
		return userDao.update(originalUser, updatedByUser);
		 
	}

	/**
	 * Used by spring security too
	 */
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		UserDetails udtls = null;
		try{
			 udtls = (UserDetails) userDao.findByEmail(username);
		}catch(NubeException e){
			throw new UsernameNotFoundException("user not found");
		}
		
		return udtls;
	}
	
	/**
	 * Find user by unique id
	 */
	public User loadUserByUserId(int userId) throws NubeException{
		return super.findUserByUserId(userId);
	}
	
	

	public void changePassword(User user, String oldPassword, String newPassword)
			throws NubeException {
		
		logger.info("Change password for "+ user.toString());
		User toBeValidatedUser = super.findUserWithUserNameAndPwd(user.getEmail(), oldPassword);
		//TODO: See super class reset password, both needs to have check for admin/super admin access
		if(toBeValidatedUser.getId() != user.getId()){
			throw new NubeException(ErrorCodes.IDM_RESET_PWD_UNAUTHORIZED, "User not found");
		}
		userDao.changePassword(user, newPassword);
	}

	public void resetPassword(User user, String resetKey, String newPassword)
			throws NubeException {
		boolean isValid = super.validateResetPwdKey(user, resetKey);
		logger.info(String.format("Password reset key for %s , isValid = %s", user.getEmail(), isValid));
		userDao.changePassword(user, newPassword);
	}

	
	
	
	
	
	

}
