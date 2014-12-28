package com.nube.api.core.idm.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nube.core.constants.ErrorCodes;
import com.nube.core.dao.idm.OrgDao;
import com.nube.core.exception.NubeException;
import com.nube.core.service.idm.OrgService;
import com.nube.core.util.string.StringUtil;
import com.nube.core.vo.common.Hours;
import com.nube.core.vo.idm.Org;

@Service
public class OrgServiceImpl implements OrgService {

	static Logger logger = Logger.getLogger(OrgServiceImpl.class);

	@Autowired
	OrgDao orgDao;

	/**
	 * insert a key val pair
	 */
	public void insert(Org org) throws NubeException {

		try {
			this.validate(org);
			// Until we have more than one org concept, this is primary
			org.setPrimary(true);
			Org existing = orgDao.readByContext(org.getContext());
			throw new NubeException(ErrorCodes.RECORD_ALREADY_EXISTS,
					"org_exists");
		} catch (NubeException e) {
			if (e.getErrorCode() == ErrorCodes.RECORD_NOT_FOUND) {
				orgDao.insert(org);
			} else {
				throw e;
			}
		}

	}

	private void validate(Org org) throws NubeException {

		if (StringUtil.isEmpty(org.getContext())
				|| StringUtil.isEmpty(org.getTitle())) {
			throw new NubeException(ErrorCodes.INVALID_INPUT,
					"orgId, context, title etc are mandatory");
		}

		// Location
		if (org.getLocation() != null) {
			//check for location id
			if (StringUtil.isEmpty(org.getLocation().getLocationId())) {
				org.getLocation().setLocationId("1");

			}
			org.getLocation().setLocationId(StringUtil.toKey(org.getLocation().getLocationId()));
			
			if (org.getLocation().getHours() != null) {
				for (Hours hrs : org.getLocation().getHours()) {
					if (hrs.getDay() == null) {
						throw new NubeException(ErrorCodes.INVALID_INPUT,
								"day is mandatory in hours");
					}
				}
			}
		}
	}

	/**
	 * update value
	 */
	public void update(Org org) throws NubeException {
		this.validate(org);
		Org existing = orgDao.readByContext(org.getContext());
		// record exists
		orgDao.update(org);
	}

	/**
	 * delete property
	 */
	public void delete(String context, int orgId) throws NubeException {
		orgDao.delete(context, orgId);
	}

	/**
	 * Read one org
	 */
	public Org readByContext(String context) throws NubeException {

		return orgDao.readByContext(context);

	}

	/**
	 * Read one org
	 */
	public Org readByOrgId(int orgId) throws NubeException {

		return orgDao.readByOrgId(orgId);
	}

}
