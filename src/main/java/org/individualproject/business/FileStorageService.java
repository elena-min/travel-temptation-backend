package org.individualproject.business;

import org.individualproject.business.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, Long tripId) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path targetLocation = Paths.get(uploadDir).resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
    public String replaceFile(MultipartFile file, Long tripId, String oldFileName) throws IOException {
        Path oldFilePath = Paths.get(uploadDir).resolve(oldFileName);
        Files.deleteIfExists(oldFilePath);

        return storeFile(file, tripId);
    }
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("File not found " + fileName);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not load file " + fileName, ex);
        }
    }
}
