package org.workspace.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.workspace.dto.request.ChatRequest;
import org.workspace.dto.response.ChatResponse;
import org.workspace.dto.response.ClassifyResponse;
import org.workspace.dto.response.ExtractResponse;
import org.workspace.dto.response.SummaryResponse;
import org.workspace.service.ChatService;
import org.workspace.service.ClassifyService;
import org.workspace.service.ExtractService;
import org.workspace.service.SummaryService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI APIs", description = "AI APIs for Chat, Summarization, Extraction and Classifications")
@RequiredArgsConstructor
public class AiController {

    private final ChatService chatService;
    private final ClassifyService classifyService;
    private final ExtractService extractService;
    private final SummaryService summaryService;

    @Tag(name = "Chat API", description = "AI Chat API")
    @Operation(
            summary = "Answers question or just chat using AI",
            description = "Simply "
    )
    @PostMapping(path = "/chat")
    public CompletableFuture<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {

        log.info("Chat request received");

        return chatService.chat(request.getMessage());
    }

    @Tag(name = "Summarize API", description = "AI API to Summarize texts")
    @Operation(
            summary = "Summarise text using AI",
            description = "Provide any paragraph and get a structured summary with confidence score"
    )
    @PostMapping(path = "/summarize")
    public CompletableFuture<SummaryResponse> summarize(@Valid @RequestBody ChatRequest request) {

        log.info("Summarize request received");

        return summaryService.summarize(request.getMessage());
    }

    @Tag(name = "Classify API", description = "AI API to Classify texts")
    @Operation(
            summary = "Classify text using AI",
            description = "Provide any text or paragraph and get a classified tag with confidence score"
    )
    @PostMapping(path = "/classify")
    public CompletableFuture<ClassifyResponse> classify(@Valid @RequestBody ChatRequest request) {

        log.info("Classify request received");

        return classifyService.classify(request.getMessage());
    }

    @Tag(name = "Extract API", description = "AI API to Extract Info from text input")
    @Operation(
            summary = "Extract text using AI",
            description = "Provide any text or paragraph and get extract info in a defined schema"
    )
    @PostMapping(path = "/extract")
    public CompletableFuture<ExtractResponse> extract(@Valid @RequestBody ChatRequest request) {

        log.info("Extract request received");

        return extractService.extract(request.getMessage());
    }
}
