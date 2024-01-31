package com.asktech.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.BulkFileUrlData;

public interface BulkFileUrlDataRepo extends JpaRepository<BulkFileUrlData,Long>{

	BulkFileUrlData findByfileName(String fileName);

	List<BulkFileUrlData> findByfileType(String fileType);

}
