package org.healeasy.exceptions;

public class LargeFileException extends RuntimeException{
    public LargeFileException(String message){
        super(message);
    }
}
