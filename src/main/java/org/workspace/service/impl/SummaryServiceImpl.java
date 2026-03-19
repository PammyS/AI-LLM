package org.workspace.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace.dto.AiOperation;
import org.workspace.dto.response.SummaryResponse;
import org.workspace.exception.LlmException;
import org.workspace.service.AiMetricsService;
import org.workspace.service.SummaryService;
import org.workspace.service.ai.SummaryAiService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final SummaryAiService aiSummaryService;

    private final AiMetricsService aiMetricsService;

    private final Executor aiExecutor;

    @Override
    @Retry(name = "aiService")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<SummaryResponse> summarize(String message) {

        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {

                SummaryResponse response = aiSummaryService.summarise(message);

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.SUMMARIZE, latencyMs);
                aiMetricsService.recordSuccess();
                log.info("AI_CALL endpoint=Summarize latencyMs={} inputSize={}", latencyMs, message.length());

                return response;

            } catch (Exception ex) {

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.SUMMARIZE, latencyMs);
                aiMetricsService.recordFailure();
                log.error("AI call failed | latencyMs={}", latencyMs, ex);

                throw new LlmException("Failed to generate response: " + ex);
            }

        }, aiExecutor);
    }

    private CompletableFuture<SummaryResponse> fallback(String message, Throwable ex) {

        log.error("AI service fallback triggered", ex);

        SummaryResponse fallbackResponse = new SummaryResponse();
        fallbackResponse.setSummary("AI service temporarily unavailable");
        fallbackResponse.setConfidence(0.0);

        return CompletableFuture.completedFuture(fallbackResponse);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
