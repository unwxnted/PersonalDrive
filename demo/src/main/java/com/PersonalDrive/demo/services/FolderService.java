package com.PersonalDrive.demo.services;

import com.PersonalDrive.demo.models.Folder;
import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.repositories.FileRepository;
import com.PersonalDrive.demo.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    UserService userService;

    @Autowired
    FileRepository fileRepository;



    public Optional<Folder> findById(int id){
        return folderRepository.findById(id);
    }

    public void deleteById(int id){
        folderRepository.deleteById(id);
    }

    public void createFolder(Folder folder) {
        folderRepository.save(folder);
    }


    public Map<String, Object> getContentsByFolderId(int folderId, User user) {
        Map<String, Object> result = new HashMap<>();

        if (folderId == 0) {
            List<Map<String, Object>> folders = folderRepository.findAll().stream()
                    .filter(f -> f.getOwner().getId() == user.getId() && f.getParentFolder() == null)
                    .map(f -> {
                        Map<String, Object> folderMap = new HashMap<>();
                        folderMap.put("id", f.getId());
                        folderMap.put("name", f.getName());
                        return folderMap;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> files = fileRepository.findAll().stream()
                    .filter(f -> f.getUser().getId() == user.getId() && f.getFolder() == null)
                    .map(f -> {
                        Map<String, Object> fileMap = new HashMap<>();
                        fileMap.put("id", f.getId());
                        fileMap.put("name", f.getName());
                        return fileMap;
                    })
                    .collect(Collectors.toList());

            result.put("folders", folders);
            result.put("files", files);
        } else {

            Folder parentFolder = folderRepository.findById(folderId)
                    .filter(f -> f.getOwner().getId() == user.getId())
                    .orElseThrow(() -> new RuntimeException("Folder not found or unauthorized"));

            List<Map<String, Object>> subfolders = parentFolder.getSubFolders().stream()
                    .map(subfolder -> {
                        Map<String, Object> subfolderMap = new HashMap<>();
                        subfolderMap.put("id", subfolder.getId());
                        subfolderMap.put("name", subfolder.getName());
                        return subfolderMap;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> files = fileRepository.findAll().stream()
                    .filter(f -> f.getFolder() != null && f.getFolder().getId() == folderId)
                    .map(f -> {
                        Map<String, Object> fileMap = new HashMap<>();
                        fileMap.put("id", f.getId());
                        fileMap.put("name", f.getName());
                        return fileMap;
                    })
                    .collect(Collectors.toList());

            result.put("folders", subfolders);
            result.put("files", files);
        }

        return result;
    }


}
