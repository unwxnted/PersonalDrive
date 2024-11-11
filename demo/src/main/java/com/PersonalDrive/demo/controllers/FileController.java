package com.PersonalDrive.demo.controllers;

import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.repositories.FileRepository;
import com.PersonalDrive.demo.security.JwtTokenProvider;
import com.PersonalDrive.demo.services.FileService;
import com.PersonalDrive.demo.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${upload.dir}")
    private String UPLOAD_DIR;

    @Autowired
    FileService fileService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<com.PersonalDrive.demo.models.File> getAll(HttpServletRequest req){
        User user = userService.getById(userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req))).getId());
        List<com.PersonalDrive.demo.models.File> response = fileService.getAll(user);
        response.stream().forEach(f-> f.getUser().setPassword(null));
        return response;
    }

    @GetMapping("/name")
    public ResponseEntity<com.PersonalDrive.demo.models.File> getFileByName(@RequestParam("name") String name, HttpServletRequest req) {
        Optional<com.PersonalDrive.demo.models.File> optionalFile = fileService.getByName(name);

        if (optionalFile.isPresent() && optionalFile.get().getUser().getId() == userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req))).getId()) {
            optionalFile.get().getUser().setPassword(null);
            return ResponseEntity.ok(optionalFile.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") int id, HttpServletRequest req) {
        try {
            Optional<com.PersonalDrive.demo.models.File> optionalFile = fileService.getById(id);

            if (optionalFile.isEmpty() || optionalFile.get().getUser().getId() != userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req))).getId()) {
                return ResponseEntity.notFound().build();
            }

            com.PersonalDrive.demo.models.File file = optionalFile.get();
            String filename = file.getName();
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not download the file", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest req) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }

        try{
            File dest = new File(UPLOAD_DIR + file.getOriginalFilename());
            if (!dest.exists()) {
                dest.mkdirs();
            }
            file.transferTo(dest);
            com.PersonalDrive.demo.models.File reqFile = new com.PersonalDrive.demo.models.File();
            reqFile.setName(file.getOriginalFilename());
            User user = userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req)));
            reqFile.setUser(user);
            fileService.save(reqFile);
            return ResponseEntity.ok().body("File uploaded");
        }catch (IOException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("id") int id, HttpServletRequest req) {
        Optional<com.PersonalDrive.demo.models.File> file = fileService.getById(id);
        if(!file.isPresent() || file.get().getUser().getId() != userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req))).getId()) {
            return  ResponseEntity.notFound().build();
        }

        fileService.delete(file.get());
        File dest = new File(UPLOAD_DIR + file.get().getName());
        if(dest.exists()) dest.delete();
        return ResponseEntity.ok().build();
    }

}
