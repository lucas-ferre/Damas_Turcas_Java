package br.com.damas.turcas.game;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NotationParser {
    private final int size;
    private static final Pattern COORD_PATTERN = Pattern.compile("[a-zA-Z][0-9]+");

    public NotationParser(int size) {
        this.size = size;
    }

    public Move parseInput(String input, List<Move> legalMoves) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Entrada vazia.");
        }

        String norm = input.trim().toLowerCase();
        norm = norm.replace("para", " ");
        norm = norm.replace("to", " ");
        norm = norm.replace("->", " ");
        norm = norm.replace("-", " ");
        norm = norm.replace("x", " ");
        norm = norm.replace(":", " ");

        Matcher matcher = COORD_PATTERN.matcher(norm);
        List<String> matchedTokens = new ArrayList<>();
        while (matcher.find()) {
            matchedTokens.add(matcher.group());
        }

        if (matchedTokens.size() < 2) {
            throw new IllegalArgumentException("Formato inválido. Digite coordenadas como 'E3 para E4' ou 'C3 D3'.");
        }

        List<Position> positions = new ArrayList<>();
        for (String token : matchedTokens) {
            positions.add(Position.parseAlgebraic(token, size));
        }

        Position from = positions.get(0);
        Position to = positions.get(positions.size() - 1);

        List<Move> matchedMoves = new ArrayList<>();
        for (Move lm : legalMoves) {
            if (lm.getFrom().equals(from) && lm.getTo().equals(to)) {
                if (positions.size() > 2) {
                    if (pathMatches(lm.getPath(), positions)) {
                        matchedMoves.add(lm);
                    }
                } else {
                    matchedMoves.add(lm);
                }
            }
        }

        if (matchedMoves.isEmpty()) {
            boolean pieceHasMove = false;
            for (Move lm : legalMoves) {
                if (lm.getFrom().equals(from)) {
                    pieceHasMove = true;
                    break;
                }
            }

            if (!pieceHasMove) {
                boolean hasCaptures = false;
                for (Move lm : legalMoves) {
                    if (lm.isCapture()) {
                        hasCaptures = true;
                        break;
                    }
                }
                if (hasCaptures) {
                    throw new IllegalArgumentException("Jogada ilegal: há capturas obrigatórias a serem feitas (Lei da Maioria).");
                }
                throw new IllegalArgumentException("Jogada ilegal: a peça em " + from.toAlgebraic(size) + " não possui movimentos válidos.");
            }

            throw new IllegalArgumentException("Movimento inválido de " + from.toAlgebraic(size) + " para " + to.toAlgebraic(size) + ".");
        }

        Move best = matchedMoves.get(0);
        for (int i = 1; i < matchedMoves.size(); i++) {
            if (matchedMoves.get(i).getCaptureCount() > best.getCaptureCount()) {
                best = matchedMoves.get(i);
            }
        }

        return best;
    }

    private boolean pathMatches(List<Position> fullPath, List<Position> inputPath) {
        if (inputPath.size() > fullPath.size()) {
            return false;
        }
        for (int i = 0; i < inputPath.size(); i++) {
            if (i == 0 && !fullPath.get(0).equals(inputPath.get(0))) {
                return false;
            }
            if (i == inputPath.size() - 1 && !fullPath.get(fullPath.size() - 1).equals(inputPath.get(inputPath.size() - 1))) {
                return false;
            }
        }
        return true;
    }

    public String formatMove(Move m) {
        return m.format(size);
    }
}
