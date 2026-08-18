package br.com.damas.turcas.ai.astar;

import br.com.damas.turcas.ai.evaluation.Evaluator;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.RulesEngine;

import java.util.List;
import java.util.PriorityQueue;

public final class AStarSolver {
    private final Evaluator evaluator;
    private final RulesEngine rules;
    private final int maxNodes;
    private final int maxDepth;

    public AStarSolver(int maxNodes, int maxDepth) {
        this.evaluator = new Evaluator();
        this.rules = new RulesEngine();
        this.maxNodes = maxNodes > 0 ? maxNodes : 600;
        this.maxDepth = maxDepth > 0 ? maxDepth : 4;
    }

    public SearchStats findBestMove(Board b, PieceColor color) {
        List<Move> moves = rules.getLegalMoves(b, color);
        if (moves.isEmpty()) {
            return new SearchStats(null, 0, 0.0, false);
        }

        if (moves.size() == 1) {
            return new SearchStats(moves.get(0), 1, 0.0, true);
        }

        double targetScore = 1000.0;
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        for (Move m : moves) {
            Board nb = b.clone();
            nb.applyMove(m);

            double g = 10.0;
            if (!m.isCapture()) {
                g += 5.0;
            } else {
                g -= m.getCaptureCount() * 15.0;
            }

            double eval = evaluator.evaluate(nb, color);
            double h = Math.max(0.0, targetScore - eval);
            double f = g + h;

            pq.add(new SearchNode(nb, m, g, h, f, 1));
        }

        Move bestMove = moves.get(0);
        double bestNodeF = Double.MAX_VALUE;
        int nodesCount = 0;

        while (!pq.isEmpty() && nodesCount < maxNodes) {
            SearchNode curr = pq.poll();
            nodesCount++;

            if (curr.getFScore() < bestNodeF) {
                bestNodeF = curr.getFScore();
                bestMove = curr.getFirstMove();
            }

            if (curr.getHScore() <= 0.0 || curr.getDepth() >= maxDepth) {
                continue;
            }

            List<Move> nextMoves = rules.getLegalMoves(curr.getBoard(), curr.getBoard().getTurn());
            for (Move nm : nextMoves) {
                Board nb = curr.getBoard().clone();
                nb.applyMove(nm);

                double stepCost = 10.0;
                if (curr.getBoard().getTurn() != color) {
                    stepCost += 5.0;
                }
                double newG = curr.getGScore() + stepCost;
                double eval = evaluator.evaluate(nb, color);
                double newH = Math.max(0.0, targetScore - eval);
                double newF = newG + newH;

                pq.add(new SearchNode(nb, curr.getFirstMove(), newG, newH, newF, curr.getDepth() + 1));
            }
        }

        boolean targetReached = (bestNodeF < 500.0);
        return new SearchStats(bestMove, nodesCount, bestNodeF, targetReached);
    }

    public String formatStats(SearchStats stats) {
        return String.format("A*: Custo f(n) = %.1f | Nós Expandidos = %d", stats.getMinFScore(), stats.getNodesExpanded());
    }
}
