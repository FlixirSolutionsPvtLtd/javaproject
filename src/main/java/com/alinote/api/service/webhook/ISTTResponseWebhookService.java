package com.alinote.api.service.webhook;

import com.alinote.api.model.webhook.*;

import java.util.*;

public interface ISTTResponseWebhookService {

    void updateNoteDetailsFromSTTCallback(Map<String, Object> payload);
}
