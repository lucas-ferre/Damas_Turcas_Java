package br.com.damas.turcas.ai.hybrid;

import br.com.damas.turcas.ai.Bot;
import br.com.damas.turcas.ai.BotResult;
import br.com.damas.turcas.ai.BotType;
import br.com.damas.turcas.ai.astar.AStarSolver;
import br.com.damas.turcas.ai.astar.SearchStats;
import br.com.damas.turcas.ai.hillclimbing.ClimbStats;
import br.com.damas.turcas.ai.hillclimbing.HillClimber;
import br.com.damas.turcas.ai.mdp.MDPSolver;
import br.com.damas.turcas.ai.mdp.MDPStats;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.RulesEngine;

import java.util.List;

public final class HybridBot implements Bot {
    private final MDPSolver mdpSolver;
    private final AStarSolver aStarSolver;
    private final HillClimber hillClimber;
    private final RulesEngine rules;

    public HybridBot() {
        this.mdpSolver = new MDPSolver(3, 0.90);
        this.aStarSolver = new AStarSolver(700, 4);
        this.hillClimber = new HillClimber(25, 20);
        this.rules = new RulesEngine();
    }

    @Override
    public String getName() {
        return "Mestre-Híbrido";
    }

    @Override
    public BotType getType() {
        return BotType.HYBRID;
    }

    @Override
    public BotResult selectMove(Board board) {
        List<Move> legalMoves = rules.getLegalMoves(board, board.getTurn());
        if (legalMoves.isEmpty()) {
            return new BotResult(null, "Sem movimentos");
        }
        if (legalMoves.size() == 1) {
            return new BotResult(legalMoves.get(0), "Jogada única obrigatória");
        }

        for (Move lm : legalMoves) {
            if (lm.isCapture()) {
                SearchStats stats = aStarSolver.findBestMove(board, board.getTurn());
                return new BotResult(stats.getBestMove(), "Híbrido: A* Tático | " + aStarSolver.formatStats(stats));
            }
        }

        MDPStats sMDP = mdpSolver.findBestMove(board, board.getTurn());
        ClimbStats sHC = hillClimber.findBestMove(board, board.getTurn());

        if (sMDP.getBestMove() != null && sMDP.getBestMove().equals(sHC.getBestMove())) {
            String info = String.format("Híbrido: Consenso MDP & HC (Utilidade: %.1f)", sMDP.getExpectedUtility());
            return new BotResult(sMDP.getBestMove(), info);
        }

        String info = String.format("Híbrido: Decisão MDP (Util: %.1f, HC: %.1f)",
                sMDP.getExpectedUtility(), sHC.getBestEvaluation());
        return new BotResult(sMDP.getBestMove(), info);
    }
}
