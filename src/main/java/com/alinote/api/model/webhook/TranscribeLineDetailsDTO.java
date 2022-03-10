package com.alinote.api.model.webhook;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TranscribeLineDetailsDTO implements Serializable {
    private static final long serialVersionUID = -7626950652703909983L;

    private int utteranceSequence;
    private int speakerSequence;
    private String utteranceStartTime;
    private String utterance;
    private Float confidenceValue;
}
