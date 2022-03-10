package com.alinote.api.repository;


import com.alinote.api.domains.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder, String> {

    List<Folder> findAllByParentFolderIdAndActiveStatusAndCreatedBy(String parentFolderId, Integer activeStatus, String createdBy);

    Optional<Folder> findByFolderIdAndActiveStatus(String folderId, int activeStatus);

    List<Folder> findAllByParentFolderIdInAndActiveStatus(List<String> filesAndFolderIdsToDelete, int value);

    List<Folder> findALlByFolderIdIn(List<String> filesAndFolderIdsToDelete);

    Folder findByFolderId(String folderId);
}
