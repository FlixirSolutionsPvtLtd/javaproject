package com.alinote.api.converter;

import com.alinote.api.converter.core.*;
import com.alinote.api.domains.*;
import com.alinote.api.model.*;

public class UserInfoDTOConverter extends DTOConverter<Users, UserInfoDTO> {

    @Override
    public UserInfoDTO convert(Users input) {
        UserInfoDTO userInfoDTO = new UserInfoDTO(input);
        return userInfoDTO;
    }
}
