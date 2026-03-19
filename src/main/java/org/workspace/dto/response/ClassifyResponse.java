package org.workspace.dto.response;

import dev.langchain4j.model.output.structured.Description;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Schema(description = "Classified response from AI")
public class ClassifyResponse {

    @Description("Category of the input. Must be from Defined category type")
    @Schema(description = "Generated category", example = "technical_support")
    private Category category;

    @Description("Confidence score between 0 and 1")
    @Schema(description = "Confidence score", example = "0.95")
    private Double confidence;
}
