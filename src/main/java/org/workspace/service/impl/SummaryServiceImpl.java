package org.workspace.service.impl;

import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.Result;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace.dto.AiOperation;
import org.workspace.dto.response.SummaryResponse;
import org.workspace.exception.LlmException;
import org.workspace.ai.metrics.AiMetricsService;
import org.workspace.exception.LlmLowConfidenceException;
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

    private static final double MIN_CONFIDENCE = 0.7;

    private final Executor aiExecutor;

    @Override
    @Retry(name = "aiService", fallbackMethod = "fallbackSummary")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<SummaryResponse> summarize(String message) {

        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {

                Result<SummaryResponse> responseResult = aiSummaryService.summarise(message);

                SummaryResponse summaryResponse = responseResult.content();

                if (summaryResponse.getConfidence() == null || summaryResponse.getConfidence() < MIN_CONFIDENCE) {
                    throw new LlmLowConfidenceException("SummaryResponse Low confidence response");
                }
                TokenUsage tokens  = responseResult.tokenUsage();
                int inputTokens = tokens.inputTokenCount();
                int outputTokens = tokens.outputTokenCount();

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.SUMMARIZE, latencyMs);
                aiMetricsService.recordSuccess(AiOperation.SUMMARIZE);
                aiMetricsService.recordTokens(AiOperation.SUMMARIZE, inputTokens, outputTokens);
                double cost = aiMetricsService.recordCost(AiOperation.SUMMARIZE, inputTokens, outputTokens);
                log.info("AI_CALL endpoint=Summarize latencyMs={} inputSize={}", latencyMs, message.length());
                log.info("AI usage Summary Service | input={} output={} cost=${}", inputTokens, outputTokens, cost);

                return summaryResponse;

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

    public CompletableFuture<SummaryResponse> fallbackSummary(String input, Throwable ex) {
        log.warn("Fallback triggered | reason={}", ex.getMessage());
        SummaryResponse fallback = new SummaryResponse();
        fallback.setSummary("Unable to generate summary at the moment");
        fallback.setConfidence(0.0);
        return CompletableFuture.completedFuture(fallback);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
