package com.alinote.api.model;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class TranscribeDTO implements Serializable {

    private static final long serialVersionUID = 5325939881779871367L;
    private TranscribeMetaDataDTO metaData;
    private List<UtteranceDetailsDTO> utterances;

    public TranscribeDTO(TranscribeMetaDataDTO metaData, List<UtteranceDetailsDTO> utterances) {
        this.metaData = metaData;
        this.utterances = utterances;
    }
}
