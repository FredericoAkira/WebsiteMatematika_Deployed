package dev.fredericoAkira.FtoA.DTO;

import lombok.Data;

@Data
public class CompleteUploadRequest {
    private String fileHash;  // The hash of the file (used to identify it)
    private String fileUrl;   // The URL of the uploaded file (from Cloudinary)
}
