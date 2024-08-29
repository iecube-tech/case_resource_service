package com.iecube.community.ys;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ys")
public class YsConfig {

    private String host;
    private String appKey;
    private String secret;

}
