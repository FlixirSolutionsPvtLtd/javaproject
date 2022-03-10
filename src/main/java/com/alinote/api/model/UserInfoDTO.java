package com.alinote.api.model;

import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UserInfoDTO implements Serializable {
    private static final long serialVersionUID = 2996778989023376820L;

    private String id;
    private String userId;
    private String profileImg;
    private String userStatus;
    private String email;
    private String name;
    private String mobileNo;
    private boolean mobileVerified;
    private boolean termsOfUseChecked;
    private boolean privacyPolicyChecked;
    private boolean marketingUseChecked;
    private String activeStatus;

    public UserInfoDTO(Users input) {
        this.id = input.getId();
        this.userId = input.getUserId();
        this.profileImg = input.getProfileImg();
        this.userStatus = input.getUserStatus();
        this.email = input.getEmail();
        this.name = input.getName();
        this.mobileNo = input.getMobileNo();
        this.mobileVerified = input.isMobileVerified();
        this.termsOfUseChecked = input.isTermsOfUseChecked();
        this.privacyPolicyChecked = input.isPrivacyPolicyChecked();
        this.marketingUseChecked = input.isMarketingUseChecked();
        this.activeStatus = ActiveStatus.valueOf(input.getActiveStatus()).get().name();
    }
}
