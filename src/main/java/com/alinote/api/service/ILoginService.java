package com.alinote.api.service;

import com.alinote.api.exception.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.web.*;

import java.security.*;
import java.util.*;

public interface ILoginService {

    OAuth2AccessToken loginUser(Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException, ServicesException;

    OAuth2AccessToken getTokens(String email, String password) throws ServicesException;
}
