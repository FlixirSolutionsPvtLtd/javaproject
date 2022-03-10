package com.alinote.api.model;

import com.alinote.api.model.common.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteSearchResponseDTO implements Serializable {
    private static final long serialVersionUID = -8173002501099155571L;

    private PaginatedDataVO<List<NoteCallSearchDTO>> notes;
    private PaginatedDataVO<List<NoteCallSearchDTO>> calls;
}
