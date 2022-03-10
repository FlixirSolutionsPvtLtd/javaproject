package com.alinote.api.domains;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class SpeakerDetails implements Serializable {
    private static final long serialVersionUID = -1457858566457273044L;

    private int speakerId;
    private String speakerName;
    private String memberId;
    private boolean canDelete;
    private String prflImg;
    private String initials;

    public SpeakerDetails(int speakerId, String speakerName,
                          boolean canDelete, String prflImg,
                          String initials) {
        this.speakerId = speakerId;
        this.speakerName = speakerName;
        this.canDelete = canDelete;
        this.prflImg = prflImg;
        this.initials = initials;
    }
}
