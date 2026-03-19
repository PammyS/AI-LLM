package org.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "Message must not be empty")
    @Size(max = 2000, message = "Message too long")
    private String message;
}
