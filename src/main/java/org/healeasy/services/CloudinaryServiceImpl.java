package org.healeasy.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Getter;
import org.healeasy.Iservices.ICloudinaryService;
import org.healeasy.exceptions.InvalidFileTypeException;
import org.healeasy.exceptions.LargeFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
public class CloudinaryServiceImpl implements ICloudinaryService {
    private final Cloudinary cloudinary;
    private final List<String> VALID_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Value("${spring.server.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.server.multipart.max_request_size}")
    private String maxRequestSize;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public int extractMBsFromStrSize(String sizeStr){
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(sizeStr);
        if(matcher.find()){
            return Integer.parseInt(matcher.group()) * 1024 * 1024;
        }
        throw new IllegalArgumentException("No numeric value found in: " + sizeStr);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if(file.getSize() > extractMBsFromStrSize(maxFileSize)) {
            throw new LargeFileException("File size exceeds the limit of "+ maxFileSize);
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
