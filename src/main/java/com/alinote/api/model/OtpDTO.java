package com.alinote.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class OtpDTO {

    private String otp;
    private String deviceId;
    private String mobileNo;
    private String emailId;
    private Map<String, String> additionalProperties = new HashMap<>();
}
