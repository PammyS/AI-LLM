package org.workspace.ai.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.workspace.ai.cost.CostCalculator;
import org.workspace.dto.AiOperation;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AiMetricsService {

    private final MeterRegistry meterRegistry;

    private final CostCalculator costCalculator;

    public void recordLatency(AiOperation operation, long latencyMs) {
        meterRegistry.timer("ai.latency",
                "model", "gpt-4o",
                "operation", getTag(operation)
        ).record(latencyMs, TimeUnit.MILLISECONDS);
    }

    public void recordSuccess(AiOperation operation) {
        meterRegistry.counter(
                "ai.requests.success",
                "operation", getTag(operation)
        ).increment();
    }

    public void recordFailure() {
        meterRegistry.counter("ai.requests.failure").increment();
    }

    public void recordTokens(AiOperation operation, int input, int output) {

        meterRegistry.counter("ai.tokens.input", "operation", getTag(operation))
                .increment(input);

        meterRegistry.counter("ai.tokens.output", "operation", getTag(operation))
                .increment(output);

        meterRegistry.counter("ai.tokens.total", "operation", getTag(operation))
                .increment(input + output);
    }

    public double recordCost(AiOperation operation, int input, int output) {
        double cost = costCalculator.calculate(input, output);
        meterRegistry.counter("ai.cost.total", "operation", getTag(operation))
                .increment(cost);
        return cost;
    }

    private static String getTag(AiOperation operation) {
        return operation.name().toLowerCase();
    }
}
