package org.workspace.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.workspace.dto.response.ExtractResponse;

@AiService
public interface ExtractAiService {

    @SystemMessage("""
            You are a backend service.
            Extract the following information:            

            Return JSON matching this schema:
            {
              "name": string,
               "organization": string,
               "date": string
            }
            """)
    ExtractResponse extract(@UserMessage String text);
}
