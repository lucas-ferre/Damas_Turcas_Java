package br.com.damas.turcas.ai.mdp;

import br.com.damas.turcas.ai.evaluation.Evaluator;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.RulesEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MDPSolver {
    private final Evaluator evaluator;
    private final RulesEngine rules;
    private final double gamma;
    private final int maxDepth;
    private final double temperature;

    public MDPSolver(int maxDepth, double gamma) {
        this.evaluator = new Evaluator();
        this.rules = new RulesEngine();
        this.maxDepth = maxDepth > 0 ? maxDepth : 3;
        this.gamma = (gamma > 0.0 && gamma <= 1.0) ? gamma : 0.90;
        this.temperature = 50.0;
    }

    public MDPStats findBestMove(Board b, PieceColor color) {
        List<Move> moves = rules.getLegalMoves(b, color);
        Map<String, Double> actionValues = new HashMap<>();

        if (moves.isEmpty()) {
            return new MDPStats(null, 0.0, 0, actionValues);
        }

        if (moves.size() == 1) {
            Move singleMove = moves.get(0);
            double eval = evaluator.evaluate(b, color);
            actionValues.put(singleMove.format(b.getSize()), eval);
            return new MDPStats(singleMove, eval, 1, actionValues);
        }

        double bestUtility = -Double.MAX_VALUE;
        Move bestMove = moves.get(0);
        int totalStatesCount = 0;

        for (Move m : moves) {
            Board nextBoard = b.clone();
            nextBoard.applyMove(m);

            double reward = calculateImmediateReward(b, nextBoard, m, color);
            double[] future = evaluateState(nextBoard, color, 1);
            totalStatesCount += (int) future[1];

            double qVal = reward + gamma * future[0];
            actionValues.put(m.format(b.getSize()), qVal);

            if (qVal > bestUtility) {
                bestUtility = qVal;
                bestMove = m;
            }
        }

        return new MDPStats(bestMove, bestUtility, totalStatesCount, actionValues);
    }

    private double calculateImmediateReward(Board before, Board after, Move m, PieceColor color) {
        double reward = 0.0;
        if (m.isCapture()) {
            reward += m.getCaptureCount() * 120.0;
        }
        if (m.isPromotion()) {
            reward += 200.0;
        }
        double diff = evaluator.evaluate(after, color) - evaluator.evaluate(before, color);
        reward += diff * 0.5;
        return reward;
    }

    private double[] evaluateState(Board b, PieceColor color, int depth) {
        if (depth >= maxDepth) {
            return new double[]{evaluator.evaluate(b, color), 1.0};
        }

        if (rules.isGameOver(b)) {
            PieceColor winner = rules.getWinner(b);
            if (winner == color) {
                return new double[]{10000.0, 1.0};
            } else if (winner == color.opponent()) {
                return new double[]{-10000.0, 1.0};
            }
            return new double[]{0.0, 1.0};
        }

        boolean isMyTurn = (b.getTurn() == color);
        List<Move> moves = rules.getLegalMoves(b, b.getTurn());
        if (moves.isEmpty()) {
            return new double[]{isMyTurn ? -10000.0 : 10000.0, 1.0};
        }

        int statesCount = 1;

        if (isMyTurn) {
            double maxVal = -Double.MAX_VALUE;
            for (Move m : moves) {
                Board nb = b.clone();
                nb.applyMove(m);
                double reward = calculateImmediateReward(b, nb, m, color);
                double[] future = evaluateState(nb, color, depth + 1);
                statesCount += (int) future[1];
                double val = reward + gamma * future[0];
                if (val > maxVal) {
                    maxVal = val;
                }
            }
            return new double[]{maxVal, statesCount};
        }

        double[] utilities = new double[moves.size()];
        double maxUtil = -Double.MAX_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            Board nb = b.clone();
            nb.applyMove(moves.get(i));
            double val = evaluator.evaluate(nb, color.opponent());
            utilities[i] = val;
            if (val > maxUtil) {
                maxUtil = val;
            }
        }

        double expSum = 0.0;
        double[] expVals = new double[moves.size()];
        for (int i = 0; i < utilities.length; i++) {
            double ev = Math.exp((utilities[i] - maxUtil) / temperature);
            expVals[i] = ev;
            expSum += ev;
        }

        double expectedVal = 0.0;
        for (int i = 0; i < moves.size(); i++) {
            double prob = expSum > 0.0 ? expVals[i] / expSum : 1.0 / moves.size();
            Board nb = b.clone();
            nb.applyMove(moves.get(i));
            double[] future = evaluateState(nb, color, depth + 1);
            statesCount += (int) future[1];
            expectedVal += prob * future[0];
        }

        return new double[]{expectedVal, statesCount};
    }

    public String formatStats(MDPStats stats) {
        return String.format("MDP: Utilidade Esperada = %.1f | Estados = %d", stats.getExpectedUtility(), stats.getStatesEvaluated());
    }
}
