package org.workspace.dto.response;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class SummaryResponse {

    @Description("Short summary of the input text")
    private String summary;

    @Description("Confidence score between 0 and 1")
    private Double confidence;
}
