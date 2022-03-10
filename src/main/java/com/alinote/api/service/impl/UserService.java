package com.alinote.api.service.impl;

import com.alinote.api.constants.*;
import com.alinote.api.converter.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.repository.*;
import com.alinote.api.security.service.*;
import com.alinote.api.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import static com.alinote.api.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserService implements IUsersService {

    @Autowired
    private IAuthenticationService authenticationService;
    @Autowired
    private ILoginService loginService;
    @Autowired
    private PublicService publicService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseWrapper getUserInfo() throws ServicesException {
        final String TAG_METHOD_NAME = "getUserInfo()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        //Fetch user info based on logged in user
        Users userInfoFromToken = authenticationService.fetchLoggedInUser();
        if (!hasValue(userInfoFromToken))
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_FOUND.value());
        Users userInfo = userRepository.findById(userInfoFromToken.getId()).get();

        //Convert to DTO
        UserInfoDTOConverter converter = new UserInfoDTOConverter();
        UserInfoDTO userInfoDTO = converter.convert(userInfo);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), userInfoDTO.toString());

        //Send response
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(userInfoDTO);
    }

    @Override
    public BaseWrapper updateUserStatus(SingleValue<String> request) throws ServicesException {
        final String TAG_METHOD_NAME = "updateUserStatus()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());
        String statusToUpdate = hasValue(request.getTerm()) ? request.getTerm() : "";

        //Get Loged in user and update
        Users users = userRepository.findById(authenticationService.fetchLoggedInUser().getId()).get();
        users.setUserStatus(statusToUpdate);
        userRepository.save(users);

        //Send response
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper changeUserPassword(ChangePasswordDTO request) throws ServicesException {
        final String TAG_METHOD_NAME = "changeUserPassword()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());
        //Validate request
        String newPwd = request.getNewPwd();
        if (!hasValue(request) || !hasValue(request.getCurrentPwd()) || !hasValue(newPwd))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Fetch logged in user
        Users users = authenticationService.fetchLoggedInUser();

        //compare the supplied password with logged in user password
        if (!passwordEncoder.matches(request.getCurrentPwd(), users.getPassword()))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());

        //Encode new password and update users pwd
        String encodedNewPwd = passwordEncoder.encode(newPwd);
        users.setPassword(encodedNewPwd);
        userRepository.save(users);

        //Get new accesstokens and send in response
        OAuth2AccessToken oAuth2AccessToken = loginService.getTokens(users.getEmail(), newPwd);
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(oAuth2AccessToken);
    }

    @Override
    public BaseWrapper archieveUserAccount(SingleValue<Boolean> request) throws ServicesException {
        final String TAG_METHOD_NAME = "archieveUserAccount()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());
        //Validate request
        Boolean archieveAccount = request.getTerm();
        if (!hasValue(request) || !hasValue(archieveAccount))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Get Loged in user and update user account status
        Users users = userRepository.findById(authenticationService.fetchLoggedInUser().getId()).get();
        int activeStatusToUdate = archieveAccount ? ActiveStatus.ARCHIEVE.value() : ActiveStatus.ACTIVE.value();
        users.setActiveStatus(activeStatusToUdate);
        userRepository.save(users);

        //TODO: Invalidate Token

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper verifyEmailBySendingOtp(SingleValue<String> request) throws ServicesException {
        if (!hasValue(request) || !hasValue(request.getTerm()))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Fetch Logged In User
        Users user = authenticationService.fetchLoggedInUser();

        //Prepare OtpDTO
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setDeviceId(request.getTerm());
        otpDTO.setEmailId(user.getEmail());

        //Send OTP
        return publicService.sendOtp(otpDTO, OtpTarget.EMAIL, Source.PRFL_CHNG_NO);
    }

    @Override
    public BaseWrapper verifyCntcNoAndUpdate(OtpDTO request) throws ServicesException {
        if (!hasValue(request)
                || !hasValue(request.getDeviceId())
                || !hasValue(request.getMobileNo())
                || !hasValue(request.getOtp()))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Verify OTP
        publicService.verifyOtp(request, OtpTarget.CONTACT.name(), Source.PRFL_CHNG_NO);

        //Update User Phone No
        Users user = userRepository.findById(authenticationService.fetchLoggedInUser().getId()).get();
        user.setMobileNo(request.getMobileNo());
        user.setMobileVerified(true);
        userRepository.save(user);

        return new BaseWrapper("Contact No. successfully updated");
    }

    @Override
    public BaseWrapper updateUserProfile(SingleValue<String> request) throws ServicesException {
        final String TAG_METHOD_NAME = "updateUserProfile()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());

        //Get Loged in user and update
        Users users = userRepository.findById(authenticationService.fetchLoggedInUser().getId()).get();
        users.setProfileImg(request.getTerm());
        userRepository.save(users);

        //Send response
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(request);
    }
}
