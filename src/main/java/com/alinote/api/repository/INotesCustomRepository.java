package com.alinote.api.repository;

import com.alinote.api.domains.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;

import java.util.*;

public interface INotesCustomRepository {

    void archeivedAllDeleteNoteBeforeConfguredThreshold(long thresholdTimeToArchieve);

    PaginatedDataVO<List<Note>> searchNoteByTermAndNoteTypeAndCreatedBy(String term, String noteType, int pageNo, int size, String userID);

    List<FolderFilesCountDTO> countFilesUnderFolders(List<String> folderIds);
}
