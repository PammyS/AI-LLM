package org.workspace.dto.response;

import dev.langchain4j.model.output.structured.Description;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Summarised response from AI")
public class SummaryResponse {

    @Description("Short summary of the input text")
    @Schema(description = "Generated summary", example = "The phrase 'soul of AI' likely refers to the ethical " +
            "and philosophical considerations of artificial intelligence, including its potential impacts on " +
            "human values, consciousness, and morality.")
    private String summary;

    @Description("Confidence score between 0 and 1")
    @Schema(description = "Confidence score", example = "0.95")
    private Double confidence;
}
