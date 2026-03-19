package org.workspace.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.workspace.dto.response.ExtractResponse;

@AiService
public interface ExtractAiService {

    @SystemMessage("Extract structured data from user input.")
    ExtractResponse extract(@UserMessage String text);
}
