package com.alinote.api.model;

import com.alinote.api.enums.*;
import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
public class NoteCreateDTO implements Serializable {

    private String noteId;
    private String parentFolderId;
    private String title;
    private String inPath;
    private String fileName;
    private String uuid;
    private String noteType = NoteTypes.AUDIO.name();
}
