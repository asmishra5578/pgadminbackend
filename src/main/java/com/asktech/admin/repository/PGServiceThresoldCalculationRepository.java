package com.asktech.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.admin.model.PGServiceThresoldCalculation;

public interface PGServiceThresoldCalculationRepository extends JpaRepository<PGServiceThresoldCalculation, String>{

	PGServiceThresoldCalculation findByPgIdAndServiceType(String pgUuid, String pgServices);

}
