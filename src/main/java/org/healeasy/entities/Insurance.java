package org.healeasy.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider")
    private String provider;

    @Column(name = "policy_number")
    private String policyNumber;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
