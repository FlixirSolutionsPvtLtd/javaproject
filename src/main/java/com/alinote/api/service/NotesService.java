package com.alinote.api.service;

import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;

import java.io.*;
import java.util.*;

public interface NotesService {
    BaseWrapper getRecentNotesOrCalls(boolean recentNotes, int pageNo, int size) throws ServicesException;

    BaseWrapper getNotesAndFoldersByParentFolderId(String parentFolderId) throws ServicesException;

    BaseWrapper createFolder(String parentFolderId, Folder folderRequest) throws ServicesException;

    BaseWrapper updateFolder(String parentFolderId, String folderId, Folder folderRequest) throws ServicesException;

    BaseWrapper createNoteAndStartTranscribe(NoteCreateDTO noteRequest) throws ServicesException;

    BaseWrapper getTranscribeProgress(String noteId, String uuid) throws ServicesException;

    BaseWrapper getNoteDetails(String noteId) throws ServicesException;

    BaseWrapper updateNoteDetails(String noteId, SingleValue<String> request) throws ServicesException;

    BaseWrapper deleteNotesAndFolder(List<String> filesAndFolderIdsToDelete);

    BaseWrapper getNotesInRecycleBin();

    BaseWrapper recoverNoteFromRecycleBin(String noteId) throws ServicesException;

    BaseWrapper archieveNote(String noteId) throws ServicesException;

    BaseWrapper getFavouriteNotes();

    BaseWrapper markUmmarkFavouriteNote(String noteId, boolean markFavourite) throws ServicesException;

    BaseWrapper pinUnpinFavouriteNote(String noteId, boolean isPinned) throws ServicesException;

    BaseWrapper updateUtterance(String noteId, String uuid, int utteranceId, SingleValue<String> updatedUtterance) throws ServicesException;

    BaseWrapper updateMemo(String noteId, SingleValue<String> updatedMemo) throws ServicesException;

    void updateNoteTranscribeProgressByUUID(String uuid, Float progress) throws ServicesException;

    void updateNoteTranscribeDetailsByUUIDAndFileName(String uuid, String fileName, Float progress) throws ServicesException, IOException;

    BaseWrapper fullTextSearch(String term, NoteTypes noteType, int pageNo, int size) throws ServicesException;
}
