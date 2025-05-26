package org.healeasy.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.healeasy.enums.DoctorStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "license_number")
    private String licenceNumber;

    @Column(name = "license_document_url")
    private String licenseDocumentUrl;

    @Column(name = "id_document_url")
    private String idDocumentUrl;

    @Column(name = "consultation_fees")
    private Integer consultationFees;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DoctorStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @PrePersist
    protected void onCreate() {
        this.status = DoctorStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
