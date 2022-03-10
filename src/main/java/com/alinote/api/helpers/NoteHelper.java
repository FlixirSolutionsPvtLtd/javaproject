package com.alinote.api.helpers;

import com.alinote.api.config.*;
import com.alinote.api.constants.*;
import com.alinote.api.domains.*;
import com.alinote.api.enums.*;
import com.alinote.api.model.*;
import com.alinote.api.model.webhook.*;
import com.alinote.api.security.service.*;
import com.amazonaws.auth.*;
import com.amazonaws.regions.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;

import static com.alinote.api.utility.CheckUtil.*;

@Slf4j
@Component
public class NoteHelper {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AWSConfigurationDetails awsConfigurationDetails;

    public void updateTranscribeMetaDataAndUtterancesByOutPath(Transcribe transcribe) throws IOException {
        final String TAG_METHOD_NM = "updateTranscribeMetaDataAndUtterancesByOutPath";
        final String CLASS_NM = getClass().getName();
        log.info(GlobalConstants.LOG.ENTRY, TAG_METHOD_NM, CLASS_NM, System.currentTimeMillis());

        //Create Speakers
        //Add Default current speaker as "ME"
        SpeakerDetails s1 = new SpeakerDetails(999, "Me", false, null, "M");

        List<SpeakerDetails> speakersList = new ArrayList<>();
        speakersList.add(s1);

        int utteranceId = 1;
        List<UtteranceDetails> utterances = new ArrayList<>();

        //Grant Public ACL to the file being read
        //Create S3 Object
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsConfigurationDetails.getAccesskey(), awsConfigurationDetails.getSecretkey());
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
        log.debug(GlobalConstants.LOG.DEBUG, TAG_METHOD_NM, CLASS_NM, "Out File name = " + transcribe.getOutFileName());

        //Set PublicRead ACL on the outFile before downloading
        //TODO: Get the file using pre-signed URL for better security
        s3.setObjectAcl(awsConfigurationDetails.getBucket(), transcribe.getOutFileName(), CannedAccessControlList.PublicRead);

        //Read contents from outPath line by line
        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NM, CLASS_NM, "Getting file content from: " + transcribe.getOutPath());
        List<String> outPathFileContents = IOUtils.readLines((new URL(transcribe.getOutPath()).openConnection().getInputStream()), StandardCharsets.UTF_8);
        for (String line : outPathFileContents) {
            log.debug("line: {}", line);
            TranscribeLineDetailsDTO transcribeLineDetailsDTO = getTranscribeLineDetails(line);
            log.debug("transcribeLineDetailsDTO = {}", hasValue(transcribeLineDetailsDTO) ? transcribeLineDetailsDTO.toString() : null);
            if (hasValue(transcribeLineDetailsDTO)) {
                int speakerId = transcribeLineDetailsDTO.getSpeakerSequence() + 1;

                //Add speaker if not exists
                addSpeakers(speakerId, speakersList);

                //Create Utterance
                UtteranceDetails utteranceDetails = new UtteranceDetails(utteranceId, speakerId, transcribeLineDetailsDTO.getUtteranceStartTime(), transcribeLineDetailsDTO.getUtterance());
                //Increment Utterance ID for next utterance
                ++utteranceId;
                utterances.add(utteranceDetails);
            }
        }

        //Create Metadata from speakers list
        TranscribeMetaData metaData = new TranscribeMetaData(speakersList);
        transcribe.setMetaData(metaData);

        //Create final transcribe data
        transcribe.setUtterances(utterances);
        log.info(GlobalConstants.LOG.EXIT, TAG_METHOD_NM, CLASS_NM, System.currentTimeMillis());
    }

    public Transcribe getStaticTranscribeData(String uuid, NoteCreateDTO noteRequest) {
        //Create Speakers
        SpeakerDetails me = new SpeakerDetails(999, "Me", false, null, "M");
        SpeakerDetails s1 = new SpeakerDetails(1, "Speaker 1", true, null, "S1");
        SpeakerDetails s2 = new SpeakerDetails(2, "Speaker 2", true, null, "S2");
        SpeakerDetails s3 = new SpeakerDetails(3, "Speaker 3", true, null, "S3");

        //Create Metadata Info
        List<SpeakerDetails> speakersList = List.of(me, s1, s2, s3);
        TranscribeMetaData metaData = new TranscribeMetaData(speakersList);

        //Create Transcribe Data
        UtteranceDetails t1 = new UtteranceDetails(1, 2, "00:01", "Utterance 1");
        UtteranceDetails t2 = new UtteranceDetails(2, 3, "00:05", "Utterance 2");
        UtteranceDetails t3 = new UtteranceDetails(3, 1, "00:12", "Utterance 3");
        UtteranceDetails t4 = new UtteranceDetails(4, 2, "00:17", "Utterance 4");
        UtteranceDetails t5 = new UtteranceDetails(5, 3, "00:21", "Utterance 5");
        UtteranceDetails t6 = new UtteranceDetails(6, 3, "01:02", "Utterance 6");
        UtteranceDetails t7 = new UtteranceDetails(7, 2, "01:01", "Utterance 7");
        UtteranceDetails t8 = new UtteranceDetails(8, 3, "01:05", "Utterance 8");
        UtteranceDetails t9 = new UtteranceDetails(9, 1, "01:12", "Utterance 9");
        UtteranceDetails t10 = new UtteranceDetails(10, 2, "01:17", "Utterance 10");
        UtteranceDetails t11 = new UtteranceDetails(11, 3, "01:21", "Utterance 11");
        UtteranceDetails t12 = new UtteranceDetails(12, 3, "02:02", "Utterance 12");
        //Create the static transcribe Data and return
        String fileName = noteRequest.getFileName();
        Transcribe tr1 = new Transcribe(fileName, uuid, noteRequest.getInPath(),
                "S3 Bucket Out Path", fileName, 0f, metaData, List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12));
        tr1.updateAuditableFields(true, authenticationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());

        return tr1;
    }


    public void addSpeakers(int speakerId, List<SpeakerDetails> speakerDetails) {
        SpeakerDetails speaker = speakerDetails.stream().filter(s -> s.getSpeakerId() == speakerId).findFirst().orElse(null);
        if (!hasValue(speaker)) {
            int nextSpeakerId = speakerDetails.size() <= 1 ? 1 : getNextSpeakerId(speakerDetails);

            String newSpeakerName = getSpeakerNameById(nextSpeakerId);

            speakerDetails.add(new SpeakerDetails(nextSpeakerId, newSpeakerName, true, null, getInitials(newSpeakerName)));
        }
    }

    private String getInitials(String newSpeakerName) {
        String[] speakerNames = newSpeakerName.trim().split(" ");
        String firstInitial = speakerNames[0].substring(0, 1);
        return speakerNames.length > 1 ? firstInitial + speakerNames[speakerNames.length - 1].substring(0, 1) : firstInitial;
    }

    private String getSpeakerNameById(int nextSpeakerId) {
        return GlobalConstants.StringConstants.SPEAKER_DEFAULT_PREFIX + " " + nextSpeakerId;
    }

    private int getNextSpeakerId(List<SpeakerDetails> speakerDetails) {
        List<Integer> speakerIds = speakerDetails.stream().map(SpeakerDetails::getSpeakerId).collect(Collectors.toList());
        Collections.sort(speakerIds);

        return (speakerIds.get(speakerIds.size() - 1) + 1);
    }

    public TranscribeLineDetailsDTO getTranscribeLineDetails(String line) {
        final String TAG_METHOD_NAME = "getTranscribeLineDetails";
        final String CLASS_NM = getClass().getName();
        TranscribeLineDetailsDTO transcribeLineDetailsDTO = null;

        if (hasValue(line)) {
            String lineBreaks[] = line.split(GlobalConstants.StringConstants.STT_SMI_DELIMITER);

            transcribeLineDetailsDTO = new TranscribeLineDetailsDTO();
            transcribeLineDetailsDTO.setUtteranceSequence(Integer.parseInt(lineBreaks[0]));
            transcribeLineDetailsDTO.setSpeakerSequence(Integer.parseInt(lineBreaks[1]));

            SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConstants.StringConstants.UTTERANCE_TIME_FORMAT_PATTERN);
            SimpleDateFormat dateParser = new SimpleDateFormat(GlobalConstants.StringConstants.STT_UTTERANCE_TIME_FORMAT_PATTERN);

            String utteranceStartTime = lineBreaks[2];
            try {
                utteranceStartTime = dateFormat.format(dateParser.parse(lineBreaks[2]));
            } catch (Exception e) {
                log.error("Error parsing time of {} from line = {}", utteranceStartTime, line);
            }
            transcribeLineDetailsDTO.setUtteranceStartTime(utteranceStartTime);

            transcribeLineDetailsDTO.setUtterance(lineBreaks[3]);
            transcribeLineDetailsDTO.setConfidenceValue(Float.parseFloat(lineBreaks[4]));
        }

        return transcribeLineDetailsDTO;
    }
}
