package org.workspace.service.ai;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import dev.langchain4j.service.spring.AiService;
import org.workspace.dto.response.SummaryResponse;

@AiService
public interface SummaryAiService {

    @SystemMessage(" You are a backend service.")
    Result<SummaryResponse> summarise(@UserMessage String text);
}
