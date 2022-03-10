package com.alinote.api.helpers;

import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.*;

import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;

@Component
public class RegistrationHelper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean isValidRequest(Users request) {
        return hasValue(request.getName())
                && hasValue(request.getMobileNo())
                && isValidPhoneNumber(request.getMobileNo())
                && hasValue(request.getEmail())
                && isValidEmail(request.getEmail())
                && hasValue(request.getPassword())
                && hasValue(request.isMobileVerified())
                && hasValue(request.isPrivacyPolicyChecked())
                && hasValue(request.isTermsOfUseChecked())
                && hasValue(request.isMarketingUseChecked());
    }

    public boolean isValidGoogleHandleRequest(GoogleHandleResponseDTO request) {
        return hasValue(request.getDisplayName())
                && hasValue(request.getEmail())
                && isValidEmail(request.getEmail())
                && hasValue(request.getUserId());
    }


    public void validateIfEmailIdAlreadyExists(String email) throws ServicesException {
        Query queryEmail = new Query();
        queryEmail.addCriteria(Criteria.where("email").is(email));
        queryEmail.addCriteria(Criteria.where("is_active").is(ActiveStatus.ACTIVE.value()));
        List<Users> existingUsersList = mongoTemplate.find(queryEmail, Users.class);
        if (hasValue(existingUsersList) && existingUsersList.size() > 0) {
            //Multiple users already exists with same contactNo/email and same Role
            throw new ServicesException(GeneralErrorCodes.ERR_USER_ALREADY_REGISTERED.value());
        }
    }

    public void validateIfMobileNoAlreadyExists(String mobileNo) throws ServicesException {
        Query queryMobNo = new Query();
        queryMobNo.addCriteria(Criteria.where("mobile_no").is(mobileNo));
        queryMobNo.addCriteria(Criteria.where("is_active").is(ActiveStatus.ACTIVE.value()));
        List<Users> existingUsersList = mongoTemplate.find(queryMobNo, Users.class);
        if (hasValue(existingUsersList) && existingUsersList.size() > 0) {
            //Multiple users already exists with same contactNo/email and same Role
            throw new ServicesException(GeneralErrorCodes.ERR_USER_ALREADY_REGISTERED.value());
        }
    }
}
