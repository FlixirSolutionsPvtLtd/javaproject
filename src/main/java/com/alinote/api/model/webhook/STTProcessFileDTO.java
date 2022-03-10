package com.alinote.api.model.webhook;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class STTProcessFileDTO implements Serializable {

    private static final long serialVersionUID = 4197731549301193769L;

    private String uuId;
    private String fileName;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String returnUrl;
}
