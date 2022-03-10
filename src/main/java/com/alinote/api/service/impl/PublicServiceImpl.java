package com.alinote.api.service.impl;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.repository.*;
import com.alinote.api.service.*;
import com.alinote.api.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

import static com.alinote.api.utility.CheckUtil.*;

@Slf4j
@Service
public class PublicServiceImpl implements PublicService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserOtpRepository userOtpRepository;
    @Autowired
    private IAppContentRepository appContentRepository;

    @Autowired
    private MessagingService messagingService;
    @Autowired
    private IEmailService emailService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("${otp.activate}")
    private boolean otpActive;

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${email.changeno.verification.title}")
    private String changeNoVerificationTitle;

    @Autowired
    private Environment environment;

    @Override
    public BaseWrapper sendOtp(OtpDTO otpDTO, OtpTarget target, Source source) throws ServicesException {
        String cnctInfo = target == OtpTarget.CONTACT ? otpDTO.getMobileNo() : otpDTO.getEmailId();
        sendOtpBySmsOrEmailID(cnctInfo, otpDTO.getDeviceId(), target);
        return new BaseWrapper("OTP sent to your " + target.value());
    }

    @Override
    public BaseWrapper verifyOtp(OtpDTO request, String target, Source source) throws ServicesException {
        String cnctcInfo = target.equals(OtpTarget.CONTACT.name()) ? request.getMobileNo() : request.getEmailId();
        if (!hasValue(cnctcInfo)
                || !hasValue(request.getOtp()))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());


        List<Users> users = null;
        if (target.equals(OtpTarget.CONTACT.name())
                && (source == Source.FIND_ID
                || source == Source.FIND_PASS))
            users = fetchUserIfExists(cnctcInfo);

        validateOtpForContactNo(cnctcInfo, request.getOtp(), request.getDeviceId());

        Users user = null;
        if (target.equals(OtpTarget.CONTACT.name()) && source != Source.REG && source != Source.PRFL_CHNG_NO) {
            user = users.get(0);
            user.setMobileVerified(true);
        }

        if (source == Source.FIND_ID)
            return new BaseWrapper(users != null ? users.stream().map(Users::getEmail).collect(Collectors.toList()) : null, new ResponseMessage(HttpStatus.OK.toString(), "OK"));

        return new BaseWrapper("OTP verified successfully");
    }

    private List<Users> fetchUserIfExists(String mobileNo) throws ServicesException {

        List<Users> users = userRepository.findByMobileNoAndActiveStatus(
                mobileNo,
                ActiveStatus.ACTIVE.value());


        if (!hasValue(users))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        return users;
    }

    private Map<String, Object> getDummyLoginResponse() {
        Map<String, Object> dummyMapResponse = new HashMap<>();
        dummyMapResponse.put("access_token", "8bafc615-132e-49dc-896c-5efa5d901eb8");
        dummyMapResponse.put("token_type", "bearer");
        dummyMapResponse.put("refresh_token", "43197a3c-0afc-4f34-b62d-d7a174ca7cdb");
        dummyMapResponse.put("expires_in", 99999);
        dummyMapResponse.put("scope", "read write");

        return dummyMapResponse;
    }

    @Override
    public BaseWrapper resetPassword(ResetPasswordDTO request) throws ServicesException {

        if (!hasValue(request.getMobileNo())
                || !hasValue(request.getNewPassword())
                || !hasValue(request.getEmail())
        )
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        List<Users> users = fetchUserIfExists(request.getMobileNo(), request.getEmail());
        if (!hasValue(users))
            throw new ServicesException("610");

        if (users.size() > 1)
            throw new ServicesException("624");

        Users user = users.get(0);
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword().trim());
        log.debug("Updating encodedPassword = {} for user ID = {}", encodedNewPassword, user.getUserId());
        user.setPassword(encodedNewPassword);
        user.setActiveStatus(ActiveStatus.ACTIVE.value());
        userRepository.save(user);
        return new BaseWrapper("Password updated successfully");
    }

    @Override
    public BaseWrapper getStaticContent(StaticContentSource source) throws ServicesException {
        final String TAG_METHOD_NAME = "getStaticContent";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), source);

        AppContent content;
        switch (source) {
            case REG_TOU:
                content = appContentRepository.findAppContentByAppContentId(StaticContentSource.REG_TOU.name());
                break;

            case REG_PP:
                content = appContentRepository.findAppContentByAppContentId(StaticContentSource.REG_PP.name());
                break;

            default:
                throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), content);
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return new BaseWrapper(content.getAppContent());
    }

    /*
        Helper Methods
         */
    private List<Users> fetchUserIfExists(String mobileNo, String email) throws ServicesException {

        List<Users> users = userRepository.findByMobileNoAndEmailAndActiveStatus(mobileNo, email, ActiveStatus.ACTIVE.value());

        if (!hasValue(users))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        return users;
    }

    private List<Users> fetchUserByEmailIfExists(String email) throws ServicesException {

        List<Users> users = userRepository.findByEmailAndActiveStatus(email, ActiveStatus.ACTIVE.value());

        if (!hasValue(users))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        return users;
    }


    private void validateOtpForContactNo(String mobileNo, String otp, String deviceId) throws ServicesException {

        log.info(GlobalConstants.LOG.INFO, "validateOtpForContactNo", getClass().getName(), "mobile no = " + mobileNo + ", deviceId = " + deviceId);
        UserOtp userOtp = userOtpRepository.findByContactNoAndDeviceId(mobileNo, deviceId);

        if (!hasValue(userOtp))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        if (otp.equals(userOtp.getOtp())) {
            userOtpRepository.delete(userOtp);
        } else
            throw new ServicesException("622");
    }

    private void sendOtpBySmsOrEmailID(String cntcInfo, String deviceId, OtpTarget target) throws ServicesException {
        final String TAG_METHOD_NAME = "sendOtpBySmsOrEmailID";

        //Validate Contact No. if target is CONTACT
        if (target == OtpTarget.CONTACT && !CheckUtil.isValidPhoneNumber(cntcInfo))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        //Validate Email ID if target is EMAIL
        if (target == OtpTarget.EMAIL && !CheckUtil.isValidEmail(cntcInfo))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        Query query = new Query();
        query.addCriteria(Criteria.where("contact_no").is(cntcInfo));
        query.addCriteria(Criteria.where("device_id").is(deviceId));

        UserOtp userOtp = mongoTemplate.findOne(query, UserOtp.class);

        boolean isCreate = true;
        if (hasValue(userOtp)) {
            if (userOtp.getAttempts() >= GlobalConstants.OTP_ALLOWED_MAX_ATTEMPTS)
                throw new ServicesException(
                        GeneralErrorCodes.ERR_MAX_OTP_ATTEMPTS_EXEMPTED.value());

            isCreate = false;
        } else {
            userOtp = new UserOtp();
            userOtp.setContactNo(cntcInfo);
            userOtp.setDeviceId(deviceId);
        }

        userOtp.updateAuditableFields(
                isCreate,
                GlobalConstants.USER_API,
                ActiveStatus.ACTIVE.value());

        // Update OTP Attempts
        int attempts = userOtp.getAttempts();
        userOtp.setAttempts(attempts + 1);

        String otp = null;
        if (otpActive)
            otp = hasValue(userOtp.getOtp()) ? userOtp.getOtp() : commonUtility.generateRandomOtp(GlobalConstants.OTP_LENGTH);
        else
            otp = "1111";
        userOtp.setOtp(otp);

        //Save OTP data against contactNo
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), userOtp.toString());
        userOtpRepository.save(userOtp);

        if (otpActive) {
            //Send OTP
            switch (target) {

                case EMAIL:
                    if (!emailService.sendEmail(cntcInfo, changeNoVerificationTitle, otp))
                        throw new ServicesException(GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
                    break;

                case CONTACT:
                default:
                    if (!messagingService.sendOTP(otp, cntcInfo))
                        throw new ServicesException(GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
                    break;
            }
        }

    }

}