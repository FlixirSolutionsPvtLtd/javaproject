package com.alinote.api.model.webhook;

import lombok.*;
import lombok.extern.slf4j.*;

import java.io.*;
import java.util.*;

import static com.alinote.api.utility.CommonUtility.*;

@Slf4j
@Setter
@Getter
@ToString
@NoArgsConstructor
public class STTMessageDTO implements Serializable {

    private static final long serialVersionUID = 2442664139989439350L;

    private String command;
    private String result;
    private String trxnType;
    private String sttType;
    private String sampleRate;
    private String uid;
    private String status;
    private Float value;
    private String fileName;
    private String errCode;

    public STTMessageDTO(LinkedHashMap<String, Object> rawPayload) {
        this.command = getStringValueIfPresent(rawPayload, "command");
        this.result = getStringValueIfPresent(rawPayload, "result");
        this.trxnType = getStringValueIfPresent(rawPayload, "trxnType");
        this.sttType = getStringValueIfPresent(rawPayload, "sttType");
        this.sampleRate = getStringValueIfPresent(rawPayload, "sampleRate");
        this.uid = getStringValueIfPresent(rawPayload, "uid");
        this.status = getStringValueIfPresent(rawPayload, "status");
        this.value = getFloatValueIfPresent(rawPayload, "value");
        this.fileName = getStringValueIfPresent(rawPayload, "fileName");
        this.errCode = getStringValueIfPresent(rawPayload, "errCode");
    }
}
