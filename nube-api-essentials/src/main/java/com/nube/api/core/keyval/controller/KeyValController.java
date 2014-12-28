package com.nube.api.core.keyval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nube.api.core.keyval.service.KeyValService;
import com.nube.core.exception.NubeException;
import com.nube.core.vo.response.ValidResponse;
import com.nube.core.vo.response.VoidResponse;
import com.nube.core.vo.system.Property;

/**
 * KeyVal Rest controller. See method comments to use this api
 * 
 * @author kamoorr
 *
 */
@Controller
@RequestMapping("/v1/keyval")
public class KeyValController {

	static Logger logger = Logger.getLogger(KeyValController.class);

	@Autowired
	KeyValService keyValService;

	@PostConstruct
	public void postConstruct() {
		logger.info("keyval api initialized");
	}

	/**
	 * Create a new key val pair in a context, Content-Type should be
	 * application/json. Request Body should map to Property.java Request
	 * Method: POST
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> create(
			@RequestBody Property prop) {
		logger.info("Create a new keyval pair " + prop.toString());
		try {
			keyValService.insert(prop);
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error creating keyval", nbe);
			return new ValidResponse<VoidResponse>(nbe);

		}

	}

	/**
	 * Update a value in keyval pair in a context, Content-Type should be
	 * application/json. Request Body should map to Property.java Request
	 * Method: POST
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> update(
			@RequestBody Property prop) {
		logger.info("Update keyval pair " + prop.toString());
		try {
			keyValService.update(prop);
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error updating keyval", nbe);
			return new ValidResponse<VoidResponse>(nbe);

		}

	}

	/**
	 * delete a keyval pair. context and key are mandatory Content-Type should
	 * be application/json. Request Body should map to Property.java (only
	 * menuId and context is required) Request Method: POST
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> delete(
			@RequestBody Property prop) {
		logger.debug("Delete " + prop.toString());
		try {
			keyValService.delete(prop.getContext(), prop.getKey());
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error deleting keyval", nbe);
			return new ValidResponse<VoidResponse>(nbe);
		}
	}

	/**
	 * Get all properties for a context or a specific keyval for a context
	 * Example: "/my?context=xyz" Example: "/my?context=xyz&key=xyss"
	 * 
	 * @return
	 */
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public @ResponseBody ValidResponse<Map<String, String>> getMenu(
			@RequestParam(value = "context", required = true) String context,
			@RequestParam(value = "key", required = false) String key) {

		try {
			Map<String, String> map = new HashMap<String, String>();

			if (key == null) {
				List<Property> props = keyValService.read(context);
				for (Property prop : props) {
					map.put(prop.getKey(), prop.getValue());
				}
			} else {
				Property prop = keyValService.read(context, key);
				map.put(prop.getKey(), prop.getValue());
			}
			return new ValidResponse<Map<String, String>>(map);

		} catch (NubeException nbe) {
			logger.error("error getting keyval: context =" + context + "-key="
					+ key);
			return new ValidResponse<Map<String, String>>(nbe);
		}

	}

}
