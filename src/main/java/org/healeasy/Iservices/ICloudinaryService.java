package org.healeasy.Iservices;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteImage(String publicId) throws IOException;
}
