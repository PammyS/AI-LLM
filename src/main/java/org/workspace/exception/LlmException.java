package org.workspace.exception;

public class LlmException extends ApiException {

    public LlmException(String message) {
        super(message, "LLM_ERROR");
    }
}
