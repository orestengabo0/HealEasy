package org.healeasy.repositories;

import org.healeasy.entities.Appointment;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.Patient;
import org.healeasy.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find appointments by doctor
    List<Appointment> findByDoctor(Doctor doctor);

    // Find appointments by patient
    List<Appointment> findByPatient(Patient patient);

    // Find appointments by status
    List<Appointment> findByStatus(AppointmentStatus status);

    // Find appointments by doctor and status
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);

    // Find appointments by patient and status
    List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);

    // Find appointments scheduled after a specific time
    List<Appointment> findByScheduleTimeAfter(LocalDateTime time);

    // Find appointments scheduled before a specific time
    List<Appointment> findByScheduleTimeBefore(LocalDateTime time);

    // Find appointments scheduled between two times
    List<Appointment> findByScheduleTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // Find appointments by doctor and scheduled between two times
    List<Appointment> findByDoctorAndScheduleTimeBetween(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime);

    // Find appointments by patient and scheduled between two times
    List<Appointment> findByPatientAndScheduleTimeBetween(Patient patient, LocalDateTime startTime, LocalDateTime endTime);

    // Find appointments by doctor and scheduled after a specific time
    List<Appointment> findByDoctorAndScheduleTimeAfter(Doctor doctor, LocalDateTime time);

    // Find appointments by patient and scheduled after a specific time
    List<Appointment> findByPatientAndScheduleTimeAfter(Patient patient, LocalDateTime time);
}
