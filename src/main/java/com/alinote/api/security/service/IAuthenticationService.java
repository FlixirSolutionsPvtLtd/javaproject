package com.alinote.api.security.service;

import com.alinote.api.domains.*;
import com.alinote.api.exception.*;

public interface IAuthenticationService {
    Users fetchLoggedInUser();

    public Users findUserByEmail(String email) throws ServicesException;
}
