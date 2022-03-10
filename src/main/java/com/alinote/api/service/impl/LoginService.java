package com.alinote.api.service.impl;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.repository.*;
import com.alinote.api.service.*;
import com.alinote.api.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.endpoint.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.*;
import org.springframework.web.client.*;
import org.springframework.web.servlet.support.*;

import java.security.*;
import java.text.*;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class LoginService implements ILoginService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenEndpoint tokenEndPoint;

    @Override
    public OAuth2AccessToken loginUser(Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException, ServicesException {
        String username = parameters.get(GlobalConstants.LoginConstants.USERNAME);
        System.out.println("username = " + username);
        System.out.println("password = " + parameters.get(GlobalConstants.LoginConstants.PASSWORD));
        System.out.println("grant type = " + parameters.get(GlobalConstants.LoginConstants.GRANT_TYPE));
        List<Users> existingUsersList = userRepository.findByMobileNoOrEmailAndActiveStatus(username, true, ActiveStatus.ACTIVE.value());

        //verify username
        log.info("does user exists = {}", CheckUtil.hasValue(existingUsersList));
        if (!CheckUtil.hasValue(existingUsersList))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_USERNAME.value());

        //TODO: Check for multiple records

        //verify active status
        Users user = existingUsersList.get(0);

        //verify password
        String decodedPwd = CommonUtility.urlDecode(parameters.get(GlobalConstants.LoginConstants.PASSWORD));
        System.out.println("parameter pwd: " + decodedPwd);
        System.out.println(", user pwd: " + user.getPassword());
        if (!passwordEncoder.matches(decodedPwd, user.getFinalPassword()))
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());

        //Generate Tokens and Send
        try {
            //Set final username and password in parameter map
            parameters.put(GlobalConstants.LoginConstants.USERNAME, username);
            parameters.put(GlobalConstants.LoginConstants.PASSWORD, decodedPwd);

            //Call token endpoint
            ResponseEntity<OAuth2AccessToken> response = tokenEndPoint.postAccessToken(principal, parameters);
            if (response.getStatusCode() == HttpStatus.OK)
                return response.getBody();
            else
                throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        } catch (InvalidGrantException e) {
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        } catch (Exception e) {
            log.error("Error getting access token = {}", e);
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());
        }
    }

    @Override
    public OAuth2AccessToken getTokens(String email, String password) throws ServicesException {
        // Define Login URL
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        log.info("baseUrl String: " + baseUrl);

        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append(RestUrlConstants.LOGIN).append(GlobalConstants.LoginConstants.QUERY_PARAMS_LOGIN);
        String logInUrl = MessageFormat.format(sb.toString(), CommonUtility.urlEncode(email), CommonUtility.urlEncode(password), GlobalConstants.LoginConstants.PASSWORD);
        log.info("logInUrl = " + logInUrl);

        // Define Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(GlobalConstants.LoginConstants.HEADER_AUTHORIZATION, GlobalConstants.LoginConstants.BASIC_AUTH_TOKEN);

        // Set Headers In Request Entity
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);

        // Send Request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(logInUrl, HttpMethod.POST, requestEntity,
                OAuth2AccessToken.class);

        // Check response
        if (response.getStatusCode() == HttpStatus.OK) {
            log.debug("Logged In Success. OAuthResponse = " + response.getBody());
            return response.getBody();
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            log.debug("Unauthorized User Error");
        } else {
            log.debug(
                    "Error Code = " + response.getStatusCode() + ", Error Message = " + response.getStatusCodeValue());
        }

        throw new ServicesException("Invalid username or password supplied");
    }
}
