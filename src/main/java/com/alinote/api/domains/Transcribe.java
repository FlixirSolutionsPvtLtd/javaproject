package com.alinote.api.domains;

import com.alinote.api.model.webhook.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class Transcribe extends Auditable implements Serializable {
    private static final long serialVersionUID = -1783582344283786481L;

    private String title;

    @Field("uu_id")
    private String uuId;

    @Field("in_path")
    private String inPath;

    @Field("out_path")
    private String outPath;

    @Field("file_name")
    private String fileName;

    @Field("out_file_name")
    private String outFileName;

    private Float rate;

    private TranscribeMetaData metaData;

    private List<UtteranceDetails> utterances;

    private String result;
    private String errCode;
    private String errMsg;

    public Transcribe(String title, String uuId, String inPath, String outPath, String fileName,
                      Float rate, TranscribeMetaData metaData, List<UtteranceDetails> utterances) {
        this.title = title;
        this.uuId = uuId;
        this.inPath = inPath;
        this.outPath = outPath;
        this.fileName = fileName;
        this.rate = rate;
        this.metaData = metaData;
        this.utterances = utterances;
    }

    public Transcribe(String title, String uuId, String inPath, String fileName, Float rate, STTCallBackDTO sttCallBackDTO) {
        this.title = title;
        this.uuId = uuId;
        this.inPath = inPath;
        this.fileName = fileName;
        this.rate = rate;
        this.result = sttCallBackDTO.getResult();
        this.errCode = sttCallBackDTO.getErrCode();
        this.errMsg = sttCallBackDTO.getErrMsg();
    }

//    public Transcribe(String outPath, String fileName,
//                      Float rate, TranscribeMetaData metaData, List<UtteranceDetails> utterances) {
//        this.title = title;
//        this.uuId = uuId;
//        this.inPath = inPath;
//        this.outPath = outPath;
//        this.fileName = fileName;
//        this.rate = rate;
//        this.metaData = metaData;
//        this.utterances = utterances;
//    }
}
