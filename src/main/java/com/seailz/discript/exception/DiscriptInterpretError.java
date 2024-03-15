package com.seailz.discript.exception;

import lombok.Getter;

@Getter
public class DiscriptInterpretError extends RuntimeException {

    private final int line;
    private final String message;

    public DiscriptInterpretError(int line, String message) {
        super("[Discript - Interpret Error] Error on line " + line + ": " + message);
        this.line = line;
        this.message = "[Discript - Interpret Error] Error on line " + line + ": " + message;
    }
}
