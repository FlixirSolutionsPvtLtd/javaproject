package com.alinote.api.service.impl;

import com.alinote.api.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.mail.*;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
public class EmailService implements IEmailService {


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    @Override
    public boolean sendEmail(String to, String subjectLine, String emailBody) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(env.getProperty("spring.mail.username"));
            mailMessage.setTo(to);
            mailMessage.setSubject(subjectLine);
            mailMessage.setText(emailBody);

            javaMailSender.send(mailMessage);

            return true;
        } catch (Exception e) {
            log.error("error sending email due to ==> ", e);
            return false;
        }
    }
}
