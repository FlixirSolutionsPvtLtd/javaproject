package com.alinote.api.model;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class FolderFilesCountDTO implements Serializable {
    private String id;
    private int filesCount;
}
