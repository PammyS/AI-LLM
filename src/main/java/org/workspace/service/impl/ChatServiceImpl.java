package org.workspace.service.impl;

import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace.dto.AiOperation;
import org.workspace.dto.response.ChatResponse;
import org.workspace.exception.LlmException;
import org.workspace.ai.metrics.AiMetricsService;
import org.workspace.service.ChatService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OpenAiChatModel chatModel;

    private final AiMetricsService aiMetricsService;

    private final Executor aiExecutor;

    @Override
    @Retry(name = "aiService")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<ChatResponse> chat(String message) {

        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {
                String response = chatModel.chat(message);

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.CHAT, latencyMs);
                aiMetricsService.recordSuccess(AiOperation.CHAT);
                log.info("AI_CALL endpoint=Chat latencyMs={} inputSize={}", latencyMs, message.length());

                return ChatResponse.builder()
                        .reply(response)
                        .build();

            } catch (Exception ex) {

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.CHAT, latencyMs);
                aiMetricsService.recordFailure();
                log.error("AI call failed | latencyMs={}", latencyMs, ex);

                throw new LlmException("Failed to generate response: " + ex);
            }

        }, aiExecutor);
    }

    private CompletableFuture<ChatResponse> fallback(String message, Throwable ex) {

        log.error("AI service fallback triggered", ex);

        ChatResponse fallbackResponse = ChatResponse
                .builder()
                .reply("AI service temporarily unavailable")
                .build();

        return CompletableFuture.completedFuture(fallbackResponse);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
