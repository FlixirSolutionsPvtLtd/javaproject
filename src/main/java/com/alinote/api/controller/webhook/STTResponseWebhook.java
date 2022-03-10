package com.alinote.api.controller.webhook;

import com.alinote.api.constants.*;
import com.alinote.api.model.webhook.*;
import com.alinote.api.service.webhook.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = {RestUrlConstants.WEBHOOK_STT_CALLBACK})
public class STTResponseWebhook {

    @Autowired
    private ISTTResponseWebhookService isttResponseWebhookService;

    @Async
    @PostMapping
    @ApiOperation(value = "API to receive the callback from STT engine while conversion of files")
    public void sttCallback(@RequestBody Map<String, Object> payload) {
        final String TAG_METHOD_NAME = "sttCallback";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.nanoTime());

        isttResponseWebhookService.updateNoteDetailsFromSTTCallback(payload);

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.nanoTime());
    }
}
