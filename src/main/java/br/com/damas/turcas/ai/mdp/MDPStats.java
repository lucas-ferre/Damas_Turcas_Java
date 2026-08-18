package br.com.damas.turcas.ai.mdp;

import br.com.damas.turcas.game.Move;

import java.util.Collections;
import java.util.Map;

public final class MDPStats {
    private final Move bestMove;
    private final double expectedUtility;
    private final int statesEvaluated;
    private final Map<String, Double> actionValues;

    public MDPStats(Move bestMove, double expectedUtility, int statesEvaluated, Map<String, Double> actionValues) {
        this.bestMove = bestMove;
        this.expectedUtility = expectedUtility;
        this.statesEvaluated = statesEvaluated;
        this.actionValues = actionValues != null ? Collections.unmodifiableMap(actionValues) : Collections.emptyMap();
    }

    public Move getBestMove() {
        return bestMove;
    }

    public double getExpectedUtility() {
        return expectedUtility;
    }

    public int getStatesEvaluated() {
        return statesEvaluated;
    }

    public Map<String, Double> getActionValues() {
        return actionValues;
    }
}
