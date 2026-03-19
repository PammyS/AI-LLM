package org.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Extracted response from AI")
public class ExtractResponse {

    @Description("Name of the user")
    @Schema(description = "Extracted Name", example = "John Smith")
    private String name;

    @Description("Organization of the user")
    @Schema(description = "Extracted Organization", example = "Google")
    private String organization;

    @Description("Date in format DD-MM-YYYY (e.g., 20-03-2023)")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Extracted Date", example = "23-03-2026")
    private Date date;
}
