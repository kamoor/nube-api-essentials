package com.nube.api.core.keyval.dao;

import java.util.List;

import com.nube.core.exception.NubeException;
import com.nube.core.vo.system.Property;

public interface KeyValDao {

	/**
	 * Insert property
	 * 
	 * @param menu
	 * @throws NubeException
	 */
	public void insert(Property property) throws NubeException;

	
	/**
	 * Update property value only
	 * @param menuId
	 * @param menu
	 * @throws NubeException
	 */
	public void update(Property property)throws NubeException;
	
	/**
	 * Read all properties for a context
	 * @param context
	 * @return
	 * @throws NubeException
	 */
	public List<Property> read(String context) throws NubeException;
	
	/**
	 * Read one property
	 * @param context
	 * @return
	 * @throws NubeException
	 */
	public Property read(String context, String key) throws NubeException;

	
	/**
	 * delete one property
	 * @param menuId
	 * @throws NubeException
	 */
	public void delete(String context, String key) throws NubeException;

	
}
