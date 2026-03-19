package org.workspace.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.workspace.dto.AiOperation;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AiMetricsService {

    private final MeterRegistry meterRegistry;


    public void recordLatency(AiOperation operation, long latencyMs) {
        meterRegistry.timer("ai.latency",
                "model", "gpt-4o",
                "operation", operation.name().toLowerCase()
        ).record(latencyMs, TimeUnit.MILLISECONDS);
    }

    public void recordSuccess() {
        meterRegistry.counter("ai.requests.success").increment();
    }

    public void recordFailure() {
        meterRegistry.counter("ai.requests.failure").increment();
    }
}
