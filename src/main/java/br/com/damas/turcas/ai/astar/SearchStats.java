package br.com.damas.turcas.ai.astar;

import br.com.damas.turcas.game.Move;

public final class SearchStats {
    private final Move bestMove;
    private final int nodesExpanded;
    private final double minFScore;
    private final boolean targetReached;

    public SearchStats(Move bestMove, int nodesExpanded, double minFScore, boolean targetReached) {
        this.bestMove = bestMove;
        this.nodesExpanded = nodesExpanded;
        this.minFScore = minFScore;
        this.targetReached = targetReached;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int getNodesExpanded() {
        return nodesExpanded;
    }

    public double getMinFScore() {
        return minFScore;
    }

    public boolean isTargetReached() {
        return targetReached;
    }
}
