package com.nube.api.core.idm.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.idm.UserDao;
import com.nube.core.exception.NubeException;
import com.nube.core.service.security.EncodeService;
import com.nube.core.service.security.EncryptService;
import com.nube.core.util.date.DateUtil;
import com.nube.core.util.security.SecurityUtil;
import com.nube.core.vo.idm.User;

/**
 * This is not a spring objects, it inherits to both auth and user service impls
 * 
 * @author kamoorr
 * 
 */
public class CommonIDMServiceImpl {

	@Autowired
	protected UserDao userDao;

	@Autowired
	protected EncodeService encodeService;

	@Autowired
	protected EncryptService encryptService;

	@Value("${auth.reset-password.link.expire.hrs:24}")
	protected int resetPwdLinkExpireHrs;

	protected long resetPwdLinkExpireMilliSec;

	protected final static String SEPERATOR = ":";

	protected final static String SERPERATOR_REGEX = "[" + SEPERATOR + "]";

	@PostConstruct
	public void init() {
		resetPwdLinkExpireMilliSec = resetPwdLinkExpireHrs * 60 * 60 * 1000;
	}

	/**
	 * Used by login and change password
	 * @param username
	 * @param password
	 * @return
	 */
	protected User findUserWithUserNameAndPwd(String username, String password) throws NubeException{
		return userDao.findUserForLogin(username, encodeService.encode(password));
	}
	
	/**
	 * Used by spring security too
	 */
	public User findUserByUserId(int userId)
			throws UsernameNotFoundException {
		
		try{
			return userDao.findByUserId(userId);
		}catch(NubeException e){
			throw new UsernameNotFoundException("user not found");
		}
	}
	
	
	
	
	/**
	 * Generate one time reset password key
	 * 
	 * @param user
	 * @return
	 */
	protected String generateResetPwdKey(User user) {
		return user.getId() + SEPERATOR
				+ (new Date().getTime() + resetPwdLinkExpireMilliSec);
	}

	/**
	 * Validate one time reset password key
	 * @param user
	 * @return
	 */
	protected boolean validateResetPwdKey(User user, String resetKey)
			throws NubeException {
		String decryptedKey = encryptService.decrypt(resetKey);
		String tokens[] = decryptedKey.split(SERPERATOR_REGEX);
		int userId = Integer.parseInt(tokens[0]);
		long expirationTime = Long.parseLong(tokens[1]);

		// user check, need to add super admin check
		if (userId != user.getId()) {
			throw new NubeException(ErrorCodes.IDM_RESET_PWD_UNAUTHORIZED,
					"User is not allowed to reset password");
		}

		// Check for key expiration
		if (new Date().getTime() > expirationTime) {
			throw new NubeException(ErrorCodes.IDM_RESET_PWD_EXPIRED,
					"Password reset key expired");
		}

		// Check for user existance, TODO
		return true;

	}
	
	/**
	 * This will generate key String 
	 * @param user
	 * @return
	 */
	protected String getKeyString(User user){
		return user.getId() + ":" + DateUtil.getSysDateString();
	}
	
	/**
	 * Return encrypted key string
	 * @param user
	 * @return
	 * @throws NubeException
	 */
	protected String generateSecurityKey(User user)throws NubeException{
		return SecurityUtil.encryptByAes(this.getKeyString(user));
	}

}
