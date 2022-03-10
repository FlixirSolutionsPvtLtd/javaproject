package com.alinote.api.domains;


import com.alinote.api.constants.*;
import com.alinote.api.model.*;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;

import java.io.*;
import java.util.*;


@Setter
@Getter
@ToString
@NoArgsConstructor
@Document(collection = GlobalConstants.DocumentCollections.COLLECTION_USERS)
public class Users extends Auditable implements Serializable, UserDetails {

    @Id
    private String id;
    @Field("user_id")
    private String userId;
    private String name;
    private String email;
    private String password;
    @Field("google_password")
    private String googlePassword;
    @Transient
    private String finalPassword;

    @Field("mobile_no")
    private String mobileNo;

    @Field("mobile_verified")
    private boolean mobileVerified;

    @Field("terms_of_use_checked")
    private boolean termsOfUseChecked;

    @Field("privacy_policy_checked")
    private boolean privacyPolicyChecked;

    @Field("marketing_use_checked")
    private boolean marketingUseChecked;

    @Field("user_status")
    private String userStatus;

    @Field("profile_img")
    private String profileImg;

    public Users(GoogleHandleResponseDTO request) {
        this.name = request.getDisplayName();
        this.email = request.getEmail();
        this.password = request.getUserId();
        this.mobileVerified = this.termsOfUseChecked = this.privacyPolicyChecked = this.marketingUseChecked = true;
    }

    public String getPassword() {
        return null == finalPassword ? password : finalPassword;
    }

    public void setPassword(String password) {
        this.password = password;
        this.finalPassword = password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        //TODO: Add roles for a user
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_GEN_USER"));
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
