package com.alinote.api.crons;

import com.alinote.api.constants.*;
import com.alinote.api.repository.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Slf4j
@Component
@Transactional(rollbackFor = Throwable.class)
public class AutoCleanStaleRecordsCron {


    @Value("${config.otp.clearindays}")
    private int otpClearInDays;
    @Value("${config.note.archieveindays}")
    private int archieveInDays;

    @Autowired
    private UserOtpRepository userOtpRepository;
    @Autowired
    private INotesRepository notesRepository;

    @Scheduled(zone = "IST", cron = "${cron.otp.clearmaxattempts}")
    public void clearOTPMaxAttempts() {
        final String TAG_METHOD_NM = "clearOTPMaxAttempts";
        final String CLASS_NAME = getClass().getName();
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NM, CLASS_NAME, System.nanoTime());

        try {
            //Get epoch time otpClearInDays day(s) before
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, (-1 * otpClearInDays));
            long timeToClearOtpAttemptsBefore = cal.getTimeInMillis();

            //Delete All OTP records before `timeToClearOtpAttemptsBefore`
            log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, CLASS_NAME, "Clearing OTP before = " + timeToClearOtpAttemptsBefore);
            userOtpRepository.deleteByCreatedTsBefore(timeToClearOtpAttemptsBefore);
            log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, CLASS_NAME, "Cleared OTP before = " + timeToClearOtpAttemptsBefore);
        } catch (Exception e) {
            log.error(GlobalConstants.LOG.ERROR, TAG_METHOD_NM, CLASS_NAME, e);
        }

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NM, CLASS_NAME, System.nanoTime());
    }

    @Scheduled(zone = "IST", cron = "${cron.notes.archieve}")
    public void archieveDeletedNotes() {
        final String TAG_METHOD_NM = "archieveDeletedNotes";
        final String CLASS_NAME = getClass().getName();
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NM, CLASS_NAME, System.nanoTime());

        try {
            //Get epoch time archieveInDays day(s) before
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, (-1 * archieveInDays));
            long timeToArchieveNotesBefore = cal.getTimeInMillis();

            //Archieve all deleted records before `timeToArchieveNotesBefore`
            log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, CLASS_NAME, "Archieving Deleted Notes before = " + timeToArchieveNotesBefore);
            notesRepository.archeivedAllDeleteNoteBeforeConfguredThreshold(timeToArchieveNotesBefore);
            log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, CLASS_NAME, "Archieved Deleted Notes before = " + timeToArchieveNotesBefore);
        } catch (Exception e) {
            log.error(GlobalConstants.LOG.ERROR, TAG_METHOD_NM, CLASS_NAME, e);
        }

        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NM, CLASS_NAME, System.nanoTime());
    }
}
