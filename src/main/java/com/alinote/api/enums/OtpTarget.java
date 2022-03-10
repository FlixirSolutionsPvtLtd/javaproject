package com.alinote.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OtpTarget {

	EMAIL("emailID"), 
	CONTACT("contactNo");
	
	private String value;
	
	OtpTarget(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
	
	public static Optional<OtpTarget> getValueOf(String value) {
		
		return Arrays.stream(values())
                .filter(gec -> gec.value().equals(value))
                .findFirst();
	}
}
