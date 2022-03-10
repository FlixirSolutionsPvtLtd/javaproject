package com.alinote.api.model;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class SpeakerDetailsDTO implements Serializable {
    private static final long serialVersionUID = -1457858566457273044L;
    private String speakerId;
    private String speakerName;
    private boolean canDelete;
    private String prflImg;
    private String initials;

    public SpeakerDetailsDTO(String speakerId, String speakerName,
                             boolean canDelete, String prflImg,
                             String initials) {
        this.speakerId = speakerId;
        this.speakerName = speakerName;
        this.canDelete = canDelete;
        this.prflImg = prflImg;
        this.initials = initials;
    }
}
