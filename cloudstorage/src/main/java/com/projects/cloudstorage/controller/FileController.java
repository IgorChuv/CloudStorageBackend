package com.projects.cloudstorage.controller;

import com.projects.cloudstorage.constants.Endpoints;
import com.projects.cloudstorage.model.FileData;
import com.projects.cloudstorage.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    //Загрузка файла на сервер и его регистрация в базе данных
    @PostMapping(value = Endpoints.FILE_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> uploadFile(@RequestParam("filename") String filename,
                                                 @RequestPart MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        File newFile = new File(uploadPath, filename);
        file.transferTo(newFile);
        fileService.saveFile(filename, newFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Обработка запроса на изменение названия файла
    @PutMapping(Endpoints.FILE_ENDPOINT)
    public ResponseEntity<HttpStatus> renameFile(@RequestParam("filename") String oldFilename, @RequestBody FileData newFilename) throws Exception {
        fileService.renameFile(newFilename.getFilename(), oldFilename);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    //Удаление файла
    @DeleteMapping(Endpoints.FILE_ENDPOINT)
    public ResponseEntity<HttpStatus> deleteFile(@RequestParam("filename") String filename) throws Exception {
        fileService.deleteFile(fileService.findByFilename(filename));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Возвращение файла с сервера
    @GetMapping(value = Endpoints.FILE_ENDPOINT, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getFile(@RequestParam("filename") String filename) throws Exception {
        FileInputStream fin = new FileInputStream(fileService.getFile(filename));
        MultipartFile multipartFile = new MockMultipartFile(filename, fin);
        return ResponseEntity.ok().contentLength(multipartFile.getSize()).body(multipartFile.getBytes());
    }

    //Отображение списка загруженных файлов
    @GetMapping(Endpoints.LIST_ENDPOINT)
    public ResponseEntity<List<FileData>> getList(@RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(fileService.getAll());
    }
}
