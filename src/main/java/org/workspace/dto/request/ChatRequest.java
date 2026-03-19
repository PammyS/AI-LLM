package org.workspace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "AI input request")
public class ChatRequest {

    @NotBlank(message = "Message must not be empty")
    @Size(max = 2000, message = "Message too long")
    @Schema(
            description = "Input text"
    )
    private String message;
}
