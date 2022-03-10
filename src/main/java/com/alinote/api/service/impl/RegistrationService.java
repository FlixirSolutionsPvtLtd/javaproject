package com.alinote.api.service.impl;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.helpers.*;
import com.alinote.api.model.*;
import com.alinote.api.repository.*;
import com.alinote.api.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;


@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class RegistrationService implements IRegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private RegistrationHelper registrationHelper;

    @Override
    public void doRegisterUser(Users request) throws ServicesException {
        final String TAG_METHOD_NAME = "doRegisterUser";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());

        if (registrationHelper.isValidRequest(request)) {
            registerUserBySource(request, RegistrationSource.REG_FORM);
        } else
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
    }

    @Override
    public void registerCustomerViaGoogleHandle(GoogleHandleResponseDTO request) throws ServicesException {
        final String TAG_METHOD_NAME = "registerCustomerViaGoogleHandle";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), request.toString());

        if (registrationHelper.isValidGoogleHandleRequest(request)) {
            Users requestUser = new Users(request);
            registerUserBySource(requestUser, RegistrationSource.GOOGLE_HANDLE);
        } else
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
    }


    /**
     * Helper method to save/update the user details based on the registration source
     *
     * @param request
     * @param registrationSource
     * @throws ServicesException
     */
    public void registerUserBySource(Users request, RegistrationSource registrationSource) throws ServicesException {
        if (!request.isMobileVerified())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_FOR_REGISTRATION.value());

        if (!request.isTermsOfUseChecked() || !request.isPrivacyPolicyChecked())
            throw new ServicesException(GeneralErrorCodes.ERR_UNCHECKED_AGREEMENT_POLICY.value());

        //Fetch any existing user by email ID
        Users existingUser = userRepository.findByEmail(request.getEmail());

        //Set whether new user has to be created or existing user if any has to be updated
        boolean isExistingUser = hasValue(existingUser) ? true : false;

        //Validate if user is already registered via traditional registration process
        if (isExistingUser
                && registrationSource == RegistrationSource.REG_FORM
                && hasValue(existingUser.getPassword()))
            throw new ServicesException(GeneralErrorCodes.ERR_USER_ALREADY_REGISTERED.value());

        if (!isExistingUser) {
            existingUser = new Users();
            existingUser.setUserId(UUID.randomUUID().toString());
        }

        //Set other required variables values
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        if (registrationSource == RegistrationSource.REG_FORM) {
            existingUser.setPassword(encodedPassword);
            existingUser.setMobileNo(request.getMobileNo());
        } else if (registrationSource == RegistrationSource.GOOGLE_HANDLE)
            existingUser.setGooglePassword(encodedPassword);

        existingUser.setMobileVerified(request.isMobileVerified());
        existingUser.setTermsOfUseChecked(request.isTermsOfUseChecked());
        existingUser.setPrivacyPolicyChecked(request.isPrivacyPolicyChecked());
        existingUser.setMarketingUseChecked(request.isMarketingUseChecked());
        existingUser.updateAuditableFields(!isExistingUser, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

        //Save User Details
        System.out.println("Saving user Details as: " + existingUser.toString());
        userRepository.save(existingUser);
    }
}
