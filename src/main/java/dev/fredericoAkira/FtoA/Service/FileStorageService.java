package dev.fredericoAkira.FtoA.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Entity.FileHashes;
import dev.fredericoAkira.FtoA.Repository.FileHashRepository;

@Service
public class FileStorageService {

    private FileHashRepository uploadRecordRepository;

    @Autowired
    public void UploadRecordService(FileHashRepository uploadRecordRepository) {
        this.uploadRecordRepository = uploadRecordRepository;
    }

    // Check if the file already exists by hash
    public String getUrlByHash(String fileHash) {
        Optional<FileHashes> existingRecord = uploadRecordRepository.findByFileHash(fileHash);
        return existingRecord.map(FileHashes::getFileUrl).orElse(null);
    }

    // Save a new upload record
    public FileHashes saveUploadRecord(String fileHash, String fileUrl) {
        FileHashes newRecord = new FileHashes(fileHash, fileUrl);
        return uploadRecordRepository.save(newRecord);
    }
}
