package com.alinote.api.repository.impl;

import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.repository.*;
import com.alinote.api.utility.*;
import com.mongodb.client.result.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.repository.support.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

import static com.alinote.api.utility.CheckUtil.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Component
public class INotesCustomRepositoryImpl implements INotesCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void archeivedAllDeleteNoteBeforeConfguredThreshold(long thresholdTimeToArchieve) {
        //Create Query with Criteria
        Query query = new Query();
        query.addCriteria(Criteria.where(AuditableDbCollectionKeys.is_active.name()).is(ActiveStatus.INACTIVE.value()));
        query.addCriteria(Criteria.where(AuditableDbCollectionKeys.modified_ts.name()).lte(thresholdTimeToArchieve));

        //Set update info
        Update update = new Update();
        update.set(AuditableDbCollectionKeys.is_active.name(), ActiveStatus.ARCHIEVE.value());

        //Update records
        UpdateResult result = mongoTemplate.updateMulti(query, update, Note.class);
        if (hasValue(result))
            log.debug("Archieved {} notes in deleted state", result.getModifiedCount());
    }

    @Override
    public PaginatedDataVO<List<Note>> searchNoteByTermAndNoteTypeAndCreatedBy(String term, String noteType, int pageNo, int size, String userID) {

        //Prepare matching criteria
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(term);

        //Prepare pagination criteria
        Pageable pageRequest = PaginationUtils.getPageRequest(pageNo, size);

        //Prepare Query
        Query query = TextQuery
                .queryText(criteria)
                .addCriteria(Criteria.where(NoteDbCollectionKeys.note_type.name()).is(noteType))
                .addCriteria(Criteria.where(AuditableDbCollectionKeys.created_by.name()).is(userID));
        //Note: Uncomment below line if pagination is required
//                .with(pageRequest);

        //Get paginated notes matching search term
        List<Note> notes = mongoTemplate.find(query, Note.class);

        //Get Pagination Details
        Page<Note> notePage = PageableExecutionUtils.getPage(
                notes,
                pageRequest,
                () -> mongoTemplate.count(query, Note.class));
        PaginationRequest paginationRequest = new PaginationRequest(pageNo, pageRequest.getPageSize(), notePage);

        return new PaginatedDataVO<>(notes, paginationRequest);
    }

    @Override
    public List<FolderFilesCountDTO> countFilesUnderFolders(List<String> folderIds) {
        //Define Final List
        final List<FolderFilesCountDTO> notesAndFoldersCountList = new ArrayList<>();
        final Map<String, FolderFilesCountDTO> folderNotesMap = new ConcurrentHashMap<>();

        //Define Aggregate function
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(NoteDbCollectionKeys.parentFolderId.name()).in(folderIds)),
                group(NoteDbCollectionKeys.parentFolderId.name())
                        .count().as("filesCount")
        );

        //Get Aggregate Notes List for given parent folder ID's
        AggregationResults<FolderFilesCountDTO> notesCountResults = mongoTemplate.aggregate(
                aggregation,
                Note.class,
                FolderFilesCountDTO.class);
        final List<FolderFilesCountDTO> notesCountList = notesCountResults.getMappedResults();
        System.out.println("notesCountList Size = " + notesCountList.size());
        System.out.println("notesCountList Data = " + notesCountList.toString());
        if (hasValue(notesCountList))
            notesCountList.forEach(noteCountResult -> {
                folderNotesMap.put(noteCountResult.getId(), noteCountResult);
            });

        AggregationResults<FolderFilesCountDTO> foldersCountResults = mongoTemplate.aggregate(
                aggregation,
                Folder.class,
                FolderFilesCountDTO.class);
        List<FolderFilesCountDTO> foldersCountList = foldersCountResults.getMappedResults();
        System.out.println("foldersCountList Size = " + foldersCountList.size());
        System.out.println("foldersCountList Data = " + foldersCountList.toString());
        if (hasValue(foldersCountList)) {
            foldersCountList.forEach(folderCountResult -> {
                String id = folderCountResult.getId();
                FolderFilesCountDTO folderFilesCountDTO;
                if (folderNotesMap.containsKey(id)) {
                    folderFilesCountDTO = folderNotesMap.get(id);
                    int finalFilesSize = folderFilesCountDTO.getFilesCount() + folderCountResult.getFilesCount();
                    folderFilesCountDTO.setFilesCount(finalFilesSize);
                } else
                    folderFilesCountDTO = folderCountResult;

                folderNotesMap.put(id, folderFilesCountDTO);
            });
        }

        //Add all updated FolderFilesCountDTO updated objects in final list
        notesAndFoldersCountList.addAll(folderNotesMap.values());

        return notesAndFoldersCountList;
    }

    /**
     * Enum to store DB Collection Key names
     */
    private enum NoteDbCollectionKeys {
        note_type, parentFolderId;
    }
}