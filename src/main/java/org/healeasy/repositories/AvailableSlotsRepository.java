package org.healeasy.repositories;

import org.healeasy.entities.AvailableSlots;
import org.healeasy.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AvailableSlots entity.
 * Provides methods to interact with the available_slots table.
 */
@Repository
public interface AvailableSlotsRepository extends JpaRepository<AvailableSlots, Long> {
    
    /**
     * Find all available slots for a specific doctor.
     * 
     * @param doctor The doctor whose slots to find
     * @return List of available slots for the doctor
     */
    List<AvailableSlots> findByDoctor(Doctor doctor);
    
    /**
     * Find all available slots for a specific doctor by doctor ID.
     * 
     * @param doctorId The ID of the doctor whose slots to find
     * @return List of available slots for the doctor
     */
    List<AvailableSlots> findByDoctorId(Long doctorId);
    
    /**
     * Find all available slots for a specific doctor within a time range.
     * 
     * @param doctor The doctor whose slots to find
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of available slots for the doctor within the time range
     */
    List<AvailableSlots> findByDoctorAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Doctor doctor, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find all available slots for a specific doctor by ID within a time range.
     * 
     * @param doctorId The ID of the doctor whose slots to find
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of available slots for the doctor within the time range
     */
    List<AvailableSlots> findByDoctorIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
}