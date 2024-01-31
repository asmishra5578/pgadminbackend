package com.asktech.admin.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.admin.model.PGConfigurationDetails;
import com.asktech.admin.repository.PGConfigurationDetailsRepository;

@Service
public class pgConfigNewSS {
@Autowired
    PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
    
    public PGConfigurationDetails getoneTest(String pgId){
    PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
    .findByPgUuid(pgId);
    return pgConfigurationDetails;
    }
}
