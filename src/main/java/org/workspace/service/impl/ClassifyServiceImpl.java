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
import org.workspace.dto.response.Category;
import org.workspace.dto.response.ClassifyResponse;
import org.workspace.exception.LlmException;
import org.workspace.ai.metrics.AiMetricsService;
import org.workspace.exception.LlmLowConfidenceException;
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

    private static final double MIN_CONFIDENCE = 0.7;

    private final Executor aiExecutor;


    @Override
    @Retry(name = "aiService", fallbackMethod = "fallbackSummary")
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    @TimeLimiter(name = "aiService")
    public CompletableFuture<ClassifyResponse> classify(String message) {
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {

            try {

                Result<ClassifyResponse> responseResult = aiClassifyService.classify(message);

                ClassifyResponse classifyResponse = responseResult.content();
                if (classifyResponse.getConfidence() == null || classifyResponse.getConfidence() < MIN_CONFIDENCE) {
                    throw new LlmLowConfidenceException("ClassifyResponse Low confidence response");
                }
                if (classifyResponse.getCategory() == null) {
                    classifyResponse.setCategory(Category.UNKNOWN);
                    classifyResponse.setConfidence(0.0);
                }

                TokenUsage tokens  = responseResult.tokenUsage();
                int inputTokens = tokens.inputTokenCount();
                int outputTokens = tokens.outputTokenCount();

                long latencyMs = elapsedMs(startTime);
                aiMetricsService.recordLatency(AiOperation.CLASSIFY, latencyMs);
                aiMetricsService.recordSuccess(AiOperation.CLASSIFY);
                aiMetricsService.recordTokens(AiOperation.CLASSIFY, inputTokens, outputTokens);
                double cost = aiMetricsService.recordCost(AiOperation.CLASSIFY, inputTokens, outputTokens);
                log.info("AI_CALL endpoint=Summarize latencyMs={} inputSize={}", latencyMs, message.length());
                log.info("AI usage Classify Service | input={} output={} cost=${}", inputTokens, outputTokens, cost);

                return classifyResponse;

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

    public CompletableFuture<ClassifyResponse> fallbackSummary(String input, Throwable ex) {
        log.warn("Fallback triggered | reason={}", ex.getMessage());
        ClassifyResponse fallback = new ClassifyResponse();
                fallback.setCategory(null);
                fallback.setConfidence(0.0);
        return CompletableFuture.completedFuture(fallback);
    }

    private long elapsedMs(long startTime) {
        return (System.currentTimeMillis() - startTime);
    }
}
