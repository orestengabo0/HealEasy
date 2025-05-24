package org.healeasy.exceptions;

public class AdminOperationException extends RuntimeException {
    public AdminOperationException(String message) {
        super(message);
    }
}