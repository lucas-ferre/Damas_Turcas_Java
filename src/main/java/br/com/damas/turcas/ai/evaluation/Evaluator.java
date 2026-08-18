package br.com.damas.turcas.ai.evaluation;

import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Piece;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.Position;
import br.com.damas.turcas.game.RulesEngine;

public final class Evaluator {
    public static final double MAN_VALUE = 100.0;
    public static final double KING_VALUE = 350.0;

    private final RulesEngine rules;

    public Evaluator() {
        this.rules = new RulesEngine();
    }

    public double evaluate(Board b, PieceColor color) {
        if (rules.isGameOver(b)) {
            PieceColor winner = rules.getWinner(b);
            if (winner == color) {
                return 100000.0;
            } else if (winner == color.opponent()) {
                return -100000.0;
            }
            return 0.0;
        }

        double score = 0.0;
        int size = b.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Piece p = b.get(r, c);
                if (p.isEmpty()) {
                    continue;
                }

                double val = evaluatePiece(b, p, new Position(r, c));
                if (p.getColor() == color) {
                    score += val;
                } else {
                    score -= val;
                }
            }
        }

        int myMoves = rules.getLegalMoves(b, color).size();
        int oppMoves = rules.getLegalMoves(b, color.opponent()).size();
        score += (myMoves - oppMoves) * 5.0;

        return score;
    }

    private double evaluatePiece(Board b, Piece p, Position pos) {
        int size = b.getSize();
        double val = p.isKing() ? KING_VALUE : MAN_VALUE;

        double centerMin = (double) size / 2.0 - 1.0;
        double centerMax = (double) size / 2.0;
        double distToCenter = Math.abs((double) pos.getRow() - (double) size / 2.0 + 0.5)
                            + Math.abs((double) pos.getCol() - (double) size / 2.0 + 0.5);
        val += ((double) size - distToCenter) * 3.0;

        if (pos.getRow() >= centerMin && pos.getRow() <= centerMax &&
            pos.getCol() >= centerMin && pos.getCol() <= centerMax) {
            val += 15.0;
        }

        if (p.isMan()) {
            if (p.isWhite()) {
                double progress = (size - 1 - pos.getRow());
                val += progress * 8.0;
                if (pos.getRow() >= size - 2) {
                    val += 10.0;
                }
            } else if (p.isBlack()) {
                double progress = pos.getRow();
                val += progress * 8.0;
                if (pos.getRow() <= 1) {
                    val += 10.0;
                }
            }
        } else if (p.isKing()) {
            if (pos.getRow() == size / 2 || pos.getCol() == size / 2) {
                val += 20.0;
            }
        }

        return val;
    }
}
