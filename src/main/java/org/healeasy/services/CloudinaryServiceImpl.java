package org.healeasy.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.healeasy.Iservices.ICloudinaryService;
import org.healeasy.exceptions.InvalidFileTypeException;
import org.healeasy.exceptions.LargeFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
    private final Cloudinary cloudinary;
    private final List<String> VALID_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if(file.getSize() > 3 * 1024 * 1024) {
            throw new LargeFileException("File size exceeds the limit of 3MB");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidFileTypeException("File is not an image");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || VALID_IMAGE_EXTENSIONS.stream().noneMatch(fileName.toLowerCase()::endsWith)) {
            throw new InvalidFileTypeException("Invalid image format. Supported formats are: " + VALID_IMAGE_EXTENSIONS);
        }
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
