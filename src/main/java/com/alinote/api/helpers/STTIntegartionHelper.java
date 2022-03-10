package com.alinote.api.helpers;

import com.alinote.api.config.*;
import com.alinote.api.constants.*;
import com.alinote.api.model.*;
import com.alinote.api.model.webhook.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Slf4j
@Component
public class STTIntegartionHelper {

    @Value("${sttengine.base.url}")
    private String sttEngineBaseUrl;
    @Value("${sttengine.processing.url}")
    private String sttEngineProcessingUrl;
    @Value("${sttengine.callback.url}")
    private String sttEngineCallbackUrl;

    @Autowired
    private AWSConfigurationDetails awsConfigurationDetails;

    public STTCallBackDTO startTranscribe(NoteCreateDTO noteRequest) {
        final String TAG_METHOD_NM = "startTranscribe";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NM, getClass().getName(), System.nanoTime());

        //Create URL to hit
        final String url = sttEngineBaseUrl + sttEngineProcessingUrl;

        //Prepare request body
        STTProcessFileDTO requestEntity = new STTProcessFileDTO(noteRequest.getUuid(), noteRequest.getFileName(),
                awsConfigurationDetails.getAccesskey(), awsConfigurationDetails.getSecretkey(), awsConfigurationDetails.getBucket(), sttEngineCallbackUrl);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, getClass().getName(), "STT transcribe request For UUID : " + noteRequest.getUuid() + " = " + requestEntity.toString());

        //Make API call and get response
        RestTemplate restTemplate = new RestTemplate();
        STTCallBackDTO sttCallBackDTO = restTemplate.postForObject(url, requestEntity, STTCallBackDTO.class);
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, getClass().getName(), null != sttCallBackDTO ? sttCallBackDTO.toString() : null);

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NM, getClass().getName(), System.nanoTime());
        return sttCallBackDTO;
    }
}
