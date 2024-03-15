package com.seailz.discript.exception;

public class DiscriptLoadError extends RuntimeException {

    private final String message;

    public DiscriptLoadError(String message) {
        super("[Discript - Load Error] " + message);
        this.message = message;
    }

}
