package com.alinote.api.security.service;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.repository.*;
import com.alinote.api.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;

@Slf4j
@Service
@Qualifier("userService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {


    @Autowired
    private IUserRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            String[] userNameSplitString = CommonUtility.urlDecode(username).split(GlobalConstants.LoginConstants.USERNAME_DELIMITER, username.length());
            final String finalUserNm = CommonUtility.urlDecode(userNameSplitString[0]);
            System.out.println("username sent = " + username + " and finalUserNm = " + finalUserNm);

            List<Users> usersList = usersRepository.findByMobileNoOrEmailAndActiveStatus(finalUserNm, true, ActiveStatus.ACTIVE.value());

            if (!hasValue(usersList)) {
                log.debug("User is null");
                log.debug("User " + username + " not found");
                throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_USER_ID.value());
            }

            Users userDetails = usersList.get(0);

            //Set Google Handle password as password for Spring Security verification
        if (userNameSplitString.length > 1 && hasValue(userNameSplitString[1]) && userNameSplitString[1].equalsIgnoreCase(RegistrationSource.GOOGLE_HANDLE.name()))
            userDetails.setFinalPassword(userDetails.getGooglePassword());

            return userDetails;
        } catch (ServicesException e) {
            log.error("ServicesException while retreiving user", e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}