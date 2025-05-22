package org.healeasy.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CloudinaryService {

    @Value("${spring.cloudinary.name}")
    private String cloudName;

    @Value("${spring.cloudinary.api-key}")
    private String apiKey;

    @Value("${spring.cloudinary.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}