package dev.fredericoAkira.FtoA.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.DTO.CompleteUploadRequest;
import dev.fredericoAkira.FtoA.DTO.UploadRequest;
import dev.fredericoAkira.FtoA.Service.CloudinaryService;
import dev.fredericoAkira.FtoA.Service.FileStorageService;
import dev.fredericoAkira.FtoA.Util.HashUtils;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    private FileStorageService uploadRecordService;
    private CloudinaryService cloudinaryService;

    public FileController(FileStorageService uploadRecordService, CloudinaryService cloudinaryService) {
        this.uploadRecordService = uploadRecordService;
        this.cloudinaryService = cloudinaryService;
    }


    @PostMapping("/sign")
    public ResponseEntity<?> signUpload(@RequestBody UploadRequest request) {
        try {
            String fileHash = HashUtils.generateHash(request.getFilename() + ":" + request.getFileType());

            // 2. Check DB for existing uploaded URL
            String existingUrl = uploadRecordService.getUrlByHash(fileHash);
            if (existingUrl != null) {
                return ResponseEntity.ok(Map.of(
                    "alreadyUploaded", true,
                    "fileUrl", existingUrl
                ));
            }

            // 3. Generate Cloudinary signature
            long timestamp = System.currentTimeMillis() / 1000;
            String publicId = "uploads/" + fileHash;
            Map<String, Object> signedParams = cloudinaryService.generateSignedUploadParams(publicId, timestamp);

            // 4. Return the signed upload data to frontend
            return ResponseEntity.ok(Map.of(
                "alreadyUploaded", false,
                "uploadData", signedParams
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/complete-upload")
    public ResponseEntity<?> completeUpload(@RequestBody CompleteUploadRequest request) {
        try {
            // 1. Save the uploaded file URL and hash in the database (MongoDB)
            uploadRecordService.saveUploadRecord(request.getFileHash(), request.getFileUrl());

            return ResponseEntity.ok(Map.of("message", "File uploaded successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
