package org.healeasy.Iservices;

import org.healeasy.entities.Appointment;
import org.healeasy.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for appointment operations
 */
public interface IAppointmentService {
    
    /**
     * Schedule a new appointment with Zoom meeting
     * 
     * @param doctorId ID of the doctor
     * @param patientId ID of the patient
     * @param scheduleTime Scheduled time for the appointment
     * @param durationMinutes Duration of the appointment in minutes
     * @param description Description of the appointment
     * @return The created appointment
     */
    Appointment scheduleAppointment(Long doctorId, Long patientId, LocalDateTime scheduleTime, 
                                   Integer durationMinutes, String description);
    
    /**
     * Get an appointment by ID
     * 
     * @param id Appointment ID
     * @return The appointment if found, null otherwise
     */
    Appointment getAppointmentById(Long id);
    
    /**
     * Get all appointments for a doctor
     * 
     * @param doctorId Doctor ID
     * @return List of appointments for the doctor
     */
    List<Appointment> getAppointmentsByDoctor(Long doctorId);
    
    /**
     * Get all appointments for a patient
     * 
     * @param patientId Patient ID
     * @return List of appointments for the patient
     */
    List<Appointment> getAppointmentsByPatient(Long patientId);
    
    /**
     * Get appointments by status
     * 
     * @param status Appointment status
     * @return List of appointments with the specified status
     */
    List<Appointment> getAppointmentsByStatus(AppointmentStatus status);
    
    /**
     * Get appointments for a doctor with a specific status
     * 
     * @param doctorId Doctor ID
     * @param status Appointment status
     * @return List of appointments for the doctor with the specified status
     */
    List<Appointment> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status);
    
    /**
     * Get appointments for a patient with a specific status
     * 
     * @param patientId Patient ID
     * @param status Appointment status
     * @return List of appointments for the patient with the specified status
     */
    List<Appointment> getAppointmentsByPatientAndStatus(Long patientId, AppointmentStatus status);
    
    /**
     * Update the status of an appointment
     * 
     * @param id Appointment ID
     * @param status New status
     * @return The updated appointment
     */
    Appointment updateAppointmentStatus(Long id, AppointmentStatus status);
    
    /**
     * Reschedule an appointment
     * 
     * @param id Appointment ID
     * @param newScheduleTime New scheduled time
     * @param newDurationMinutes New duration in minutes
     * @return The updated appointment
     */
    Appointment rescheduleAppointment(Long id, LocalDateTime newScheduleTime, Integer newDurationMinutes);
    
    /**
     * Cancel an appointment
     * 
     * @param id Appointment ID
     * @return The cancelled appointment
     */
    Appointment cancelAppointment(Long id);
    
    /**
     * Get upcoming appointments for a doctor
     * 
     * @param doctorId Doctor ID
     * @return List of upcoming appointments for the doctor
     */
    List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId);
    
    /**
     * Get upcoming appointments for a patient
     * 
     * @param patientId Patient ID
     * @return List of upcoming appointments for the patient
     */
    List<Appointment> getUpcomingAppointmentsForPatient(Long patientId);
}