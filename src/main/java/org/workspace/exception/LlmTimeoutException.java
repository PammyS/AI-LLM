package org.workspace.exception;

public class LlmTimeoutException extends ApiException {

    public LlmTimeoutException() {
        super("LLM request timed out", "LLM_TIMEOUT");
    }
}
