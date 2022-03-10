package com.alinote.api.domains;

import lombok.*;
import org.springframework.data.mongodb.core.index.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UtteranceDetails extends Auditable implements Serializable {
    private static final long serialVersionUID = 338799644061423766L;

    private int utteranceId;
    private int speakerId;
    private String startTime;
    @TextIndexed
    private String utterance;

    public UtteranceDetails(int utteranceId, int speakerId, String startTime, String utterance) {
        this.utteranceId = utteranceId;
        this.speakerId = speakerId;
        this.startTime = startTime;
        this.utterance = utterance;
    }
}
