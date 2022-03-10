package com.alinote.api.model.common;

public class ResponseMessage {

	private String status;

	private String message;

	private String trace;

	public ResponseMessage() {
	}

	public ResponseMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public ResponseMessage(String status, String message, String trace) {
		this.status = status;
		this.message = message;
		this.trace = trace;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	@Override
	public String toString() {
		return "ResponseMessage [status=" + status + ", message=" + message + ", trace=" + trace + "]";
	}

}
