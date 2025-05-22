package org.healeasy.exceptions;

public class RequestSizeExceededException extends RuntimeException {
    public RequestSizeExceededException(String message) {
        super(message);
    }
}
