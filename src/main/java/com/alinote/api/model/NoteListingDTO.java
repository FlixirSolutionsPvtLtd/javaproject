package com.alinote.api.model;

import com.alinote.api.domains.*;
import lombok.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.alinote.api.utility.CheckUtil.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class NoteListingDTO implements Serializable {
    private static final long serialVersionUID = -2408487648858052820L;
    private String noteId;
    private String parentFolderId;
    private String title;
    private Date date;
    private String memo;
    private boolean favourite;
    private boolean pinned;
    private Long pinnedTs;
    private String noteType;
    private String transcribe;
    private List<Transcribe> transcribes;
    private String speakerName;
    private String speakerNameInitials;
    private String speakerPrflImg;
    private String startTime;


    public NoteListingDTO(Note note) {
        this.noteId = note.getNoteId();
        this.parentFolderId = note.getParentFolderId();
        this.title = note.getTitle();
        this.date = note.getDate();
        this.memo = note.getMemo();
        this.favourite = note.isFavourite();
        this.pinned = note.isPinned();
        this.pinnedTs = note.getPinnedTs();
        this.noteType = note.getNoteType();

        //Get the transcribes which are successfully completed
        this.transcribes = note.getTranscribes().stream().filter(transcribe1 -> transcribe1.getRate() >= 100f).collect(Collectors.toList());

        if (hasValue(this.transcribes)
                && hasValue(this.transcribes.get(0).getUtterances())) {
            //Get Utterance Info
            UtteranceDetails utteranceDetails = this.transcribes.get(0).getUtterances().get(0);

            //Get 1st Transcribe and it's starttime
            this.transcribe = utteranceDetails.getUtterance();
            this.startTime = utteranceDetails.getStartTime();

            //Get Speaker Details
            final int speakerId = utteranceDetails.getSpeakerId();
            Optional<SpeakerDetails> speakerDetailsOpt = this.transcribes.get(0).getMetaData().getSpeakerDetails().stream().filter(speakerDtls -> speakerDtls.getSpeakerId() == speakerId).findFirst();

            if (speakerDetailsOpt.isPresent()) {
                SpeakerDetails speakerDetails = speakerDetailsOpt.get();
                this.speakerName = speakerDetails.getSpeakerName();
                this.speakerNameInitials = speakerDetails.getInitials();
                this.speakerPrflImg = speakerDetails.getPrflImg();
            }
        }
    }
}
