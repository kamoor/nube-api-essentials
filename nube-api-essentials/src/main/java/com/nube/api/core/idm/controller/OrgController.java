package com.nube.api.core.idm.controller;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nube.core.exception.NubeException;
import com.nube.core.service.idm.OrgService;
import com.nube.core.vo.idm.Org;
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
@RequestMapping("/v1/orgs")
public class OrgController {

	static Logger logger = Logger.getLogger(OrgController.class);

	@Autowired
	OrgService orgService;

	@PostConstruct
	public void postConstruct() {
		logger.info("org api initialized");
	}

	/**
	 * Create a new organization Method: POST
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> create(@RequestBody Org org) {
		logger.info("Create a new org  " + org.toString());
		try {
			orgService.insert(org);
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error creating org", nbe);
			return new ValidResponse<VoidResponse>(nbe);

		}

	}

	/**
	 * Update org Method: POST
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> update(@RequestBody Org org) {
		logger.info("Update org  " + org.toString());
		try {
			orgService.update(org);
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error updating org", nbe);
			return new ValidResponse<VoidResponse>(nbe);

		}

	}

	/**
	 * delete an org Method: post
	 * 
	 * @see Property
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> delete(@RequestBody Org org) {
		logger.debug("Delete " + org.toString());
		try {
			orgService.delete(org.getContext(), org.getOrgId());
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error deleting keyval", nbe);
			return new ValidResponse<VoidResponse>(nbe);
		}
	}

	/**
	 * Get my org info Example: "/my?context=xyz"
	 * 
	 * @return
	 */
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public @ResponseBody ValidResponse<Org> getMenu(
			@RequestParam(value = "context", required = true) String context) {

		try {
			return new ValidResponse<Org>(orgService.readByContext(context));

		} catch (NubeException nbe) {
			logger.error("error getting org: context =" + context);
			return new ValidResponse<Org>(nbe);
		}

	}

}
