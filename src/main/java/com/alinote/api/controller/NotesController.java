package com.alinote.api.controller;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = {RestUrlConstants.NOTES})
public class NotesController {


    @Autowired
    private NotesService notesService;

    @GetMapping
    @ApiOperation(value = "Get Recent Notes")
    public BaseWrapper getRecentNotesOrCalls(
            @RequestParam(value = GlobalConstants.Pagination.PAGE_SIZE, required = false, defaultValue = "0") int size,
            @RequestParam(value = GlobalConstants.Pagination.PAGE_NO, required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "recentNotes", required = true, defaultValue = "true") boolean recentNotes,
            @RequestHeader(value = GlobalConstants.AUTH_USER_EMAIL, defaultValue = "piyushjadhav65@gmail.com") String authUserEmail
    ) throws ServicesException {
        final String TAG_METHOD_NAME = "getRecentNotesOrCalls()";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return notesService.getRecentNotesOrCalls(recentNotes, pageNo, size);
    }

    @ApiOperation(value = "Get notes and folders by parent folder Id")
    @GetMapping(value = RestUrlConstants.NOTES_GET_NOTES_AND_FOLDERS_BY_PARENT_FOLDER_ID)
    public BaseWrapper getNotesAndFolderHierarchyByParentFolderId(
            @PathVariable(GlobalConstants.PARENT_FOLDER_ID) String parentFolderId) throws ServicesException {
        log.info("--> getNotesAndFolderHierarchyByParentFolderId(parentFolderId:{})", parentFolderId);
        BaseWrapper baseResponse = notesService.getNotesAndFoldersByParentFolderId(parentFolderId);
        log.info("<-- getNotesAndFolderHierarchyByParentFolderId() : response {}", baseResponse);
        return baseResponse;
    }

    @ApiOperation(value = "Create note and start transcribe")
    @PostMapping
    public BaseWrapper createNoteAndStartTranscribe(
            @RequestBody NoteCreateDTO noteRequest) throws ServicesException {
        log.info("--> createNoteAndStartTranscribe(folderRequest:{})", noteRequest);
        BaseWrapper baseResponse = notesService.createNoteAndStartTranscribe(noteRequest);
        log.info("<-- createNoteAndStartTranscribe() : response {}", baseResponse);
        return baseResponse;
    }


    @ApiOperation(value = "Get transcribe progress for a note")
    @GetMapping(value = RestUrlConstants.NOTES_TRANSCRIBE_PROGRESS)
    public BaseWrapper getTranscribeProgress(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @PathVariable(GlobalConstants.UUID) String uuid) throws ServicesException {
        log.info("--> getTranscribeProgress(noteId:{})", noteId);
        BaseWrapper baseResponse = notesService.getTranscribeProgress(noteId, uuid);
        log.info("<-- getTranscribeProgress() : response {}", baseResponse);
        return baseResponse;
    }

    @ApiOperation(value = "Get note details")
    @GetMapping(value = RestUrlConstants.NOTES_DETAILS)
    public BaseWrapper getNoteDetails(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId) throws ServicesException {
        log.info("--> getNoteDetails(noteId:{})", noteId);
        BaseWrapper baseResponse = notesService.getNoteDetails(noteId);
        log.info("<-- getNoteDetails() : response {}", baseResponse);
        return baseResponse;
    }

    @ApiOperation(value = "Update note details")
    @PutMapping(value = RestUrlConstants.NOTES_DETAILS)
    public BaseWrapper updateNoteDetails(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @RequestBody SingleValue<String> request) throws ServicesException {
        log.info("--> updateNoteDetails(noteId:{}, request:{})", noteId, request.toString());
        BaseWrapper baseResponse = notesService.updateNoteDetails(noteId, request);
        log.info("<-- updateNoteDetails() : response {}", baseResponse);
        return baseResponse;
    }

    @ApiOperation(value = "Delte a note")
    @DeleteMapping(value = RestUrlConstants.FOLDER_DETAILS)
    public BaseWrapper deleteNotesAndFolder(
            @PathVariable(GlobalConstants.PARENT_FOLDER_ID) String parentFolderId,
            @RequestBody List<String> filesAndFolderIdsToDelete) throws ServicesException {
        final String TAG_METHOD_NAME = "deleteNotesAndFolder";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        BaseWrapper baseResponse = notesService.deleteNotesAndFolder(filesAndFolderIdsToDelete);
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return baseResponse;
    }

    @ApiOperation(value = "Api to create folder")
    @PostMapping(value = RestUrlConstants.FOLDER_DETAILS)
    public BaseWrapper createFolder(
            @PathVariable(GlobalConstants.PARENT_FOLDER_ID) String parentFolderId,
            @RequestBody Folder folderRequest) throws ServicesException {
        log.info("--> createFolder(parentFolderId:{},folderRequest:{})", parentFolderId, folderRequest);
        BaseWrapper baseResponse = notesService.createFolder(parentFolderId, folderRequest);
        log.info("<-- createFolder() : response {}", baseResponse);
        return baseResponse;
    }

    @ApiOperation(value = "Api to update folder")
    @PutMapping(value = RestUrlConstants.UPDATE_FOLDER)
    public BaseWrapper updateFolder(
            @PathVariable(GlobalConstants.PARENT_FOLDER_ID) String parentFolderId,
            @PathVariable(GlobalConstants.FOLDER_ID) String folderId,
            @RequestBody Folder folderRequest) throws ServicesException {
        final String TAG_METHOD_NAME = "updateFolder";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        BaseWrapper baseResponse = notesService.updateFolder(parentFolderId, folderId, folderRequest);
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());
        return baseResponse;
    }

    @GetMapping(value = RestUrlConstants.RECYCLE_BIN)
    @ApiOperation(value = "Api to get items in recycle bin for logged in user")
    public BaseWrapper getNotesInRecycleBin() {
        return notesService.getNotesInRecycleBin();
    }

    @GetMapping(value = RestUrlConstants.RECOVER_NOTE)
    @ApiOperation(value = "Api to recover note from recycle bin for logged in user")
    public BaseWrapper recoverNoteFromRecycleBin(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId
    ) throws ServicesException {
        return notesService.recoverNoteFromRecycleBin(noteId);
    }

    @GetMapping(value = RestUrlConstants.ARCHIEVE_NOTE)
    @ApiOperation(value = "Api to archieve note in recycle bin for logged in user")
    public BaseWrapper archieveNote(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId
    ) throws ServicesException {
        return notesService.archieveNote(noteId);
    }

    @GetMapping(value = RestUrlConstants.FAVOURITE_NOTES)
    @ApiOperation(value = "Api to get notes marked as favourite by logged in user")
    public BaseWrapper getFavouriteNotes() {
        return notesService.getFavouriteNotes();
    }

    @GetMapping(value = RestUrlConstants.MARK_UNMARK_FAVOURITE_NOTE)
    @ApiOperation(value = "Api to mark/unmark any notes as favourite by logged in user")
    public BaseWrapper markUmmarkFavouriteNote(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @PathVariable(value = "markFavourite") boolean markFavourite
    ) throws ServicesException {
        return notesService.markUmmarkFavouriteNote(noteId, markFavourite);
    }

    @GetMapping(value = RestUrlConstants.PIN_UNPIN_FAVOURITE_NOTE)
    @ApiOperation(value = "Api to pin/unpin any notes as favourite by logged in user")
    public BaseWrapper pinUnpinFavouriteNote(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @PathVariable(value = "isPinned") boolean isPinned
    ) throws ServicesException {
        notesService.pinUnpinFavouriteNote(noteId, isPinned);
        return getFavouriteNotes();
    }

    @PutMapping(value = {RestUrlConstants.UPDATE_UTTERANCE})
    @ApiOperation(value = "Api to update any utterance")
    public BaseWrapper updateUtterance(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @PathVariable(GlobalConstants.UUID) String uuid,
            @PathVariable("utteranceId") int utteranceId,
            @RequestBody SingleValue<String> updatedUtterance) throws ServicesException {
        return notesService.updateUtterance(noteId, uuid, utteranceId, updatedUtterance);
    }

    @PutMapping(value = {RestUrlConstants.UPDATE_MEMO})
    @ApiOperation(value = "Api to update memo for a note")
    public BaseWrapper updateMemo(
            @PathVariable(GlobalConstants.NOTE_ID) String noteId,
            @RequestBody SingleValue<String> updatedMemo) throws ServicesException {
        return notesService.updateMemo(noteId, updatedMemo);
    }

    @GetMapping(value = {RestUrlConstants.FULL_TEXT_SEARCH})
    @ApiOperation(value = "Api to do a full text search on note title and itstranscribe text")
    public BaseWrapper fullTextSearch(
            @RequestParam(value = GlobalConstants.StringConstants.SEARCH_TERM) String term,
            @RequestParam(value = "noteType", required = false, defaultValue = "ALL") NoteTypes noteType,
            @RequestParam(value = GlobalConstants.Pagination.PAGE_SIZE, required = false, defaultValue = "100") int size,
            @RequestParam(value = GlobalConstants.Pagination.PAGE_NO, required = false, defaultValue = "1") int pageNo) throws ServicesException {
        return notesService.fullTextSearch(term, noteType, pageNo, size);
    }
}
