package org.healeasy.exceptions;

/**
 * Exception thrown when an available slot is not found.
 */
public class SlotNotFoundException extends RuntimeException {
    
    public SlotNotFoundException(String message) {
        super(message);
    }
    
    public SlotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}