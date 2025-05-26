package org.healeasy.repositories;

import org.healeasy.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Basic CRUD operations are provided by JpaRepository
}