package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "file_hashes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileHashes {
    @Id
    private ObjectId id;        // MongoDB uses String for IDs by default
    private String fileHash;    // Store the file hash (generated during upload)
    private String fileUrl;     // Store the Cloudinary file URL

    private String userId;
    private String quizId;
    private String questionId;
    private int correctCount;
    private int falseCount;

    public FileHashes(String fileHash, String fileUrl) {
        this.fileHash = fileHash;
        this.fileUrl = fileUrl;
    }
}