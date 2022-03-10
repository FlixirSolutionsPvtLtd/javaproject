package com.alinote.api.domains;

import com.alinote.api.constants.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Document(collection = GlobalConstants.DocumentCollections.COLLECTION_APP_CONTENT)
public class AppContent extends Auditable implements Serializable {

    private static final long serialVersionUID = 3540174051867702594L;
    @Id
    private String id;

    @Field("app_content_id")
    private String appContentId;

    @Field("app_content")
    private String appContent;
}
