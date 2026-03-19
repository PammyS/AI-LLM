package org.workspace.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.workspace.dto.response.ClassifyResponse;

@AiService
public interface ClassifyAiService {

    @SystemMessage("""
            You are a backend service.
            Classify the following text into one of these categories:
                    
                - billing
                - technical_support
                - account_issue
                - unknown
                    
            Return JSON matching this schema:
            {
              "category": "...",
              "confidence": number between 0 and 1
            }
            """)
    ClassifyResponse classify(@UserMessage String text);
}
