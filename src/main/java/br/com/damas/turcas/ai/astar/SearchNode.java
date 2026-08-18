package br.com.damas.turcas.ai.astar;

import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;

public final class SearchNode implements Comparable<SearchNode> {
    private final Board board;
    private final Move firstMove;
    private final double gScore;
    private final double hScore;
    private final double fScore;
    private final int depth;

    public SearchNode(Board board, Move firstMove, double gScore, double hScore, double fScore, int depth) {
        this.board = board;
        this.firstMove = firstMove;
        this.gScore = gScore;
        this.hScore = hScore;
        this.fScore = fScore;
        this.depth = depth;
    }

    public Board getBoard() {
        return board;
    }

    public Move getFirstMove() {
        return firstMove;
    }

    public double getGScore() {
        return gScore;
    }

    public double getHScore() {
        return hScore;
    }

    public double getFScore() {
        return fScore;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public int compareTo(SearchNode other) {
        return Double.compare(this.fScore, other.fScore);
    }
}
