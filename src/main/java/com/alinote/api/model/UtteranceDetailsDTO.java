package com.alinote.api.model;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UtteranceDetailsDTO implements Serializable {
    private static final long serialVersionUID = 338799644061423766L;
    private String utteranceId;
    private String speakerId;
    private String startTime;
    private String utterance;

    public UtteranceDetailsDTO(String utteranceId, String speakerId, String startTime, String utterance) {
        this.utteranceId = utteranceId;
        this.speakerId = speakerId;
        this.startTime = startTime;
        this.utterance = utterance;
    }
}
