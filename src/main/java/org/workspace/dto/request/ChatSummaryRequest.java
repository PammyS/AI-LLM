package org.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChatSummaryRequest {

    private static final String TEMPLATE = """
            You are a backend service.

            Return ONLY raw JSON.
            Do NOT wrap the response in markdown.

            Schema:
            {
             "summary": "string",
             "confidence": number between 0 and 1
            }

            Summarize the following text:
            {input}          
            """;

    @NotBlank(message = "Message must not be empty")
    @Size(max = 2000, message = "Message too long")
    private String requestText;

    public ChatSummaryRequest(String input) {
        this.requestText = TEMPLATE.replace("{input}", input);
    }
}
