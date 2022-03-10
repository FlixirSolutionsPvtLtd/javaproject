package com.alinote.api.service;

public interface IEmailService {
    boolean sendEmail(String to, String subjectLine, String emailBody);
}
