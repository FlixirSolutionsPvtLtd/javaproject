package com.alinote.api.security.service;

import com.alinote.api.domains.*;
import com.alinote.api.exception.*;
import com.alinote.api.repository.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import static com.alinote.api.utility.CheckUtil.hasValue;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class AuthenticationService implements IAuthenticationService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public Users fetchLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.debug("authentication = {}", authentication);
        if (hasValue(authentication))
            return (Users) authentication.getPrincipal();
        else
            return null;
    }

    @Override
    public Users findUserByEmail(String email) throws ServicesException {
        Users users = userRepository.findByEmail(email);
        if (hasValue(users))
            return users;

        throw new ServicesException("631");
    }
}
