package com.PersonalDrive.demo.repositories;

import com.PersonalDrive.demo.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> getByName(String name);
}
