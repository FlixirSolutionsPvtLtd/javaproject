package com.alinote.api.controller;

import com.alinote.api.constants.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = RestUrlConstants.USERS)
public class UsersController {

    @Autowired
    private IUsersService usersService;


    @GetMapping
    @ApiOperation(value = "Api to get logged in user info")
    public BaseWrapper getUserInfo() throws ServicesException {
        return usersService.getUserInfo();
    }

    @PutMapping(value = RestUrlConstants.STATUS_UPDATE)
    @ApiOperation(value = "Api to update status message of logged in user")
    public BaseWrapper updateUserStatus(@RequestBody SingleValue<String> request) throws ServicesException {
        return usersService.updateUserStatus(request);
    }

    @PutMapping(value = RestUrlConstants.CHANGE_PASSWORD)
    @ApiOperation(value = "Api to reset password")
    public BaseWrapper changeUserPassword(@RequestBody ChangePasswordDTO request) throws ServicesException {
        return usersService.changeUserPassword(request);
    }

    @PutMapping(value = RestUrlConstants.ARCHIEVE_ACCOUNT)
    @ApiOperation(value = "Api to reset password")
    public BaseWrapper archieveUserAccount(@RequestBody SingleValue<Boolean> request) throws ServicesException {
        return usersService.archieveUserAccount(request);
    }

    @PostMapping(value = RestUrlConstants.VERIFY_EMAIL)
    @ApiOperation(value = "Api to send OTP to registered email")
    public BaseWrapper verifyEmailBySendingOtp(
            @ApiParam(value = "Device ID of the device requesting Email Verification") @RequestBody SingleValue<String> request) throws ServicesException {
        return usersService.verifyEmailBySendingOtp(request);
    }

    @PostMapping(value = RestUrlConstants.CHANGE_CNTC_NO)
    @ApiOperation(value = "Api to verify OTP sent to new contact no. and update logged in user contact no.")
    public BaseWrapper verifyCntcNoAndUpdate(@RequestBody OtpDTO request) throws ServicesException {
        return usersService.verifyCntcNoAndUpdate(request);
    }

    @PutMapping(value = RestUrlConstants.USER_PRFL_IMG)
    @ApiOperation(value = "Api to update user profile image of logged in user")
    public BaseWrapper updateUserProfile(@RequestBody SingleValue<String> request) throws ServicesException {
        return usersService.updateUserProfile(request);
    }
}
