package org.workspace.dto.response;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class ClassifyResponse {

    @Description("Category of the input text")
    private String category;

    @Description("Confidence score between 0 and 1")
    private Double confidence;
}
