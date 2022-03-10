package com.alinote.api.repository.impl;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.repository.*;
import com.alinote.api.utility.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static com.alinote.api.utility.CheckUtil.hasValue;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Users> findByMobileNoOrEmailAndActiveStatus(String username, boolean isEmail, int isActive) throws ServicesException {
        String[] userNameSplitString = CommonUtility.urlDecode(username).split(GlobalConstants.LoginConstants.USERNAME_DELIMITER, username.length());
        final String finalUserNm = userNameSplitString[0];
        System.out.println("finalUserNm = " + finalUserNm);

        Query query = new Query();
        query.addCriteria(Criteria.where(isEmail ? "email" : "mobile_no").is(finalUserNm));
        query.addCriteria(Criteria.where("is_active").is(isActive));

        List<Users> users = mongoTemplate.find(query, Users.class);

        if (!hasValue(users))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        String regSource = userNameSplitString.length > 1 && hasValue(userNameSplitString[1]) ? userNameSplitString[1] : "";
        if (regSource.equals(RegistrationSource.GOOGLE_HANDLE.name()))
            users.get(0).setFinalPassword(users.get(0).getGooglePassword());
        else if (regSource.equals(RegistrationSource.REG_FORM.name()))
            users.get(0).setFinalPassword(users.get(0).getPassword());
        else
            users.get(0).setFinalPassword(users.get(0).getPassword());

        return users;
    }
}
