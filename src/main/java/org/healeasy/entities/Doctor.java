package org.healeasy.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "consultation_fees")
    private Integer consultationFees;

    @OneToMany(mappedBy = "doctors", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}