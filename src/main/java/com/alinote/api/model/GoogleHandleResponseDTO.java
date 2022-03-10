package com.alinote.api.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class GoogleHandleResponseDTO implements Serializable {

    public String accessToken;
    public String displayName;
    public String email;
    public Integer expires;
    public Integer expiresIn;
    public String familyName;
    public String givenName;
    public String userId;
    private final static long serialVersionUID = 7769292747497416671L;
}