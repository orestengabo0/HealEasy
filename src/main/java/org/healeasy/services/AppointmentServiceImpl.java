package org.healeasy.services;

import org.healeasy.Iservices.IAppointmentService;
import org.healeasy.Iservices.IZoomService;
import org.healeasy.entities.Appointment;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.Patient;
import org.healeasy.enums.AppointmentStatus;
import org.healeasy.repositories.AppointmentRepository;
import org.healeasy.repositories.DoctorRepository;
import org.healeasy.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final IZoomService zoomService;

    @Autowired
    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            IZoomService zoomService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.zoomService = zoomService;
    }

    @Override
    @Transactional
    public Appointment scheduleAppointment(Long doctorId, Long patientId, LocalDateTime scheduleTime,
                                          Integer durationMinutes, String description) {
        // Validate doctor and patient
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setScheduleTime(scheduleTime);
        appointment.setDurationMinutes(durationMinutes != null ? durationMinutes : 30);
        appointment.setDescription(description);
        appointment.setStatus(AppointmentStatus.PENDING);

        // Create Zoom meeting
        String meetingTopic = "Appointment with Dr. " + doctor.getUser().getUsername();
        Map<String, String> meetingDetails = zoomService.createMeeting(
                meetingTopic,
                scheduleTime,
                appointment.getDurationMinutes(),
                description
        );

        // Set Zoom meeting details
        appointment.setZoomMeetingId(meetingDetails.get("id"));
        appointment.setZoomJoinUrl(meetingDetails.get("join_url"));
        appointment.setZoomStartUrl(meetingDetails.get("start_url"));
        appointment.setZoomPassword(meetingDetails.get("password"));

        // Save and return appointment
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        return appointmentRepository.findByDoctor(doctor);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        return appointmentRepository.findByDoctorAndStatus(doctor, status);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientAndStatus(Long patientId, AppointmentStatus status) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        return appointmentRepository.findByPatientAndStatus(patient, status);
    }

    @Override
    @Transactional
    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment rescheduleAppointment(Long id, LocalDateTime newScheduleTime, Integer newDurationMinutes) {
        Appointment appointment = getAppointmentById(id);

        // Update appointment details
        appointment.setScheduleTime(newScheduleTime);
        if (newDurationMinutes != null) {
            appointment.setDurationMinutes(newDurationMinutes);
        }

        // Update Zoom meeting
        if (appointment.getZoomMeetingId() != null) {
            zoomService.updateMeeting(
                    appointment.getZoomMeetingId(),
                    "Appointment with Dr. " + appointment.getDoctor().getUser().getUsername(),
                    newScheduleTime,
                    appointment.getDurationMinutes(),
                    appointment.getDescription()
            );
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Update status to CANCELLED
        appointment.setStatus(AppointmentStatus.CANCELLED);

        // Delete Zoom meeting
        if (appointment.getZoomMeetingId() != null) {
            zoomService.deleteMeeting(appointment.getZoomMeetingId());
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByDoctorAndScheduleTimeAfter(doctor, now);
    }

    @Override
    public List<Appointment> getUpcomingAppointmentsForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByPatientAndScheduleTimeAfter(patient, now);
    }
}
