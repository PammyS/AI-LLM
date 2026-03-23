package org.workspace.exception;

public class LlmLowConfidenceException extends RuntimeException {
    public LlmLowConfidenceException(String message) {
        super(message);
    }
}