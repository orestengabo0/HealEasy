package org.healeasy.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    @Column(name = "active_status")
    private Boolean activeStatus = true;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalHistory> medicalHistory;
}
