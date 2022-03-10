package com.alinote.api.service;

import com.alinote.api.exception.ServicesException;

public interface MessagingService {

    boolean sendMessage(String contactNumber, Object message) throws Exception;

    boolean sendOTP(String otp, String mobileNo) throws ServicesException;
}
