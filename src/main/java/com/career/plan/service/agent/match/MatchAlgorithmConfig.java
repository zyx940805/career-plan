package com.career.plan.service.agent.match;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MatchAlgorithmConfig {

    private Map<MatchDimension, Float> dimensionWeights = new HashMap<>();

    public MatchAlgorithmConfig() {
        for (MatchDimension dimension : MatchDimension.values()) {
            dimensionWeights.put(dimension, dimension.getDefaultWeight());
        }
    }

    public void updateWeight(MatchDimension dimension, float weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("权重必须在 0-1 之间");
        }
        dimensionWeights.put(dimension, weight);
    }

    public float getWeight(MatchDimension dimension) {
        return dimensionWeights.getOrDefault(dimension, 0.2f);
    }

    public Map<MatchDimension, Float> getAllWeights() {
        return new HashMap<>(dimensionWeights);
    }

    public void resetToDefault() {
        dimensionWeights.clear();
        for (MatchDimension dimension : MatchDimension.values()) {
            dimensionWeights.put(dimension, dimension.getDefaultWeight());
        }
    }
}
