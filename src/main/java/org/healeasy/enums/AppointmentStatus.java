package org.healeasy.enums;

/**
 * Enum representing the possible statuses of an appointment.
 */
public enum AppointmentStatus {
    PENDING,    // Initial state when an appointment is created
    CONFIRMED,  // When the appointment is confirmed by the doctor
    CANCELLED,  // When the appointment is cancelled by the patient or doctor
    COMPLETED   // When the appointment has been completed
}
