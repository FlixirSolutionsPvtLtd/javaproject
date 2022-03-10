package com.alinote.api.service.impl;

import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.exception.*;
import com.alinote.api.helpers.*;
import com.alinote.api.model.*;
import com.alinote.api.model.common.*;
import com.alinote.api.model.webhook.*;
import com.alinote.api.repository.*;
import com.alinote.api.security.service.*;
import com.alinote.api.service.*;
import com.alinote.api.utility.*;
import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.alinote.api.utility.CheckUtil.*;
import static com.alinote.api.utility.CommonUtility.*;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class NotesServiceImpl implements NotesService {

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private INotesRepository notesRepository;

    @Autowired
    private NoteHelper noteHelper;
    @Autowired
    private STTIntegartionHelper sttIntegartionHelper;

    @Value("${note.progress.cutoff.mins}")
    private long noteProgressCutOffTimeInMins;

    @Override
    public BaseWrapper createFolder(String parentFolderId, Folder folderRequest) throws ServicesException {
        if (!hasValue(parentFolderId))
            throw new ServicesException(GeneralErrorCodes.ERR_PARENT_FOLDER_ID_NOT_SUPPLIED.value());

        if (!hasValue(folderRequest.getName()))
            throw new ServicesException(GeneralErrorCodes.ERR_FOLDER_NAME_NOT_SUPPLIED.value());

        Users user = authenticationService.fetchLoggedInUser();
        folderRequest.setParentFolderId(parentFolderId);
        folderRequest.updateAuditableFields(true, hasValue(user) ? user.getUserId() : GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
        folderRepository.save(folderRequest);
        return new BaseWrapper(folderRequest);
    }

    @Override
    public BaseWrapper updateFolder(String parentFolderId, String folderId, Folder folderRequest) throws ServicesException {
        if (!hasValue(parentFolderId))
            throw new ServicesException(GeneralErrorCodes.ERR_PARENT_FOLDER_ID_NOT_SUPPLIED.value());

        if (!hasValue(folderId))
            throw new ServicesException(701);

        if (!hasValue(folderRequest.getName()))
            throw new ServicesException(GeneralErrorCodes.ERR_FOLDER_NAME_NOT_SUPPLIED.value());

        //Fetch Folder Details associated with folderId
        Optional<Folder> folderDetailsOpt = folderRepository.findByFolderIdAndActiveStatus(folderId, ActiveStatus.ACTIVE.value());
        if (!folderDetailsOpt.isPresent())
            throw new ServicesException(702);

        Folder folder = folderDetailsOpt.get();
        folder.setName(folderRequest.getName());
        Users user = authenticationService.fetchLoggedInUser();
        folder.updateAuditableFields(false, user.getUserId(), ActiveStatus.ACTIVE.value());
        folderRepository.save(folder);

        return new BaseWrapper(folder);
    }

    @Override
    public BaseWrapper createNoteAndStartTranscribe(NoteCreateDTO noteRequest) throws ServicesException {
        validateNoteCreateRequest(noteRequest);

        //Create Note
        Note note;
        boolean isCreate;
        if (hasValue(noteRequest.getNoteId())) {
            note = notesRepository.findByNoteId(noteRequest.getNoteId());
            isCreate = false;
        } else {
            note = new Note();
            BeanUtils.copyProperties(noteRequest, note);
            note.setDate(Calendar.getInstance().getTime());
            isCreate = true;
        }

        Users user = authenticationService.fetchLoggedInUser();
        String userID = hasValue(user) && hasValue(user.getUserId()) ? user.getUserId() : GlobalConstants.USER_API;

        String fileName = noteRequest.getFileName();
        String inPath = noteRequest.getInPath();
        int noteActiveStatus = ActiveStatus.INACTIVE.value();
        //Check if a blank note is being created or some audio file has been passed. If passed then create the transcribe
        if (hasValue(fileName) && hasValue(inPath)) {
            //Generate the parameters required for note creation
            String uuid = UUID.randomUUID().toString();
            noteRequest.setUuid(uuid);

            //Start Transcribe
            STTCallBackDTO sttCallBackDTO = sttIntegartionHelper.startTranscribe(noteRequest);

            //Update Transcribe response
            //Check if result is `success` or `fail` and set rate as 0f or -1f
            float rate = hasValue(sttCallBackDTO.getResult()) &&
                    GlobalConstants.successCallbackResults.contains(sttCallBackDTO.getResult().trim().toLowerCase()) ? 0f : -1f;

            Transcribe transcribe = new Transcribe(FilenameUtils.getBaseName(fileName), uuid, inPath, fileName, rate, sttCallBackDTO);
            transcribe.updateAuditableFields(true, userID, ActiveStatus.INACTIVE.value());

            note.getTranscribes().add(transcribe);
        } else {
            noteActiveStatus = ActiveStatus.ACTIVE.value();
        }

        //Save Note Details
        note.updateAuditableFields(isCreate, userID, noteActiveStatus);
        notesRepository.save(note);

        //Update response data
        noteRequest.setNoteId(note.getNoteId());

        return new BaseWrapper(noteRequest);
    }

    private void validateNoteCreateRequest(NoteCreateDTO noteRequest) throws ServicesException {
        if (!hasValue(noteRequest.getParentFolderId()))
            throw new ServicesException(GeneralErrorCodes.ERR_PARENT_FOLDER_ID_NOT_SUPPLIED.value());

        if (!hasValue(noteRequest.getTitle()))
            throw new ServicesException(GeneralErrorCodes.ERR_NOTE_TITLE_NOT_SUPPLIED.value());
    }

    @Override
    public BaseWrapper getTranscribeProgress(String noteId, String uuId) throws ServicesException {
        Note note = notesRepository.findByNoteId(noteId);
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        Float noteProgress = 0f;
        for (int i = 0; i < note.getTranscribes().size(); i++) {
            if (note.getTranscribes().get(i).getUuId().equals(uuId)) {
                noteProgress = note.getTranscribes().get(i).getRate();
//                updateStaticNoteProgress(noteProgress, note, i);
                break;
            }
        }

        if (noteProgress >= 100)
            return new BaseWrapper(new NoteProgressDetailsDTO(noteProgress));

        if (noteProgress < 0
                || DateTimeUtils.timeDiffInMinutes(note.getCreatedTs(), System.currentTimeMillis()) >= noteProgressCutOffTimeInMins) {
            throw new ServicesException(GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
        }

        return new BaseWrapper(new NoteProgressDetailsDTO(noteProgress));
    }

    private void updateStaticNoteProgress(Float noteProgress, Note note, int transcribeIndex) {
        if (!hasValue(noteProgress))
            noteProgress = 25f;
        else if (noteProgress <= 75f)
            noteProgress = noteProgress + 25f;

        note.getTranscribes().get(transcribeIndex).setRate(noteProgress);

        notesRepository.save(note);
    }

    @Override
    public BaseWrapper getNoteDetails(String noteId) throws ServicesException {
        Users user = authenticationService.fetchLoggedInUser();
        Note note = notesRepository.findByNoteIdAndActiveStatusAndCreatedBy(noteId, ActiveStatus.ACTIVE.value(), user.getUserId());
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        //Remove the transcribes whose rate != 100
        NoteListingDTO noteListingDTO = new NoteListingDTO(note);

        return new BaseWrapper(noteListingDTO);
    }

    @Override
    public BaseWrapper updateNoteDetails(String noteId, SingleValue<String> request) throws ServicesException {
        String title = request.getTerm();
        if (!hasValue(title))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Fetch notes associated with given note ID
        Note note = notesRepository.findByNoteIdAndActiveStatus(noteId, ActiveStatus.ACTIVE.value());
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        //Update note title and save
        note.setTitle(request.getTerm());
        note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        notesRepository.save(note);

        return new BaseWrapper(note);
    }

    @Override
    public BaseWrapper getNotesAndFoldersByParentFolderId(String parentFolderId) throws ServicesException {
        Users user = authenticationService.fetchLoggedInUser();

        List<Folder> folderList = folderRepository.findAllByParentFolderIdAndActiveStatusAndCreatedBy(parentFolderId, ActiveStatus.ACTIVE.value(), hasValue(user) ? user.getUserId() : GlobalConstants.USER_API);

        //Get No Of Files under each folder
        List<Folder> finalFolderList;
        if (hasValue(folderList)) {
            finalFolderList = new ArrayList<>();
            final List<String> folderIdsList = new ArrayList<>();
            final Map<String, Folder> folderIdToFolderMap = new ConcurrentHashMap<>();
            folderList.forEach(folder -> {
                String folderId = folder.getFolderId();
                folderIdsList.add(folderId);
                folderIdToFolderMap.put(folderId, folder);
            });

            //Get folder id and files count for all folder id's who have atleast 1 note in them
            List<FolderFilesCountDTO> notesAndFoldersCountUnderFolderList = notesRepository.countFilesUnderFolders(folderIdsList);

            for (FolderFilesCountDTO folderFilesCountDTO : notesAndFoldersCountUnderFolderList) {
                Folder folderFromMap = folderIdToFolderMap.get(folderFilesCountDTO.getId());
                if (hasValue(folderFromMap)) {
                    folderFromMap.setNoOfFiles(Integer.toString(folderFilesCountDTO.getFilesCount()));
                    finalFolderList.add(folderFromMap);
                    folderIdToFolderMap.remove(folderFilesCountDTO.getId());
                }
            }

            //Add all other folder's who don't have any files in them
            finalFolderList.addAll(folderIdToFolderMap.values());
        } else
            finalFolderList = folderList;

        List<Note> noteList = notesRepository.findAllByParentFolderIdAndActiveStatusAndCreatedBy(parentFolderId, ActiveStatus.ACTIVE.value(), hasValue(user) ? user.getUserId() : GlobalConstants.USER_API);

        return new BaseWrapper(new FoldersAndNotesDTO(finalFolderList, noteList.stream().map(NoteListingDTO::new).collect(Collectors.toList())));
    }

    @Override
    public BaseWrapper getRecentNotesOrCalls(boolean recentNotes, int pageNo, int size) throws ServicesException {
        final String TAG_METHOD_NAME = "getRecentNotesOrCalls";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        //Get Logged In User
        Users user = authenticationService.fetchLoggedInUser();

        //Prepare pagination object
        Pageable pageRequest = PaginationUtils.getPageRequest(pageNo, size);

        Page<Note> notesPage = null;
        List<Note> noteList = null;

        if (recentNotes) {
            notesPage = notesRepository.findAllByActiveStatusAndCreatedByOrderByModifiedTsDesc(ActiveStatus.ACTIVE.value(), user.getUserId(), pageRequest);
            log.info("notesPage getTotalPages: {}", notesPage.getTotalPages());

            if (notesPage.hasContent())
                noteList = notesPage.getContent();
            else
                noteList = new ArrayList<>();
        }

        //Crate Pagination Request
        PaginationRequest paginationRequest = new PaginationRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), notesPage);

        //Prepare response and send
        List<NoteListingDTO> finalNotesList = noteList.stream().map(NoteListingDTO::new).collect(Collectors.toList());
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass().getName(), System.currentTimeMillis());

        return new BaseWrapper(finalNotesList, paginationRequest);
    }

    @Override
    public BaseWrapper deleteNotesAndFolder(List<String> filesAndFolderIdsToDelete) {
        final String TAG_METHOD_NAME = "deleteNotesAndFolder";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass(), filesAndFolderIdsToDelete.toString());

        //Deactivate all notes for given ID's
        List<Note> notesToDeactivate = notesRepository.findAllByNoteIdIn(filesAndFolderIdsToDelete);
        if (hasValue(notesToDeactivate)) {
            notesToDeactivate.forEach(note ->
                    note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.INACTIVE.value()));
            notesRepository.saveAll(notesToDeactivate);
        }

        //Deactivate all folders for given ID's
        List<Folder> foldersToDeactivate = folderRepository.findALlByFolderIdIn(filesAndFolderIdsToDelete);
        if (hasValue(foldersToDeactivate)) {
            foldersToDeactivate.forEach(folder ->
                    folder.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.INACTIVE.value()));
            folderRepository.saveAll(foldersToDeactivate);
        }

        //Deactivate child folders and files of these child folders and files
        deactivateChildFilesAndFolders(filesAndFolderIdsToDelete);

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper();
    }

    @Override
    public BaseWrapper getNotesInRecycleBin() {
        final String TAG_METHOD_NAME = "getNotesInRecycleBin";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        //Fetch deleted notes
        Page<Note> deletedNotesPage = notesRepository.findAllByActiveStatusAndCreatedByOrderByModifiedTsDesc(ActiveStatus.INACTIVE.value(), authenticationService.fetchLoggedInUser().getUserId(), null);
        List<Note> deletedNotes;
        if (deletedNotesPage.hasContent())
            deletedNotes = deletedNotesPage.getContent();
        else
            deletedNotes = new ArrayList<>();
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), deletedNotes.toString());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(deletedNotes);
    }

    @Override
    public BaseWrapper recoverNoteFromRecycleBin(String noteId) throws ServicesException {
        final String TAG_METHOD_NAME = "recoverNoteFromRecycleBin";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass(), noteId);
        Note note = updateInactiveNoteActiveStatus(noteId, ActiveStatus.ACTIVE.value());

        //Recurrsively update active status of all parent folderds as well
        activateParentFolderTillTopLevel(note.getParentFolderId());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(noteId);
    }

    @Override
    public BaseWrapper archieveNote(String noteId) throws ServicesException {
        final String TAG_METHOD_NAME = "archieveNote";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass(), noteId);
        Note note = updateInactiveNoteActiveStatus(noteId, ActiveStatus.ARCHIEVE.value());

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(noteId);
    }

    @Override
    public BaseWrapper getFavouriteNotes() {
        final String TAG_METHOD_NAME = "getFavouriteNotes";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        //Fetch deleted notes
        Page<Note> favouriteNotesPage = notesRepository.findAllByActiveStatusAndCreatedByAndFavouriteOrderByModifiedTsDesc(ActiveStatus.ACTIVE.value(), authenticationService.fetchLoggedInUser().getUserId(), true, null);
        List<Note> favouriteNotes;
        if (favouriteNotesPage.hasContent())
            favouriteNotes = favouriteNotesPage.getContent();
        else
            favouriteNotes = new ArrayList<>();
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "favouriteNotes = " + favouriteNotes.toString());

        List<Note> sortedNotesList = new ArrayList<>();
        if (!favouriteNotes.isEmpty()) {
            //Define Comparator
            Comparator<Note> noteComparator = (n1, n2) -> {
                long n1PinnedTs = getValue(n1.getPinnedTs());
                long n2PinnedTs = getValue(n2.getPinnedTs());
                if (n1PinnedTs > n2PinnedTs)
                    return 1;
                if (n1PinnedTs < n2PinnedTs)
                    return -1;
                if (n1PinnedTs == n2PinnedTs)
                    return 0;
                return 0;
            };

            //Sort Pinned/Unpinned Notes
            List<Note> pinnedFavouriteNotes = favouriteNotes.stream().filter(Note::isPinned).collect(Collectors.toList());
            Collections.sort(pinnedFavouriteNotes, noteComparator);
            List<Note> unpinnedFavouriteNotes = favouriteNotes.stream().filter(note -> !note.isPinned()).collect(Collectors.toList());
            Collections.sort(unpinnedFavouriteNotes, noteComparator);

            //Prepare final List
            sortedNotesList.addAll(pinnedFavouriteNotes);
            sortedNotesList.addAll(unpinnedFavouriteNotes);
        }

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "sortedNotesList = " + sortedNotesList.toString());

        //Prepare response and send
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(sortedNotesList);
    }

    @Override
    public BaseWrapper markUmmarkFavouriteNote(String noteId, boolean markFavourite) throws ServicesException {
        final String TAG_METHOD_NAME = "markUmmarkFavouriteNote";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass(), "noteId = " + noteId + ", markFavourite = " + markFavourite);
        Note note = notesRepository.findByNoteId(noteId);
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        note.setFavourite(markFavourite);
        if (!markFavourite) {
            note.setPinned(false);
            note.setPinnedTs(null);
        }
        note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        notesRepository.save(note);

        //Prepare response and send
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(noteId);
    }

    @Override
    public BaseWrapper pinUnpinFavouriteNote(String noteId, boolean isPinned) throws ServicesException {
        final String TAG_METHOD_NAME = "pinUnpinFavouriteNote";
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass(), "noteId = " + noteId + ", isPinned = " + isPinned);
        Note note = notesRepository.findByNoteId(noteId);
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        note.setPinned(isPinned);
        note.setPinnedTs(isPinned ? System.currentTimeMillis() : null);
        note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        notesRepository.save(note);

        //Prepare response and send
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NAME, getClass(), System.currentTimeMillis());
        return new BaseWrapper(noteId);
    }

    @Override
    public BaseWrapper updateUtterance(String noteId, String uuid, int utteranceId, SingleValue<String> updatedUtterance) throws ServicesException {
        CommonUtility.validateInput(noteId, uuid, utteranceId, updatedUtterance);
        CommonUtility.validateInput(updatedUtterance.getTerm());

        Note note = notesRepository.findByNoteId(noteId);
        CommonUtility.validateInput(note);

        //Update utterance
        boolean isUtteranceUpdated = false;
        for (int transcibeIndex = 0; transcibeIndex < note.getTranscribes().size(); transcibeIndex++) {
            if (note.getTranscribes().get(transcibeIndex).getUuId().equals(uuid)) {
                List<UtteranceDetails> utterances = note.getTranscribes().get(transcibeIndex).getUtterances();
                for (int i = 0; i < utterances.size(); i++) {
                    if (utterances.get(i).getUtteranceId() == utteranceId) {
                        utterances.get(i).setUtterance(updatedUtterance.getTerm().trim());
                        break;
                    }
                }
                note.getTranscribes().get(transcibeIndex).setUtterances(utterances);
                note.getTranscribes().get(transcibeIndex).updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
                note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());

                notesRepository.save(note);

                isUtteranceUpdated = true;
                break;
            }
        }

        if (!isUtteranceUpdated)
            throw new ServicesException(704);

        return new BaseWrapper(updatedUtterance);
    }

    @Override
    public BaseWrapper updateMemo(String noteId, SingleValue<String> updatedMemo) throws ServicesException {
        CommonUtility.validateInput(noteId, updatedMemo);

        Note note = notesRepository.findByNoteId(noteId);
        CommonUtility.validateInput(note);

        //Update memo
        note.setMemo(updatedMemo.getTerm());
        note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        notesRepository.save(note);

        return new BaseWrapper(updatedMemo);
    }

    @Override
    public void updateNoteTranscribeProgressByUUID(String uuid, Float progress) throws ServicesException {
        Note note = getNoteByUUID(uuid);

        //Update transcribe progress by uuid
        boolean isTranscribeProgressUpdated = false;
        for (int transcibeIndex = 0; transcibeIndex < note.getTranscribes().size(); transcibeIndex++) {
            if (note.getTranscribes().get(transcibeIndex).getUuId().equals(uuid)) {
                note.getTranscribes().get(transcibeIndex).setRate(progress);
                note.getTranscribes().get(transcibeIndex).updateAuditableFields(false, GlobalConstants.CALLBACK_API, ActiveStatus.ACTIVE.value());
                note.updateAuditableFields(false, GlobalConstants.CALLBACK_API, ActiveStatus.ACTIVE.value());

                notesRepository.save(note);

                isTranscribeProgressUpdated = true;
                break;
            }
        }
    }


    @Override
    public void updateNoteTranscribeDetailsByUUIDAndFileName(String uuid, String outFileName, Float progress) throws ServicesException, IOException {
        final String TAG_METHOD_NM = "updateNoteTranscribeDetailsByUUIDAndFileName";
        final String CLASS_NM = getClass().getName();

        Note note = getNoteByUUID(uuid);

        //Update transcribe data
        boolean isTranscribeProgressUpdated = false;
        for (int transcibeIndex = 0; transcibeIndex < note.getTranscribes().size(); transcibeIndex++) {
            if (note.getTranscribes().get(transcibeIndex).getUuId().equals(uuid)) {
                log.debug(GlobalConstants.LOG.DEBUG, TAG_METHOD_NM, CLASS_NM, "Found Transcribe In Note with title: " + note.getTranscribes().get(transcibeIndex).getTitle());
                Transcribe transcribe = note.getTranscribes().get(transcibeIndex);
                transcribe.setRate(progress);
                transcribe.setResult(GlobalConstants.StringConstants.STT_CALBACK_RESULT_SUCCESS.toLowerCase());
                //Update outPath
                String inPath = transcribe.getInPath();
                String outPath = inPath.substring(0, (inPath.lastIndexOf("/") + 1)) + outFileName;
                transcribe.setOutPath(outPath);
                transcribe.setOutFileName(outFileName);

                //Set Transcribe Data
                noteHelper.updateTranscribeMetaDataAndUtterancesByOutPath(transcribe);

                //Update Auditable Fields
                transcribe.updateAuditableFields(false, GlobalConstants.CALLBACK_API, ActiveStatus.ACTIVE.value());

                //Update transcribe and auditable fields in note
                note.getTranscribes().set(transcibeIndex, transcribe);
                note.updateAuditableFields(false, GlobalConstants.CALLBACK_API, ActiveStatus.ACTIVE.value());
                notesRepository.save(note);

                isTranscribeProgressUpdated = true;
                break;
            }
        }
    }

    @Override
    public BaseWrapper fullTextSearch(String term, NoteTypes noteType, int pageNo, int size) throws ServicesException {
        if (!hasValue(term))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Get Logged In user ID
        String loggedInUserId = authenticationService.fetchLoggedInUser().getUserId();

        //Get All AUDIO types of notes data
        PaginatedDataVO<List<NoteCallSearchDTO>> notesSearchPaginatedVO = null;
        if (NoteTypes.ALL == noteType || NoteTypes.AUDIO == noteType) {
            PaginatedDataVO<List<Note>> notesPaginatedVO = notesRepository.searchNoteByTermAndNoteTypeAndCreatedBy(term, NoteTypes.AUDIO.name(), pageNo, size, loggedInUserId);
            //Prepare Note Search Response
            List<NoteCallSearchDTO> notesSearchesResponse = notesPaginatedVO.getData().stream().map(NoteCallSearchDTO::new).collect(Collectors.toList());
            notesSearchPaginatedVO = new PaginatedDataVO<>(notesSearchesResponse, notesPaginatedVO.getPaginationDetails());
        }

        //GetAll CALL type of notes data
        PaginatedDataVO<List<NoteCallSearchDTO>> callSearchPaginatedVO = null;
        if (NoteTypes.ALL == noteType || NoteTypes.CALLS == noteType) {
            PaginatedDataVO<List<Note>> callsPaginatedVO = notesRepository.searchNoteByTermAndNoteTypeAndCreatedBy(term, NoteTypes.CALLS.name(), pageNo, size, loggedInUserId);
            //Prepare Call Search Response
            List<NoteCallSearchDTO> callsSearchesResponse = callsPaginatedVO.getData().stream().map(NoteCallSearchDTO::new).collect(Collectors.toList());
            callSearchPaginatedVO = new PaginatedDataVO<>(callsSearchesResponse, callsPaginatedVO.getPaginationDetails());
        }

        //Prepare final response and send
        NoteSearchResponseDTO responseDTO = new NoteSearchResponseDTO(notesSearchPaginatedVO, callSearchPaginatedVO);

        return new BaseWrapper(responseDTO);
    }

    /**
     * Get note by any of it's tramscribe's UUID
     *
     * @param uuid
     * @return
     * @throws ServicesException
     */
    private Note getNoteByUUID(String uuid) throws ServicesException {
        Optional<Note> noteOptional = notesRepository.findByTranscribes_uuId(uuid);
        if (!noteOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_NOTE_UUID_NOT_SUPPLIED.value());

        return noteOptional.get();
    }

    /**
     * Update the active status of the Inactive Note ID
     *
     * @param noteId
     * @param activeStatusToUpdate
     * @return
     * @throws ServicesException
     */
    private Note updateInactiveNoteActiveStatus(String noteId, int activeStatusToUpdate) throws ServicesException {
        if (!hasValue(noteId))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        Note note = notesRepository.findByNoteIdAndActiveStatus(noteId, ActiveStatus.INACTIVE.value());
        if (!hasValue(note))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), activeStatusToUpdate);
        notesRepository.save(note);

        return note;
    }

    /**
     * Recurssively activate all parentFolders for given folderId
     *
     * @param parentFolderId
     */
    private void activateParentFolderTillTopLevel(String folderId) {
        if (hasValue(folderId) && !folderId.equals("0")) {
            Folder folder = folderRepository.findByFolderId(folderId);
            if (folder.getActiveStatus() != ActiveStatus.ACTIVE.value()) {
                folder.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
                folderRepository.save(folder);
            }

            activateParentFolderTillTopLevel(folder.getParentFolderId());
        }
    }

    /**
     * Delete all child files and folders recurrsively
     *
     * @param filesAndFolderIdsToDelete
     */
    private void deactivateChildFilesAndFolders(List<String> filesAndFolderIdsToDelete) {
        //Get All child folders And files for given ID's
        final List<String> childFilesAndFolderIdsToDelete = new ArrayList<>();

        //Deactivate All child files
        List<Note> childNotes = notesRepository.findAllByParentFolderIdInAndActiveStatus(filesAndFolderIdsToDelete, ActiveStatus.ACTIVE.value());
        if (hasValue(childNotes)) {
            childNotes.forEach(note -> {
                note.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.INACTIVE.value());
                childFilesAndFolderIdsToDelete.add(note.getNoteId());
            });
            notesRepository.saveAll(childNotes);
        }

        //Deactivate All child folders
        List<Folder> childFolders = folderRepository.findAllByParentFolderIdInAndActiveStatus(filesAndFolderIdsToDelete, ActiveStatus.ACTIVE.value());
        if (hasValue(childFolders)) {
            childFolders.forEach(folder -> {
                folder.updateAuditableFields(false, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.INACTIVE.value());
                childFilesAndFolderIdsToDelete.add(folder.getFolderId());
            });
            folderRepository.saveAll(childFolders);
        }

        //Deactivate child folders and files of these child folders and files
        if (hasValue(childFilesAndFolderIdsToDelete))
            deactivateChildFilesAndFolders(childFilesAndFolderIdsToDelete);
    }
}
