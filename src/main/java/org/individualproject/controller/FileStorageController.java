package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import org.individualproject.business.ExcursionService;
import org.individualproject.business.FileStorageService;
import org.individualproject.domain.Excursion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/files")
public class FileStorageController {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);
    private FileStorageService fileStorageService;
    private ExcursionService excursionService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService, ExcursionService excursionService1)
    {
        this.fileStorageService = fileStorageService;
        this.excursionService = excursionService1;
    }
    @PostMapping("/upload/{excursionId}")
    @RolesAllowed({"TRAVELAGENCY", "ADMIN"})
    public ResponseEntity<?> uploadFile(@PathVariable Long excursionId, @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Upload request received for excursion ID: {}", excursionId);
            logger.info("File name: {}", file.getOriginalFilename());
            logger.info("File size: {} bytes", file.getSize());

            Optional<Excursion> excursionOptional = excursionService.getExcursion(excursionId);
            if (!excursionOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Excursion not found with id: " + excursionId);
            }

            Excursion event = excursionOptional.get();
            String oldFileName = event.getFileName();
            String fileName;

            if (oldFileName == null || oldFileName.isEmpty()) {
                fileName = fileStorageService.storeFile(file, excursionId);
            } else {
                fileName = fileStorageService.replaceFile(file, excursionId, oldFileName);
            }

            excursionService.updateExcursionPresentationFile(excursionId, fileName);
            Excursion updatedExcursion = excursionService.getExcursion(excursionId)
                    .orElseThrow(() -> new RuntimeException("Excursion not found after updating presentation file"));

            return ResponseEntity.ok(updatedExcursion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the file: " + e.getMessage());
        }
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
