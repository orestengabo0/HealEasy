package org.healeasy.repositories;

import org.healeasy.entities.Doctor;
import org.healeasy.enums.DoctorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for Doctor entity.
 * Provides methods for database operations related to doctors.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE u.role = 'DOCTOR' AND u.id = :id")
    boolean isDoctor(Long id);
    
    /**
     * Find a doctor by license number.
     * 
     * @param licenceNumber The license number to search for
     * @return The doctor with the given license number, or null if not found
     */
    Doctor findByLicenceNumber(String licenceNumber);
    
    /**
     * Check if a doctor with the given license number exists.
     * 
     * @param licenceNumber The license number to check
     * @return True if a doctor with the given license number exists, false otherwise
     */
    boolean existsByLicenceNumber(String licenceNumber);
    
    /**
     * Find doctors by status.
     * 
     * @param status The status to search for
     * @return List of doctors with the given status
     */
    List<Doctor> findByStatus(DoctorStatus status);
    
    /**
     * Find doctors by status with pagination.
     * 
     * @param status The status to search for
     * @param pageable Pagination information
     * @return Page of doctors with the given status
     */
    Page<Doctor> findByStatus(DoctorStatus status, Pageable pageable);
    
    /**
     * Search doctors by name, email, phone number, or license number.
     * 
     * @param searchTerm The search term
     * @return List of doctors matching the search term
     */
    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.licenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Doctor> searchDoctors(@Param("searchTerm") String searchTerm);
    
    /**
     * Search doctors by name, email, phone number, or license number with pagination.
     * 
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of doctors matching the search term
     */
    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.licenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Doctor> searchDoctorsPaginated(@Param("searchTerm") String searchTerm, Pageable pageable);
}