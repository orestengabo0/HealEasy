package org.healeasy.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing an invitation code for doctor registration.
 * Used to track invitation codes sent to doctors during the registration process.
 */
@Getter
@Setter
@Entity
@Table(name = "invitation_codes")
public class InvitationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(name = "used", nullable = false)
    private boolean used;
    
    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if the invitation code is expired.
     * 
     * @return true if the code is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
    
    /**
     * Check if the invitation code is valid (not used and not expired).
     * 
     * @return true if the code is valid, false otherwise
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
}