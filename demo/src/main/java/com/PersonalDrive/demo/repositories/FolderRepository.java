package com.PersonalDrive.demo.repositories;

import com.PersonalDrive.demo.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {

}
