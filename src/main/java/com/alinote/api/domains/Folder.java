package com.alinote.api.domains;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Folder extends Auditable {

    @Id
    private String folderId;
    private String name;
    @Field("parent_folder_id")
    private String parentFolderId;
    @Field("no_of_files")
    private String noOfFiles;
}
