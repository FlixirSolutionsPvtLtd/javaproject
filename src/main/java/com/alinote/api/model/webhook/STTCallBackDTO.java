package com.alinote.api.model.webhook;

import com.fasterxml.jackson.databind.*;
import lombok.*;

import java.io.*;
import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;
import static com.alinote.api.utility.CommonUtility.*;


@Setter
@Getter
@ToString
@NoArgsConstructor
public class STTCallBackDTO implements Serializable {
    private static final long serialVersionUID = 8678850927742036080L;

    private String result;
    private String uuId;
    private STTMessageDTO message;
    private String rate;
    private String errCode;
    private String errMsg;


    public STTCallBackDTO(Map<String, Object> rawPayload) throws IOException {
        this.result = getStringValueIfPresent(rawPayload, "result");
        this.uuId = getStringValueIfPresent(rawPayload, "uuId");
        this.rate = getStringValueIfPresent(rawPayload, "rate");
        this.errCode = getStringValueIfPresent(rawPayload, "errCode");
        this.errMsg = getStringValueIfPresent(rawPayload, "errMsg");

        Object messageObjectFromPayload = rawPayload.get("message");
        if (hasValue(messageObjectFromPayload)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            this.message = new ObjectMapper().readValue(messageObjectFromPayload.toString(), STTMessageDTO.class);
        }
    }
}
