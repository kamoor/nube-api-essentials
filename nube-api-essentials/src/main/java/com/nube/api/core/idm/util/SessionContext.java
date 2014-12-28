package com.nube.api.core.idm.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nube.core.vo.idm.User;

/**
 * User session context, This is not the righ place to put this util.
 * Every web app uses auth key needs to have it (TODO)
 * @author kamoorr
 *
 */
public class SessionContext {
	
	//Turn on debug mode for a session
	private static final ThreadLocal<Boolean> thredLocalForDebug = new ThreadLocal<Boolean>();
	
	/**
	 * Set user authentication
	 * @param authentication
	 */
	public static void setUserAuthentication(Authentication authentication){
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	
	/**
	 * Get user authentication 
	 * @return
	 */
	public static Authentication getUserAuthentication(){
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	/**
	 * Get authenticated user, return null for unauthenticated user
	 * @return
	 */
	public static User getUser(){
		
		Object principal = getUserAuthentication().getPrincipal();
		if(principal == null || !(principal instanceof User)){
			return null;
		}
		
		return  (User)principal;
	}
	
	/**
	 * Is user authenticated
	 * @return
	 */
	public static boolean isAuthenticated(){
		return getUserAuthentication().getPrincipal() != null;
	}
	
	
	/**
	 * Turn on debug mode for a session
	 * @param debug
	 */
	public static void setDebug(boolean debug){
		//Most cases, we dont need to set this value for consumer
		if(debug == false && thredLocalForDebug.get() == null)
			return;
		thredLocalForDebug.set(debug);
	}
	
	/**
	 * See if debug mode is turned on
	 * @return
	 */
	public static boolean isDebug(){
		return thredLocalForDebug.get() == null? false: thredLocalForDebug.get();
	}

}
