package com.alinote.api.model;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OtpSmsResponse {

    private String message;
    private String type;
}
