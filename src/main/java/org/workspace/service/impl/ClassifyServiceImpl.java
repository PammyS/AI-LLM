package org.workspace.service.impl;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace.dto.AiOperation;
import org.workspace.dto.response.Category;
import org.workspace.dto.response.ClassifyResponse;
import org.workspace.dto.response.SummaryResponse;
import org.workspace.exception.LlmException;
import org.workspace.service.AiMetricsService;
import org.workspace.service.ClassifyService;
import org.workspace.service.ai.ClassifyAiService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifyServiceImpl implements ClassifyService {

    private final ClassifyAiService aiClassifyService;

    private final AiMetricsService aiMetricsService;

    private final Executor aiExecutor;


    @Override
    @Retry(name = "aiService")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<ClassifyResponse> classify(String message) {
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {

                ClassifyResponse response = aiClassifyService.classify(message);
                if (response.getCategory() == null) {
                    response.setCategory(Category.UNKNOWN);
                    response.setConfidence(0.0);
                }
                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.CLASSIFY, latencyMs);
                aiMetricsService.recordSuccess();
                log.info("AI_CALL endpoint=Classify latencyMs={} inputSize={}", latencyMs, message.length());

                return response;

            } catch (Exception ex) {

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.CLASSIFY, latencyMs);
                aiMetricsService.recordFailure();
                log.error("AI call failed | latencyMs={}", latencyMs, ex);

                throw new LlmException("Failed to generate response: " + ex);
            }

        }, aiExecutor);
    }

    private CompletableFuture<ClassifyResponse> fallback(String message, Throwable ex) {

        log.error("AI service fallback triggered", ex);

        ClassifyResponse fallbackResponse = new ClassifyResponse();
        fallbackResponse.setCategory(Category.UNKNOWN);
        fallbackResponse.setConfidence(0.0);

        return CompletableFuture.completedFuture(fallbackResponse);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
