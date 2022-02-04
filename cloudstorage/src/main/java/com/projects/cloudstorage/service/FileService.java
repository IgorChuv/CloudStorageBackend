package com.projects.cloudstorage.service;

import com.projects.cloudstorage.model.FileData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileData> getAll();

    FileData findByFilename(String filename) throws Exception;

    ResponseEntity<HttpStatus> deleteFile(FileData fileData) throws Exception;

    ResponseEntity<HttpStatus> renameFile(String newFilename, String filename) throws Exception;

    ResponseEntity<HttpStatus> saveFile(String filename, MultipartFile file) throws Exception;

    ResponseEntity<byte[]> getFile(String filename) throws Exception;
}
