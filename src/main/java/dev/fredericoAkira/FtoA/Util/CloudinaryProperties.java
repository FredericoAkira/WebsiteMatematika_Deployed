package dev.fredericoAkira.FtoA.Util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties(prefix = "cloudinary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
}

