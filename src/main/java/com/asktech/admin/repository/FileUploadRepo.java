package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.FileLoading;

public interface FileUploadRepo  extends JpaRepository<FileLoading, String> {

}
