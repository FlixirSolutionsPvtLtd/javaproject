package com.alinote.api.config;

import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AWSConfigurationDetails {

    String accesskey;
    String secretkey;
    String bucket;
}
