package org.healeasy.enums;

/**
 * Enum representing the status of a doctor in the system.
 * Used to track the approval process for doctor registration.
 */
public enum DoctorStatus {
    PENDING,    // Doctor has submitted registration but not yet approved
    APPROVED,   // Doctor has been approved by admin
    REJECTED    // Doctor registration has been rejected
}