package com.projects.cloudstorage.controller;

import com.projects.cloudstorage.ConfigConstants.Endpoints;
import com.projects.cloudstorage.JsonEntity.AuthenticationRequest;
import com.projects.cloudstorage.model.FileData;
import com.projects.cloudstorage.security.jwt.JwtTokenProvider;
import com.projects.cloudstorage.service.FileService;
import com.projects.cloudstorage.service.UserService;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class MainRestController {

    private final UserService userService;
    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public MainRestController(UserService userService, FileService fileService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.fileService = fileService;
        this.jwtTokenProvider = jwtTokenProvider;

        this.authenticationManager = authenticationManager;
    }

    //Проверка логина и пароля, присвоение токена пользователю
    @PostMapping(Endpoints.LOGIN_ENDPOINT)
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest authRequest) {
        String login = authRequest.getLogin();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, authRequest.getPassword()));
        String token = jwtTokenProvider.createToken(login, userService.findByLogin(login).getRoles());
        return ResponseEntity.ok(new JSONObject().put("auth-token", token).toString());
    }

    //?Предпологается выполнение logout,но на деле фронт отправляет запрос GET на login?logout
    @PostMapping(Endpoints.LOGOUT_ENDPOINT)
    public HttpStatus logoutPost() {
        return HttpStatus.OK;
    }

    //Действующий обработчик запроса logout
    @GetMapping(Endpoints.LOGIN_ENDPOINT)
    public HttpStatus logout() {
        return HttpStatus.OK;
    }

    //Загрузка файла на сервер и его регистрация в базе данных
    @PostMapping(value = Endpoints.FILE_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> file(@RequestParam("filename") String filename,
                                           @RequestBody MultipartFile file) throws Exception {
        return fileService.saveFile(filename, file);
    }

    //?Только имитация редактирования файла с помощью автогенерации нового имени файла?
    //Обработка запроса на изменение названия файла
    @PutMapping(Endpoints.FILE_ENDPOINT)
    public ResponseEntity<HttpStatus> putFile(@RequestParam("filename") String oldFilename, @RequestBody FileData newFilename) throws Exception {
        return fileService.renameFile(newFilename.getFilename(), oldFilename);
    }

    //Удаление файла
    @DeleteMapping(Endpoints.FILE_ENDPOINT)
    public ResponseEntity<HttpStatus> deleteFile(@RequestParam("filename") String filename) throws Exception {
        return fileService.deleteFile(fileService.findByFilename(filename));
    }

    //Возвращение файла с сервера
    //?Требуется ли удаление файла с сервера после возврата?
    //?Не получается полноценно возвращать ничего кроме текстовых файлов?
    @GetMapping(value = Endpoints.FILE_ENDPOINT, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getFile(@RequestParam("filename") String filename) throws Exception {
        return fileService.getFile(filename);
    }

    //?Зачем нужен лимит когда фронт поддерживает отображение большего количества файлов?
    //Отображение списка загруженных файлов
    @GetMapping(Endpoints.LIST_ENDPOINT)
    public ResponseEntity<List<FileData>> getList(@RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(fileService.getAll());
    }
}
