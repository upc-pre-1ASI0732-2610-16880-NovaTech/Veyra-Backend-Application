package com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.MedicationAdministration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationAdministrationRepository extends JpaRepository<MedicationAdministration, Long> {
    List<MedicationAdministration> findByResidentIdOrderByCreatedAtDesc(Long residentId);
}
