package com.gamezone.exception;

public class VideoGameNotFoundException extends RuntimeException {
    public VideoGameNotFoundException(String message) {
        super(message);
    }
}
