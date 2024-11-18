package com.PersonalDrive.demo.controllers;

import com.PersonalDrive.demo.models.Folder;
import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.security.JwtTokenProvider;
import com.PersonalDrive.demo.services.FileService;
import com.PersonalDrive.demo.services.FolderService;
import com.PersonalDrive.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @PostMapping("/create")
    public ResponseEntity<?> createFolder(@RequestParam("name") String name, @RequestParam("parentId") int parentId, HttpServletRequest req) {
        try {
            User user = userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req)));
            Folder folder = new Folder();
            folder.setName(name);
            folder.setOwner(user);

            Optional<Folder> parentFolder = folderService.findById(parentId);
            folder.setParentFolder(parentFolder.orElse(null));

            folderService.createFolder(folder);

            return ResponseEntity.ok().body("Folder Created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}/contents")
    public ResponseEntity<?> getFolderContents(@PathVariable("id") int folderId, HttpServletRequest req) {
        try {
            String username = jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req));
            User user = userService.getByName(username);

            Map<String, Object> contents = folderService.getContentsByFolderId(folderId, user);

            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int folderId, HttpServletRequest req){
        Optional<Folder> folder = folderService.findById(folderId);
        if(!folder.isPresent() || folder.get().getOwner().getId() != userService.getByName(jwtTokenProvider.getUserNameFromToken(jwtTokenProvider.resolveToken(req))).getId()){
            return ResponseEntity.badRequest().body("Folder Not Found");
        }
        folderService.deleteById(folderId);
        return ResponseEntity.ok().body("Folder Deleted");
    }

}
