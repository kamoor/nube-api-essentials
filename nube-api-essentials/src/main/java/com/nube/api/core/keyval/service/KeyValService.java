package com.nube.api.core.keyval.service;

import java.util.List;

import com.nube.core.exception.NubeException;
import com.nube.core.vo.system.Property;

public interface KeyValService {

	/**
	 * Insert keyval
	 * 
	 * @param prop
	 * @throws NubeException
	 */
	public void insert(Property prop) throws NubeException;
	
	
	/**
	 * update keyval
	 * 
	 * @param prop
	 * @throws NubeException
	 */
	public void update(Property prop) throws NubeException;

	/**
	 * delete a keyvalue pair 
	 * @param context
	 * @param key
	 * @throws NubeException
	 */
	public void delete(String context, String key) throws NubeException;


	/**
	 * Read all key val pairs for a context
	 * @param context
	 * @return
	 * @throws NubeException
	 */
	public List<Property> read(String context) throws NubeException;
	
	/**
	 * Read one Key Val
	 * @param context
	 * @param key
	 * @return
	 * @throws NubeException
	 */
	public Property read(String context, String key) throws NubeException;

	
	
	
}
