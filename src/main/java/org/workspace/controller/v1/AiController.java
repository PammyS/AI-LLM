package org.workspace.controller.v1;

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
@RequiredArgsConstructor
public class AiController {

    private final ChatService chatService;
    private final ClassifyService classifyService;
    private final ExtractService extractService;
    private final SummaryService summaryService;

    @PostMapping(path = "/chat")
    public CompletableFuture<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {

        log.info("Chat request received");

        return chatService.chat(request.getMessage());
    }

    @PostMapping(path = "/summarize")
    public CompletableFuture<SummaryResponse> summarize(@Valid @RequestBody ChatRequest request) {

        log.info("Summarize request received");

        return summaryService.summarize(request.getMessage());
    }

    @PostMapping(path = "/classify")
    public CompletableFuture<ClassifyResponse> classify(@Valid @RequestBody ChatRequest request) {

        log.info("Classify request received");

        return classifyService.classify(request.getMessage());
    }

    @PostMapping(path = "/extract")
    public CompletableFuture<ExtractResponse> extract(@Valid @RequestBody ChatRequest request) {

        log.info("Extract request received");

        return extractService.extract(request.getMessage());
    }
}
