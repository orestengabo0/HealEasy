package org.healeasy.exceptions;

public class FailedToUploadImageException extends RuntimeException{
    public FailedToUploadImageException(String message) {
        super(message);
    }
}
