package com.nube.api.core.email.controller;

import java.util.ArrayList;
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

import com.nube.api.core.email.service.EmailService;
import com.nube.api.core.email.vo.Email;
import com.nube.api.core.keyval.service.KeyValService;
import com.nube.core.exception.NubeException;
import com.nube.core.vo.response.ValidResponse;
import com.nube.core.vo.response.VoidResponse;
import com.nube.core.vo.system.Property;

/**
 * Send email
 * 
 * @author kamoorr
 *
 */
@Controller
@RequestMapping("/v1/email")
public class EmailController {

	static Logger logger = Logger.getLogger(EmailController.class);

	@Autowired
	EmailService emailService;

	@PostConstruct
	public void postConstruct() {
		logger.info("email api initialized");
	}

	/**
	 * This api will send email.
	 * Subject , to, content etc are mandatory
	 * To can be comma seperated
	 * Method: POST
	 * @see Email to learn content of email to be expected by this API
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ValidResponse<VoidResponse> create(
			@RequestBody Email email) {
		logger.info("Send email  " + email.toString());
		try {
			emailService.send(email);
			return new ValidResponse(new VoidResponse());
		} catch (NubeException nbe) {
			logger.error("error creating keyval", nbe);
			return new ValidResponse<VoidResponse>(nbe);

		}

	}

	
}
