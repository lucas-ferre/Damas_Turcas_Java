package br.com.damas.turcas.ai.hillclimbing;

import br.com.damas.turcas.ai.evaluation.Evaluator;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.RulesEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class HillClimber {
    private final Evaluator evaluator;
    private final RulesEngine rules;
    private final int maxRestarts;
    private final int maxSteps;
    private final Random random;

    public HillClimber(int maxRestarts, int maxSteps) {
        this.evaluator = new Evaluator();
        this.rules = new RulesEngine();
        this.maxRestarts = maxRestarts > 0 ? maxRestarts : 20;
        this.maxSteps = maxSteps > 0 ? maxSteps : 15;
        this.random = new Random();
    }

    public ClimbStats findBestMove(Board b, PieceColor color) {
        List<Move> moves = rules.getLegalMoves(b, color);
        if (moves.isEmpty()) {
            return new ClimbStats(null, 0.0, 0, 0);
        }

        if (moves.size() == 1) {
            Move singleMove = moves.get(0);
            return new ClimbStats(singleMove, evaluator.evaluate(b, color), 1, 1);
        }

        Move globalBestMove = moves.get(0);
        double globalBestScore = -Double.MAX_VALUE;
        int totalIters = 0;

        for (int restart = 0; restart < maxRestarts; restart++) {
            int startIdx = random.nextInt(moves.size());
            Move currentMove = moves.get(startIdx);
            double currentScore = evaluateMovePlan(b, currentMove, color);

            for (int step = 0; step < maxSteps; step++) {
                totalIters++;
                boolean improved = false;
                List<Move> neighbors = getNeighborMoves(moves, currentMove);

                for (Move nbMove : neighbors) {
                    double nbScore = evaluateMovePlan(b, nbMove, color);
                    if (nbScore > currentScore) {
                        currentScore = nbScore;
                        currentMove = nbMove;
                        improved = true;
                        break;
                    }
                }

                if (!improved) {
                    break;
                }
            }

            if (currentScore > globalBestScore) {
                globalBestScore = currentScore;
                globalBestMove = currentMove;
            }
        }

        return new ClimbStats(globalBestMove, globalBestScore, maxRestarts, totalIters);
    }

    private double evaluateMovePlan(Board b, Move m, PieceColor color) {
        Board nb = b.clone();
        nb.applyMove(m);

        double score = evaluator.evaluate(nb, color);
        if (m.isCapture()) {
            score += m.getCaptureCount() * 80.0;
        }
        if (m.isPromotion()) {
            score += 150.0;
        }

        List<Move> oppMoves = rules.getLegalMoves(nb, color.opponent());
        if (!oppMoves.isEmpty()) {
            double worstOppResponse = Double.MAX_VALUE;
            for (Move om : oppMoves) {
                Board onb = nb.clone();
                onb.applyMove(om);
                double eval = evaluator.evaluate(onb, color);
                if (eval < worstOppResponse) {
                    worstOppResponse = eval;
                }
            }
            score = score * 0.4 + worstOppResponse * 0.6;
        }

        return score;
    }

    private List<Move> getNeighborMoves(List<Move> allMoves, Move current) {
        List<Move> neighbors = new ArrayList<>();
        for (Move m : allMoves) {
            if (!m.equals(current)) {
                neighbors.add(m);
            }
        }
        Collections.shuffle(neighbors, random);
        return neighbors;
    }

    public String formatStats(ClimbStats stats) {
        return String.format("Hill-Climbing: Score = %.1f | Restarts = %d | Iters = %d",
                stats.getBestEvaluation(), stats.getRestartsDone(), stats.getTotalIterations());
    }
}
