package com.alinote.api.model;

import com.alinote.api.domains.*;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import java.io.*;
import java.util.*;

import static com.alinote.api.utility.CheckUtil.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteCallSearchDTO implements Serializable {

    private static final long serialVersionUID = -1679129498051440143L;
    private Long createdTs;
    private Long modifiedTs;
    private String noteId;
    private String parentFolderId;
    private String title;
    private Date date;
    @JsonProperty(value = "favourite")
    private boolean favourite;
    private String transcribes;
    private String noteType;

    public NoteCallSearchDTO(Note note) {
        this.createdTs = note.getCreatedTs();
        this.modifiedTs = note.getModifiedTs();
        this.noteId = note.getNoteId();
        this.parentFolderId = note.getParentFolderId();
        this.title = note.getTitle();
        this.date = note.getDate();
        this.favourite = note.isFavourite();
        this.noteType = note.getNoteType();

        List<Transcribe> transcribes = note.getTranscribes();
        if (hasValue(transcribes)
                && hasValue(transcribes.get(0).getUtterances()))
            this.transcribes = transcribes.get(0).getUtterances().get(0).getUtterance();
    }
}
