package com.nube.api.core.idm.request;

/**
 * Used for both reset and change password
 * @author kamoorr
 *
 */
public class PasswordRequest {
	
	String currentPassword;
	String resetPasswordKey;
	String newPassword;
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getResetPasswordKey() {
		return resetPasswordKey;
	}
	public void setResetPasswordKey(String resetPasswordKey) {
		this.resetPasswordKey = resetPasswordKey;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	

}
