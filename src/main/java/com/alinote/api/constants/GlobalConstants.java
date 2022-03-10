package com.alinote.api.constants;

import java.util.*;

public class GlobalConstants {

    private GlobalConstants() {
    }

    public static final String USER_API = "API";
    public static final String CALLBACK_API = "CALLBACK_API";
    public static final String OTP_TARGET = "target";
    public static final int OTP_ALLOWED_MAX_ATTEMPTS = 10;
    public static final int OTP_LENGTH = 6;
    public static final String GENERIC_ERROR_MSSG = "Something went wrong. Please try after sometime";
    public static final String NOTE_ID = "noteId";
    public static final String UUID = "uuid";
    public static final String PARENT_FOLDER_ID = "parentFolderId";
    public static final String FOLDER_ID = "folderId";
    public static final String AUTH_USER_EMAIL = "email";

    public static final List<String> successCallbackResults = Collections.unmodifiableList(Arrays.asList(
            StringConstants.STT_CALBACK_RESULT_START.toLowerCase(),
            StringConstants.STT_CALBACK_RESULT_READY.toLowerCase(),
            StringConstants.STT_CALBACK_RESULT_RUNNING.toLowerCase(),
            StringConstants.STT_CALBACK_RESULT_PROCESSING.toLowerCase(),
            StringConstants.STT_CALBACK_RESULT_SUCCESS.toLowerCase(),
            StringConstants.STT_CALBACK_RESULT_DONE.toLowerCase()
    ));

    /**
     * Holds DB related constants
     */
    public final class DocumentCollections {

        private DocumentCollections() {
        }

        public static final String COLLECTION_USERS = "users";
        public static final String COLLECTION_USER_OTP = "user_otp";
        public static final String COLLECTION_NOTE = "notes";
        public static final String COLLECTION_TRANSCRIBE = "transcribe";
        public static final String COLLECTION_TRANSCRIBE_METADATA = "transcribe_metadata";
        public static final String COLLECTION_SPEAKER_DETAILS = "speaker_details";
        public static final String COLLECTION_TRANSCRIBE_UTTERANCE = "utterances";
        public static final String COLLECTION_FOLDER = "folder";

        public static final String COLLECTION_APP_CONTENT = "app_content";
    }

    /**
     * Holds the constants associated with Pagination logic
     */
    public class Pagination {
        private Pagination() {
        }

        public static final String PAGE_SIZE = "size";
        public static final String PAGE_NO = "pageNo";
    }

    /**
     * Holds the constants associated with Logging
     */
    public final class LOG {
        private LOG() {
        }

        public static final String ENTRY = "Entering method {} in class {} at {}";
        public static final String EXIT = "Exiting method {} in class {} at {}";
        public static final String INFO = "Calling method {} in class {} and data {}";
        public static final String DEBUG = "Calling method {} in class {} and data {}";
        public static final String ERROR = "Error in method {} in class {}";
    }

    /**
     * Hold all the constants associated with the Login workflow
     */
    public class LoginConstants {
        private LoginConstants() {
        }

        public static final String QUERY_PARAMS_LOGIN = "?username={0}&password={1}&grant_type={2}";
        public static final String HEADER_AUTHORIZATION = "Authorization";
        public static final String BASIC_AUTH_TOKEN = "Basic b2F1dGhkYjpvYXV0aGRiLXNlY3JldA==";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String GRANT_TYPE = "grant_type";
        public static final String USERNAME_DELIMITER = "#_#";
    }

    /**
     * Hold all the String constants associated with the application
     */
    public class StringConstants {
        private StringConstants() {
        }

        public static final String SEARCH_TERM = "term";
        public static final String STT_CALBACK_RESULT_READY = "ready";
        public static final String STT_CALBACK_RESULT_START = "start";
        public static final String STT_CALBACK_RESULT_RUNNING = "running";
        public static final String STT_CALBACK_RESULT_PROCESSING = "processing";
        public static final String STT_CALBACK_RESULT_SUCCESS = "success";
        public static final String STT_CALBACK_RESULT_DONE = "done";
        public static final String STT_CALBACK_RESULT_FAIL = "fail";
        public static final String SPEAKER_DEFAULT_PREFIX = "Speaker";
        public static final String STT_SMI_DELIMITER = ";";
        public static final String UTTERANCE_TIME_FORMAT_PATTERN = "HH:mm:ss";
        public static final String STT_UTTERANCE_TIME_FORMAT_PATTERN = "HH:mm:ss.SSS";
    }
}
