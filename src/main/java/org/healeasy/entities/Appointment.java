package org.healeasy.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.healeasy.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;
}
