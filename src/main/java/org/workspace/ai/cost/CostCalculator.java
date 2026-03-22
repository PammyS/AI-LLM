package org.workspace.ai.cost;

import org.springframework.stereotype.Component;

@Component
public class CostCalculator {

    private static final double INPUT_COST_PER_1K = 0.005;
    private static final double OUTPUT_COST_PER_1K = 0.015;

    public double calculate(int inputTokens, int outputTokens) {
        return (inputTokens / 1000.0 * INPUT_COST_PER_1K)
                + (outputTokens / 1000.0 * OUTPUT_COST_PER_1K);
    }
}
