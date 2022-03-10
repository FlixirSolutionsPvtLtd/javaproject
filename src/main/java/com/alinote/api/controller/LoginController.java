package com.alinote.api.controller;

import com.alinote.api.constants.*;
import com.alinote.api.exception.*;
import com.alinote.api.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.web.*;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = RestUrlConstants.LOGIN)
public class LoginController {

    @Autowired
    private ILoginService loginService;


    @PostMapping
    public OAuth2AccessToken loginUser(
            Principal principal,
            @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException, ServicesException {
        return loginService.loginUser(principal, parameters);
    }
}
