package com.alinote.api.repository;

import com.alinote.api.domains.*;
import com.alinote.api.exception.*;

import java.util.*;

public interface UserCustomRepository {

    List<Users> findByMobileNoOrEmailAndActiveStatus(String username, boolean isEmail, int isActive) throws ServicesException;
}
