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
    private final List<String> VALID_DOCUMENT_EXTENSIONS = List.of("pdf", "doc", "docx", "txt", "rtf");

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
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
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "resource_type", "auto"
        ));
        return uploadResult.get("url").toString();
    }

    @Override
    public String uploadDocument(MultipartFile file) throws IOException {
        if(file.getSize() > extractMBsFromStrSize(maxFileSize)) {
            throw new LargeFileException("File size exceeds the limit of "+ maxFileSize);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || 
            (!file.getContentType().startsWith("image/") && 
             !file.getContentType().startsWith("application/") && 
             !file.getContentType().equals("text/plain"))) {
            throw new InvalidFileTypeException("File type not supported. Must be an image or document");
        }

        // Check if it's a valid document extension
        boolean isValidDocument = VALID_DOCUMENT_EXTENSIONS.stream()
            .anyMatch(ext -> fileName.toLowerCase().endsWith(ext));

        // Check if it's a valid image extension
        boolean isValidImage = VALID_IMAGE_EXTENSIONS.stream()
            .anyMatch(ext -> fileName.toLowerCase().endsWith(ext));

        if (!isValidDocument && !isValidImage) {
            throw new InvalidFileTypeException("Invalid file format. Supported formats are: " + 
                VALID_IMAGE_EXTENSIONS + " and " + VALID_DOCUMENT_EXTENSIONS);
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "resource_type", "auto"
        ));
        return uploadResult.get("url").toString();
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
