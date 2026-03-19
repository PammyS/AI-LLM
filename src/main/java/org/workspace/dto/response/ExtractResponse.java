package org.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.util.Date;

@Data
public class ExtractResponse {

    @Description("Name of the user")
    private String name;

    @Description("Organization of the user")
    private String organization;

    @Description("Date in format DD-MM-YYYY")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;
}
