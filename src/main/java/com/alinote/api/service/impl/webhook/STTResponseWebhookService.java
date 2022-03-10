package com.alinote.api.service.impl.webhook;

import com.alinote.api.constants.*;
import com.alinote.api.enums.*;
import com.alinote.api.model.webhook.*;
import com.alinote.api.service.*;
import com.alinote.api.service.webhook.*;
import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.io.*;
import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class STTResponseWebhookService implements ISTTResponseWebhookService {

    @Autowired
    private NotesService notesService;

    @Override
    public void updateNoteDetailsFromSTTCallback(Map<String, Object> rawPayload) {
        final String TAG_METHOD_NAME = "updateNoteDetailsFromSTTCallback";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.nanoTime());

        try {
            log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), rawPayload.toString());

            //Get the STT Callback Info
            STTCallBackDTO payload = convertRawPayloadToSttCallBackData(rawPayload);
            log.info("Converted Payload = {}", payload);

            //Check if payload is not empty and payload contains message element
            if (hasValue(payload)
                    && hasValue(payload.getMessage())) {
                STTMessageDTO messageDTO = payload.getMessage();
                String result = messageDTO.getResult();
                String uuid = messageDTO.getUid();

                //Check the result of processing. If success then update note progress details. If fail then update the note progress as -1 and error details
                if (hasValue(result)
                        && GlobalConstants.successCallbackResults.contains(result.trim().toLowerCase())) {
                    Float progress = messageDTO.getValue();

                    if (hasValue(progress) && progress > 0 && hasValue(uuid)) {
                        //If result is success then check the file extension. If smi then update note progress as 100 and get the transcribe data
                        String fileName = messageDTO.getFileName();

                        //If progress is 100 then update transcribe info
                        if (hasValue(fileName)
                                && FilenameUtils.getExtension(fileName).equalsIgnoreCase(STTFileExtensions.smi.name())
                                && progress >= 100f)
                            notesService.updateNoteTranscribeDetailsByUUIDAndFileName(uuid, fileName, progress);
                        else
                            notesService.updateNoteTranscribeProgressByUUID(uuid, progress);
                    }
                } else {
                    //Update failure status and reason
                    notesService.updateNoteTranscribeProgressByUUID(uuid, -1f);
                }
            }
        } catch (Exception e) {
            log.error(GlobalConstants.LOG.ERROR, TAG_METHOD_NAME, getClass().getName(), e);
        }

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.nanoTime());
    }

    private STTCallBackDTO convertRawPayloadToSttCallBackData(Map<String, Object> rawPayload) throws IOException {
        STTCallBackDTO sttCallBackDTO = new STTCallBackDTO(rawPayload);
        return sttCallBackDTO;
    }
}
