package org.healeasy.exceptions;

/**
 * Exception thrown when a doctor is not found.
 */
public class DoctorNotFoundException extends RuntimeException {
    
    public DoctorNotFoundException(String message) {
        super(message);
    }
    
    public DoctorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}