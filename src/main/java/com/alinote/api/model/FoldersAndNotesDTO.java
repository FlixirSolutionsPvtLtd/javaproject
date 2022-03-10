package com.alinote.api.model;

import com.alinote.api.domains.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoldersAndNotesDTO {
    private List<Folder> folders;
    private List<NoteListingDTO>  notes;
}
