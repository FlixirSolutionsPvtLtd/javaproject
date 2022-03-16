package com.alinote.api.controller;

import com.alinote.api.constants.*;
import com.alinote.api.crons.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.service.*;
import com.alinote.api.utility.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = {RestUrlConstants.PUBLIC})
public class PublicController {

    @Autowired
    private PublicService publicService;
    @Autowired
    private ILoginService loginService;
    @Autowired
    private IRegistrationService registrationService;

    @GetMapping(value = {RestUrlConstants.PING})
    public String ping() {
        log.info("Pinged Successfully");
        return "Ping successful!!-deployed. Working. Done";
    }

    @ApiOperation(value = "Api to register User")
    @PostMapping(value = {RestUrlConstants.REGISTER})
    public OAuth2AccessToken registerUser(@RequestBody Users users) throws ServicesException {
        final String TAG_METHOD_NAME = "registerUser";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "Registration request parameters = " + users.toString());

        final String orgPwd = users.getPassword();
        registrationService.doRegisterUser(users);

        //Get Tokens and return
        final String finalUsrNm = CommonUtility.getFinalUserNmByRegistrationSource(users.getEmail(), RegistrationSource.REG_FORM);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "finalUsrNm for login tokens = " + finalUsrNm);
        OAuth2AccessToken response = loginService.getTokens(finalUsrNm, orgPwd);

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return response;
    }

    @ApiOperation(value = "Api to register a customer using google social handle")
    @PostMapping(value = {
            RestUrlConstants.AN_SOCIAL_HANDLES_GOOGLE_HANDLE
    })
    public OAuth2AccessToken registerCustomerViaGoogleHandle(
            @RequestBody GoogleHandleResponseDTO request) throws ServicesException {
        final String TAG_METHOD_NAME = "registerCustomerViaGoogleHandle";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "Google Handle Registration request parameters = " + request.toString());
        registrationService.registerCustomerViaGoogleHandle(request);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "User registered via Google Handle");

        //Get Tokens and return
        final String finalUsrNm = CommonUtility.getFinalUserNmByRegistrationSource(request.getEmail(), RegistrationSource.GOOGLE_HANDLE);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "finalUsrNm for login tokens = " + finalUsrNm);
        OAuth2AccessToken response = loginService.getTokens(finalUsrNm, request.getUserId());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return response;
    }

    @ApiOperation(value = "Api to send OTP")
    @PostMapping(value = {
            RestUrlConstants.AN_SEND_OTP
    })
    public BaseWrapper sendOtp(
            @ApiParam(value = "value will be either emailID or contactNo") @RequestBody OtpDTO request,
            @RequestParam(GlobalConstants.OTP_TARGET) OtpTarget target,
            @RequestParam Source source) throws ServicesException {

        return publicService.sendOtp(request, target, source);
    }

    @PostMapping(value = {
            RestUrlConstants.AN_VERIFY_OTP
    })
    public BaseWrapper verifyOtp(
            @RequestBody OtpDTO request,
            @RequestParam(GlobalConstants.OTP_TARGET) String target,
            @RequestParam Source source
    ) throws ServicesException {

        return publicService.verifyOtp(request, target, source);
    }

    @ApiOperation(value = "Api to set password")
    @PostMapping(value = {
            RestUrlConstants.AN_RESET_PASSWORD
    })
    public BaseWrapper resetPassword(
            @RequestBody ResetPasswordDTO request) throws ServicesException {

        return publicService.resetPassword(request);
    }

    @GetMapping(value = RestUrlConstants.STATIC_CONTENT)
    @ApiOperation(value = "Get static content for the App")
    public BaseWrapper getStaticContent(
            @RequestParam(value = "source") StaticContentSource source
    ) throws ServicesException {
        return publicService.getStaticContent(source);
    }

    @Autowired
    private AutoCleanStaleRecordsCron autoCleanStaleRecordsCron;

    @GetMapping("/testcron")
    public String testCron() {
        autoCleanStaleRecordsCron.archieveDeletedNotes();
        return "DONE";
    }
}
