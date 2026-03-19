package org.workspace.exception;

public abstract class ApiException extends RuntimeException {

    private final String errorCode;

    protected ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
