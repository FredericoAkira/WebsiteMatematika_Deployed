package dev.fredericoAkira.FtoA.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    public CloudinaryService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    public Map<String, Object> generateSignedUploadParams(String publicId, long timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("public_id", publicId);

        String signature = cloudinary.apiSignRequest(params, apiSecret);

        Map<String, Object> result = new HashMap<>();
        result.put("signature", signature);
        result.put("public_id", publicId);
        result.put("timestamp", timestamp);
        result.put("api_key", apiKey);
        result.put("cloud_name", cloudName);

        return result;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getCloudName() {
        return cloudName;
    }
}
