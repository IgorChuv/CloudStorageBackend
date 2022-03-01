package com.projects.cloudstorage.service.impl;

import com.projects.cloudstorage.model.FileData;
import com.projects.cloudstorage.model.Status;
import com.projects.cloudstorage.repository.FileRepository;
import com.projects.cloudstorage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
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
    public void deleteFile(FileData fileData) throws Exception {
        if (new File(uploadPath, fileData.getFilename()).delete()) {
            fileRepository.delete(fileData);
            log.info("METHOD: deleteFile - file successfully deleted");
        } else {
            log.error("IN deleteFile - file deleteFile error");
            throw new Exception("file deleteFile error");
        }
    }

    @Override
    @Modifying
    public void renameFile(String newFilename, String filename) throws Exception {
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
    }

    @Override
    public void saveFile(String filename, File file) {
        if (filename.isEmpty()) {
            log.error("METHOD: saveFile - error input data");
            throw new BadCredentialsException("error input data");
        }
        FileData fileData = new FileData();
        fileData.setFilename(filename);
        fileData.setSize((int) file.length());
        fileData.setUpdated(new Date());
        fileData.setCreated(new Date());
        fileData.setStatus(Status.ACTIVE);
        fileRepository.save(fileData);
        log.info("METHOD: saveFile - file successfully saved");
    }

    @Override
    public File getFile(String filename) {
        File file = new File(uploadPath, filename);
        log.info("METHOD: getFile - file {} download", filename);
        return file;
    }
}
