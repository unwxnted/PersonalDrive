package com.PersonalDrive.demo;

import com.PersonalDrive.demo.models.File;
import com.PersonalDrive.demo.repositories.FileRepository;
import com.PersonalDrive.demo.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByName(){
        File mockFile = new File();
        mockFile.setId(1);
        mockFile.setName("file");

        when(fileRepository.getByName("file")).thenReturn(List.of(mockFile));

        Optional<File> file = fileService.getByName("file");

        assertNotNull(file);
    }

}
