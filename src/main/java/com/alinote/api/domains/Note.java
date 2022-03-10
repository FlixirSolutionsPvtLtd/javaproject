package com.alinote.api.domains;

import com.alinote.api.constants.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = GlobalConstants.DocumentCollections.COLLECTION_NOTE)
public class Note extends Auditable {

    @Id
    private String noteId;

    @Field("parent_folder_id")
    private String parentFolderId;
    @TextIndexed
    private String title;
    private Date date;
    @TextIndexed
    private String memo;

    @Field("is_favourite")
    private boolean favourite;

    @Field("is_pinned")
    private boolean pinned;

    @Field("pinned_ts")
    private Long pinnedTs;

    @Field("note_type")
    private String noteType;

    private List<Transcribe> transcribes = new LinkedList<>();
}
