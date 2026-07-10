package com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication,Long> {
    boolean existsByNursingHomeIdAndNameAndLot(Long nursingHomeId, String name, String lot);

    boolean existsByNursingHomeId(Long nursingHomeId);
    List<Medication> findByNursingHomeId(Long nursingHomeId);
}
