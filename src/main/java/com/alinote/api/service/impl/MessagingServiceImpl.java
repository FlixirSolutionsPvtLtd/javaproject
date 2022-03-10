package com.alinote.api.service.impl;

import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.service.*;
import com.alinote.api.utility.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.json.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.text.*;

@Service
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private Environment env;


    @Override
    public boolean sendMessage(String contactNumber, Object message) throws Exception {
        return false;
    }

    @Override
    public boolean sendOTP(String otp, String mobileNo) throws ServicesException {

        if (!CheckUtil.hasValue(mobileNo)
                || !CheckUtil.hasValue(otp))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        boolean isOtpSent = false;

        //Send SMS with country code for Other than India or add 91 for indiain nos (10 digit)
        mobileNo = mobileNo.length() > 10 ? mobileNo : ("91" + mobileNo);
        log.info("Recieved mobileNo = {} and otp = {}", mobileNo, otp);

        try {
            String otpApiEndpoint = MessageFormat
                    .format(
                            env.getProperty("otp.url"),
                            otp,
                            mobileNo);

            log.info("Formed otpApiEndpoint = {}", otpApiEndpoint);

            String otpApiResponse = executeRESTCallByPost(otpApiEndpoint, null);
            log.info("otpApiResponse = {}", otpApiResponse);

            JSONObject otpApiResponseJson = new JSONObject(otpApiResponse);
            OtpSmsResponse otpSmsResponse = new OtpSmsResponse();
            otpSmsResponse.setMessage(otpApiResponseJson.getString("message"));
            otpSmsResponse.setType(otpApiResponseJson.getString("type"));
            log.info("otpSmsResponse = {}", otpSmsResponse.toString());

            if (otpSmsResponse.getType().equalsIgnoreCase("success"))
                isOtpSent = true;
        } catch (Exception e) {
            log.error("Error Occured sending OTP", e);
        }

        return isOtpSent;
    }

    private String executeRESTCallByPost(String urlToHit, Object valueToWrite) throws Exception {

        String response = "";
        ObjectMapper mapper = new ObjectMapper();
        HttpPost post = null;

        try {
            post = new HttpPost(urlToHit);

            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            if (valueToWrite != null) {
                post.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

                StringEntity entity = new StringEntity(mapper.writeValueAsString(valueToWrite));
                post.setEntity(entity);
            }

            HttpClient http = HttpClientBuilder.create().build();
            InputStream stream = http.execute(post).getEntity().getContent();

            int c;
            while ((c = stream.read()) != -1)
                response += ((char) c);
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != post)
                post.releaseConnection();
        }

        return response;
    }
}
