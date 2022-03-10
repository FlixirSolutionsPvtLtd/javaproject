package com.alinote.api.utility;

import com.alinote.api.constants.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;

@Component
public class CommonUtility {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Creates the final username to pass to the login API from the user email and source of registration
     *
     * @param email
     * @param registrationSource
     * @return
     */
    public static String getFinalUserNmByRegistrationSource(String email, RegistrationSource registrationSource) {
        return email + GlobalConstants.LoginConstants.USERNAME_DELIMITER + registrationSource.name();
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value);
    }

    /**
     * Decode the encoded term
     *
     * @param value Value to decode
     * @return
     */
    public static String urlDecode(String value) {
        return URLDecoder.decode(value);
    }

    /**
     * Common method to validate any type of input's
     *
     * @param inputs
     * @return
     * @throws ServicesException
     */
    public static void validateInput(Object... inputs) throws ServicesException {
        for (Object input : inputs)
            if (!hasValue(input))
                throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
    }


    /**
     * Description Method for to generate alpha numeric Password
     *
     * @return
     */
    public String getAlphaNumericString() {
        int count = 10;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    @Autowired
    private ResourceLoader resourceLoader;

    public <T> T readJsonFile(String fileName, Class<T> typeValue) throws JsonParseException, JsonMappingException, IOException {

        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        final File file = resource.getFile();

        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, typeValue);
    }

    public String generateRandomOtp(int otpLength) {

        return String.format(
                "%04d",
                new Random()
                        .nextInt(10000));
    }

    public static long getValue(Long longValue) {
        return hasValue(longValue) && longValue > 0 ? longValue : 0;
    }

    public static String getStringValueIfPresent(Map<String, Object> rawPayload, String key) {
        return hasValue(rawPayload.get(key)) ? rawPayload.get(key).toString() : null;
    }

    public static Float getFloatValueIfPresent(Map<String, Object> rawPayload, String key) {
        return hasValue(rawPayload.get(key)) ? Float.parseFloat(rawPayload.get(key).toString()) : null;
    }
}
