package com.projects.cloudstorage.service.impl;

import com.projects.cloudstorage.model.FileData;
import com.projects.cloudstorage.model.Status;
import com.projects.cloudstorage.repository.FileRepository;
import com.projects.cloudstorage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional

public class FileServiceImpl implements FileService {

    @Value("${upload.path}")
    private String uploadPath;
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<FileData> getAll() {
        List<FileData> result = fileRepository.findAll();
        log.info("METHOD: getAll - {} files found", result.size());
        return result;
    }

    @Override
    public FileData findByFilename(String filename) throws Exception {
        if (filename.isEmpty()) {
            log.error("METHOD: findByFilename - requested filename is empty");
            throw new BadCredentialsException("requested filename is empty");
        }
        FileData result = fileRepository.findByFilename(filename);
        if (result.getFilename() == null || result.getSize() == 0) {
            log.error("METHOD: findByFilename - missing object data");
            throw new Exception("internal server error");
        }
        log.info("METHOD: findByFilename - file: {} found by filename: {}", result.getFilename(), filename);
        return result;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteFile(FileData fileData) throws Exception {
        if (new File(uploadPath, fileData.getFilename()).delete()) {
            fileRepository.delete(fileData);
            log.info("METHOD: deleteFile - file successfully deleted");
        } else {
            log.error("IN deleteFile - file deleteFile error");
            throw new Exception("file deleteFile error");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @Modifying
    public ResponseEntity<HttpStatus> renameFile(String newFilename, String filename) throws Exception {
        if (filename.isEmpty()) {
            log.error("METHOD: renameFile - new filename is empty");
            throw new BadCredentialsException("new filename is empty");
        }
        if (uploadPath.isEmpty()) {
            log.error("METHOD: renameFile - upload path does not exist");
            throw new Exception("internal server error");
        }
        File file = new File(uploadPath, filename);
        if (!file.exists()) {
            log.error("METHOD: renameFile - file does not exist");
            throw new BadCredentialsException("file does not exist");
        }
        if (file.renameTo(new File(uploadPath, newFilename))) {
            fileRepository.renameFile(newFilename, filename, new Date());
            log.info("METHOD: renameFile - file successfully renamed");
        } else {
            log.error("METHOD: renameFile - file rename error");
            throw new Exception("Failed to rename file");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<HttpStatus> saveFile(String filename, MultipartFile file) throws Exception {
        if (filename.isEmpty() || file.isEmpty()) {
            log.error("METHOD: saveFile - error input data");
            throw new BadCredentialsException("error input data");
        }
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
            log.info("METHOD: saveFile - directory created: " + uploadPath);
        }
        FileData fileData = new FileData();
        fileData.setFilename(filename);
        fileData.setSize((int) file.getSize());
        fileData.setUpdated(new Date());
        fileData.setCreated(new Date());
        fileData.setStatus(Status.ACTIVE);
        file.transferTo(new File(uploadPath + "/" + filename));
        fileRepository.save(fileData);
        log.info("METHOD: saveFile - file successfully saved");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<byte[]> getFile(String filename) throws Exception {
        FileInputStream fin = new FileInputStream(new File(uploadPath, filename));
        MultipartFile multipartFile = new MockMultipartFile(filename, fin);
        log.info("METHOD: getFile - file {} download", filename);
        return ResponseEntity.ok().contentLength(multipartFile.getSize()).body(multipartFile.getBytes());
    }
}
