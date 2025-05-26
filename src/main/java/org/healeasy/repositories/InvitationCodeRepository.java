package org.healeasy.repositories;

import org.healeasy.entities.Doctor;
import org.healeasy.entities.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for InvitationCode entity.
 * Provides methods for database operations related to invitation codes.
 */
public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {
    
    /**
     * Find an invitation code by its code value.
     * 
     * @param code The code to search for
     * @return Optional containing the invitation code if found
     */
    Optional<InvitationCode> findByCode(String code);
    
    /**
     * Find all valid (not used and not expired) invitation codes for a doctor.
     * 
     * @param doctor The doctor to find codes for
     * @param now The current time to check against expiration
     * @return List of valid invitation codes
     */
    @Query("SELECT ic FROM InvitationCode ic WHERE ic.doctor = :doctor AND ic.used = false AND ic.expirationDate > :now")
    List<InvitationCode> findValidCodesByDoctor(@Param("doctor") Doctor doctor, @Param("now") LocalDateTime now);
    
    /**
     * Find the most recent valid invitation code for a doctor.
     * 
     * @param doctor The doctor to find the code for
     * @param now The current time to check against expiration
     * @return Optional containing the most recent valid invitation code if found
     */
    @Query("SELECT ic FROM InvitationCode ic WHERE ic.doctor = :doctor AND ic.used = false AND ic.expirationDate > :now ORDER BY ic.createdAt DESC")
    Optional<InvitationCode> findMostRecentValidCodeByDoctor(@Param("doctor") Doctor doctor, @Param("now") LocalDateTime now);
    
    /**
     * Check if a doctor has any valid invitation codes.
     * 
     * @param doctor The doctor to check
     * @param now The current time to check against expiration
     * @return true if the doctor has at least one valid invitation code, false otherwise
     */
    @Query("SELECT COUNT(ic) > 0 FROM InvitationCode ic WHERE ic.doctor = :doctor AND ic.used = false AND ic.expirationDate > :now")
    boolean hasValidCodes(@Param("doctor") Doctor doctor, @Param("now") LocalDateTime now);
    
    /**
     * Find all expired invitation codes.
     * 
     * @param now The current time to check against expiration
     * @return List of expired invitation codes
     */
    @Query("SELECT ic FROM InvitationCode ic WHERE ic.expirationDate <= :now AND ic.used = false")
    List<InvitationCode> findExpiredCodes(@Param("now") LocalDateTime now);
}