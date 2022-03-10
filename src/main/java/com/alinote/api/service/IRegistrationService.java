package com.alinote.api.service;

import com.alinote.api.domains.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;

public interface IRegistrationService {
    void doRegisterUser(Users request) throws ServicesException;

    void registerCustomerViaGoogleHandle(GoogleHandleResponseDTO request) throws ServicesException;
}
