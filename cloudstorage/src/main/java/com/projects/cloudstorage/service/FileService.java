package com.projects.cloudstorage.service;

import com.projects.cloudstorage.model.FileData;

import java.io.File;
import java.util.List;

public interface FileService {
    List<FileData> getAll();

    FileData findByFilename(String filename) throws Exception;

    void deleteFile(FileData fileData) throws Exception;

    void renameFile(String newFilename, String filename) throws Exception;

    void saveFile(String filename, File file) throws Exception;

    File getFile(String filename) throws Exception;
}
