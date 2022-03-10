package com.alinote.api.repository;

import com.alinote.api.domains.*;
import org.springframework.data.mongodb.repository.*;


public interface IAppContentRepository extends MongoRepository<AppContent, String> {
    AppContent findAppContentByAppContentId(String appContentId);
}
