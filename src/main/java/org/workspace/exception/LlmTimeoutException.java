package org.workspace.exception;

import java.util.concurrent.TimeoutException;

public class LlmTimeoutException extends ApiException {

    public LlmTimeoutException(TimeoutException ex) {
        super("LLM request timed out", "LLM_TIMEOUT" + ex);
    }
}
