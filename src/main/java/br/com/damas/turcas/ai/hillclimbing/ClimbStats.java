package br.com.damas.turcas.ai.hillclimbing;

import br.com.damas.turcas.game.Move;

public final class ClimbStats {
    private final Move bestMove;
    private final double bestEvaluation;
    private final int restartsDone;
    private final int totalIterations;

    public ClimbStats(Move bestMove, double bestEvaluation, int restartsDone, int totalIterations) {
        this.bestMove = bestMove;
        this.bestEvaluation = bestEvaluation;
        this.restartsDone = restartsDone;
        this.totalIterations = totalIterations;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public double getBestEvaluation() {
        return bestEvaluation;
    }

    public int getRestartsDone() {
        return restartsDone;
    }

    public int getTotalIterations() {
        return totalIterations;
    }
}
