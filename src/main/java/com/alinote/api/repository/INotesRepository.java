package com.alinote.api.repository;

import com.alinote.api.domains.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface INotesRepository extends MongoRepository<Note, String>, INotesCustomRepository {

    List<Note> findAllByParentFolderIdAndActiveStatusAndCreatedBy(String parentFolderId, Integer activeStatus, String createdBy);

    Note findByNoteIdAndActiveStatusAndCreatedBy(String noteId, int value, String createdBy);

    Page<Note> findAllByActiveStatusAndCreatedByOrderByModifiedTsDesc(int value, String userId, Pageable pageRequest);

    Note findByNoteIdAndActiveStatus(String noteId, int value);

    void deleteByNoteIdIn(List<String> filesAndFolderIdsToDelete);

    List<Note> findAllByNoteIdIn(List<String> filesAndFolderIdsToDelete);

    List<Note> findAllByParentFolderIdInAndActiveStatus(List<String> filesAndFolderIdsToDelete, int activeStatus);

    Page<Note> findAllByActiveStatusAndCreatedByAndFavouriteOrderByModifiedTsDesc(int activeStatus, String createdBy, boolean isFavourite, Pageable pageRequest);

    Note findByNoteId(String noteId);

    Optional<Note> findByTranscribes_uuId(String uuid);
}
