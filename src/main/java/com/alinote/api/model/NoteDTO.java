package com.alinote.api.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NoteDTO {

    private String noteId;
    private String parentFolderId;
    private String title;
    private Date date;
    private List<TranscribeDTO> transcribes;
    private String memo;

    private String uuId;

    private String inPath;
    private String outPath;
    private String fileName;
    private Float rate;

    private boolean favourite;
    private boolean pinned;
    private Long pinnedTs;


}
