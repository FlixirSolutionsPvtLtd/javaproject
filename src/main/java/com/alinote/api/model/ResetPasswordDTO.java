package com.alinote.api.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {
    private String mobileNo;
    private String newPassword;
    private String email;
}
