package com.alinote.api.constants;

public final class RestUrlConstants {

    private RestUrlConstants() {
    }


    //API Endpoints Baseurl
    public static final String PUBLIC = "/public";
    public static final String LOGIN = "/login";
    public static final String NOTES = "/notes";
    public static final String USERS = "/users";

    //Public API Endpoints
    public static final String PING = "/ping";
    public static final String REGISTER = "/register";
    public static final String AN_SOCIAL_HANDLES_GOOGLE_HANDLE = "/social-handles/google-handle";
    public static final String AN_SEND_OTP = "/sendotp";
    public static final String AN_VERIFY_OTP = "/verifyotp";
    public static final String AN_RESET_PASSWORD = "/resetpassword";
    public static final String STATIC_CONTENT = "/content/static";

    //Notes API EndPoints
    public static final String UUID = "/{uuid}";
    public static final String FOLDER_DETAILS = "/{parentFolderId}/folder";
    public static final String UPDATE_FOLDER = FOLDER_DETAILS + "/{folderId}";
    public static final String NOTES_TRANSCRIBE_PROGRESS = "/{noteId}/progress" + UUID;
    public static final String NOTES_DETAILS = "/{noteId}";
    public static final String NOTES_GET_NOTES_AND_FOLDERS_BY_PARENT_FOLDER_ID = "/{parentFolderId}/all";
    public static final String UPDATE_UTTERANCE = NOTES_DETAILS + "/transcribes" + UUID + "/utterances/{utteranceId}";
    public static final String UPDATE_MEMO = NOTES_DETAILS + "/memo";
    public static final String FULL_TEXT_SEARCH = "/search";

    //User Profile API Endpoints
    public static final String STATUS_UPDATE = "/status";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String ARCHIEVE_ACCOUNT = "/archieve-account";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String CHANGE_CNTC_NO = "/change-no";
    public static final String USER_PRFL_IMG = "/profile-image";

    //Recycle Bin API Endpoints
    public static final String RECYCLE_BIN = "/recycle-bin";
    public static final String RECOVER_NOTE = NOTES_DETAILS + "/recover";
    public static final String ARCHIEVE_NOTE = NOTES_DETAILS + "/archieve";

    //Favourites API Endpoints
    public static final String FAVOURITE_NOTES = "/favourites";
    public static final String MARK_UNMARK_FAVOURITE_NOTE = NOTES_DETAILS + FAVOURITE_NOTES + "/{markFavourite}";
    public static final String PIN_UNPIN_FAVOURITE_NOTE = NOTES_DETAILS + FAVOURITE_NOTES + "/pin/{isPinned}";

    //STT Engine Callback API Endpoints
    public static final String WEBHOOK_STT_CALLBACK = "/processing/results";
}
