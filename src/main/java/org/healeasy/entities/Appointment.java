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

    @OneToOne(mappedBy = "appointment", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    private Consultation consultation;

    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    @Column(name = "zoom_meeting_id")
    private String zoomMeetingId;

    @Column(name = "zoom_join_url")
    private String zoomJoinUrl;

    @Column(name = "zoom_start_url")
    private String zoomStartUrl;

    @Column(name = "zoom_password")
    private String zoomPassword;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 30; // Default duration of 30 minutes

    @Column(name = "description")
    private String description;
}
