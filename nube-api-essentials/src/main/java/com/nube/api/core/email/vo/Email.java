package com.nube.api.core.email.vo;

public class Email {
	
	String from;
	
	String to;
	
	String subject;
	
	String content;
	
	String contentType="text/html";
	
	String sendDate;
	
	/**
	 * new - for new email
	 * success -sent success
	 * error - error to send
	 */
	String status="new";
	
	
	public Email(){
		
	}
	
	public Email(String to, String subject, String content){
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Email [to=" + to + ", subject=" + subject;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	
	

}
