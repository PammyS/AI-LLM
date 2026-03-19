package org.workspace.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.workspace.dto.response.ClassifyResponse;

@AiService
public interface ClassifyAiService {

    @SystemMessage("""
            You are a backend classification service.

            Classify the input into one of the predefined categories.

            Do not explain your answer.
            Only return structured output.
            """)
    ClassifyResponse classify(@UserMessage String text);
}
