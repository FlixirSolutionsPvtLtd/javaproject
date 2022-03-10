package com.alinote.api.model;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TranscribeMetaDataDTO implements Serializable {

    private static final long serialVersionUID = 1745422234049097359L;
    private List<SpeakerDetailsDTO> speakerDetails;

    public TranscribeMetaDataDTO(List<SpeakerDetailsDTO> speakerDetails) {
        this.speakerDetails = speakerDetails;
    }
}
