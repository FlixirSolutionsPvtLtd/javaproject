package com.alinote.api.service;

import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;

public interface IUsersService {
    BaseWrapper getUserInfo() throws ServicesException;

    BaseWrapper updateUserStatus(SingleValue<String> request) throws ServicesException;

    BaseWrapper changeUserPassword(ChangePasswordDTO request) throws ServicesException;

    BaseWrapper archieveUserAccount(SingleValue<Boolean> request) throws ServicesException;

    BaseWrapper verifyEmailBySendingOtp(SingleValue<String> request) throws ServicesException;

    BaseWrapper verifyCntcNoAndUpdate(OtpDTO request) throws ServicesException;

    BaseWrapper updateUserProfile(SingleValue<String> request) throws ServicesException;
}
