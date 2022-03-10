package com.alinote.api.domains;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TranscribeMetaData implements Serializable {
    private static final long serialVersionUID = 6107862953367181069L;

    private List<SpeakerDetails> speakerDetails;

    public TranscribeMetaData(List<SpeakerDetails> speakerDetails) {
        this.speakerDetails = speakerDetails;
    }
}
