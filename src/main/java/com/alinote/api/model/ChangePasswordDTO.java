package com.alinote.api.model;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ChangePasswordDTO implements Serializable {
    private static final long serialVersionUID = 4638827406418587788L;

    private String currentPwd;
    private String newPwd;
}
