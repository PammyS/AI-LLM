package org.workspace.service.impl;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace.dto.AiOperation;
import org.workspace.dto.response.ExtractResponse;
import org.workspace.dto.response.SummaryResponse;
import org.workspace.exception.LlmException;
import org.workspace.service.AiMetricsService;
import org.workspace.service.ExtractService;
import org.workspace.service.ai.ExtractAiService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractServiceImpl implements ExtractService {

    private final ExtractAiService extractAiService;

    private final AiMetricsService aiMetricsService;

    private final Executor aiExecutor;

    @Override
    @Retry(name = "aiService")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<ExtractResponse> extract(String message) {
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {

                ExtractResponse response = extractAiService.extract(message);

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.EXTRACT, latencyMs);
                aiMetricsService.recordSuccess();
                log.info("AI_CALL endpoint=Extract latencyMs={} inputSize={}", latencyMs, message.length());

                return response;

            } catch (Exception ex) {

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.EXTRACT, latencyMs);
                aiMetricsService.recordFailure();
                log.error("AI call failed | latencyMs={}", latencyMs, ex);

                throw new LlmException("Failed to generate response: " + ex);
            }

        }, aiExecutor);
    }

    private CompletableFuture<ExtractResponse> fallback(String message, Throwable ex) {

        log.error("AI service fallback triggered", ex);

        ExtractResponse fallbackResponse = new ExtractResponse();

        return CompletableFuture.completedFuture(fallbackResponse);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
