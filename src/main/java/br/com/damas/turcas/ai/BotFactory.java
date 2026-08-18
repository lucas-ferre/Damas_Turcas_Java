package br.com.damas.turcas.ai;

import br.com.damas.turcas.ai.astar.AStarSolver;
import br.com.damas.turcas.ai.astar.SearchStats;
import br.com.damas.turcas.ai.hillclimbing.ClimbStats;
import br.com.damas.turcas.ai.hillclimbing.HillClimber;
import br.com.damas.turcas.ai.hybrid.HybridBot;
import br.com.damas.turcas.ai.mdp.MDPSolver;
import br.com.damas.turcas.ai.mdp.MDPStats;
import br.com.damas.turcas.game.Board;

public final class BotFactory {

    private BotFactory() {}

    public static Bot createBot(BotType type, int difficulty) {
        int depth = 3;
        int nodes = 600;
        int restarts = 20;
        int steps = 15;

        if (difficulty == 1) {
            depth = 2;
            nodes = 250;
            restarts = 10;
            steps = 8;
        } else if (difficulty == 3) {
            depth = 4;
            nodes = 1200;
            restarts = 40;
            steps = 25;
        }

        switch (type) {
            case MDP: {
                final MDPSolver solver = new MDPSolver(depth, 0.90);
                return new Bot() {
                    @Override
                    public String getName() {
                        return "MDP-Markov";
                    }

                    @Override
                    public BotType getType() {
                        return BotType.MDP;
                    }

                    @Override
                    public BotResult selectMove(Board board) {
                        MDPStats stats = solver.findBestMove(board, board.getTurn());
                        return new BotResult(stats.getBestMove(), solver.formatStats(stats));
                    }
                };
            }
            case A_STAR: {
                final AStarSolver solver = new AStarSolver(nodes, depth);
                return new Bot() {
                    @Override
                    public String getName() {
                        return "A*-Tactical";
                    }

                    @Override
                    public BotType getType() {
                        return BotType.A_STAR;
                    }

                    @Override
                    public BotResult selectMove(Board board) {
                        SearchStats stats = solver.findBestMove(board, board.getTurn());
                        return new BotResult(stats.getBestMove(), solver.formatStats(stats));
                    }
                };
            }
            case HILL_CLIMBING: {
                final HillClimber solver = new HillClimber(restarts, steps);
                return new Bot() {
                    @Override
                    public String getName() {
                        return "HillClimber";
                    }

                    @Override
                    public BotType getType() {
                        return BotType.HILL_CLIMBING;
                    }

                    @Override
                    public BotResult selectMove(Board board) {
                        ClimbStats stats = solver.findBestMove(board, board.getTurn());
                        return new BotResult(stats.getBestMove(), solver.formatStats(stats));
                    }
                };
            }
            case HYBRID:
            default:
                return new HybridBot();
        }
    }
}
