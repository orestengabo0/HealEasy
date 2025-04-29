package org.healeasy.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "medications")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "duration_in_days")
    private int durationInDays;

    @ManyToMany(mappedBy = "medications")
    private List<Prescription> prescriptions;
}
