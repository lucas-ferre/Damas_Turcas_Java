package br.com.damas.turcas.ai;

import br.com.damas.turcas.game.Move;

public final class BotResult {
    private final Move move;
    private final String evaluationInfo;

    public BotResult(Move move, String evaluationInfo) {
        this.move = move;
        this.evaluationInfo = evaluationInfo;
    }

    public Move getMove() {
        return move;
    }

    public String getEvaluationInfo() {
        return evaluationInfo;
    }
}
