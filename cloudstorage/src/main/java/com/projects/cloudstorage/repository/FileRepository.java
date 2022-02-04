package com.projects.cloudstorage.repository;

import com.projects.cloudstorage.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface FileRepository extends JpaRepository<FileData, Long> {
    FileData findByFilename(String filename);

    @Modifying
    @Query("update FileData u set u.filename = ?1, u.updated=?3 where u.filename = ?2")
    void renameFile(String newFilename, String filename, Date date);


}
