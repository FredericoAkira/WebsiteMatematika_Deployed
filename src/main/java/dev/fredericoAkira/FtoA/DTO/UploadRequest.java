package dev.fredericoAkira.FtoA.DTO;

import lombok.Data;

@Data
public class UploadRequest {
    private String filename;  // The name of the file being uploaded
    private String fileType;  // The file type (e.g., "image/png", "application/pdf")
}
