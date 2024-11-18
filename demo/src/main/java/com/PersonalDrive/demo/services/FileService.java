package com.PersonalDrive.demo.services;

import com.PersonalDrive.demo.models.File;
import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.models.Folder;
import com.PersonalDrive.demo.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired FileRepository fileRepository;

    @Autowired
    UserService userService;

    public Optional<com.PersonalDrive.demo.models.File> getByName(String name) {
        return fileRepository.getByName(name).stream().findFirst();
    }

    public Optional<File> getById(int id) {
        return fileRepository.findById(id);
    }

    public void save(com.PersonalDrive.demo.models.File file) {
        fileRepository.save(file);
    }

    public void delete(com.PersonalDrive.demo.models.File file) {
        fileRepository.delete(file);
    }

}
