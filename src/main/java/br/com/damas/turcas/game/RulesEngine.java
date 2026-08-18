package br.com.damas.turcas.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RulesEngine {

    public List<Move> getLegalMoves(Board board, PieceColor color) {
        if (board == null || color == null || color == PieceColor.NONE) {
            return Collections.emptyList();
        }

        List<Move> allCaptures = new ArrayList<>();
        int maxCaptures = 0;
        int size = board.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Position pos = new Position(r, c);
                Piece p = board.get(pos);
                if (p.isEmpty() || p.getColor() != color) {
                    continue;
                }

                List<Move> pieceCaptures = getPieceCaptures(board, pos, p);
                for (Move capMove : pieceCaptures) {
                    int count = capMove.getCaptureCount();
                    if (count > maxCaptures) {
                        maxCaptures = count;
                        allCaptures.clear();
                        allCaptures.add(capMove);
                    } else if (count == maxCaptures && count > 0) {
                        allCaptures.add(capMove);
                    }
                }
            }
        }

        if (maxCaptures > 0) {
            return allCaptures;
        }

        List<Move> simpleMoves = new ArrayList<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Position pos = new Position(r, c);
                Piece p = board.get(pos);
                if (p.isEmpty() || p.getColor() != color) {
                    continue;
                }
                simpleMoves.addAll(getPieceSimpleMoves(board, pos, p));
            }
        }

        return simpleMoves;
    }

    public List<Move> getPieceSimpleMoves(Board board, Position pos, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int size = board.getSize();

        if (piece.isMan()) {
            int forwardDir = piece.isWhite() ? -1 : 1;
            int[][] directions = {
                {forwardDir, 0},
                {0, -1},
                {0, 1}
            };

            for (int[] d : directions) {
                Position target = new Position(pos.getRow() + d[0], pos.getCol() + d[1]);
                if (target.isValid(size) && board.get(target).isEmpty()) {
                    moves.add(new Move(pos, target));
                }
            }
        } else if (piece.isKing()) {
            int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
            };

            for (int[] d : directions) {
                int step = 1;
                while (true) {
                    Position target = new Position(pos.getRow() + d[0] * step, pos.getCol() + d[1] * step);
                    if (!target.isValid(size) || !board.get(target).isEmpty()) {
                        break;
                    }
                    moves.add(new Move(pos, target));
                    step++;
                }
            }
        }

        return moves;
    }

    public List<Move> getPieceCaptures(Board board, Position pos, Piece piece) {
        List<Move> results = new ArrayList<>();
        Board copyBoard = board.clone();
        List<Position> path = new ArrayList<>();
        path.add(pos);
        List<Position> captures = new ArrayList<>();

        exploreCaptures(copyBoard, pos, piece, path, captures, results);
        return results;
    }

    private void exploreCaptures(Board b, Position currentPos, Piece p, List<Position> path,
                                 List<Position> captures, List<Move> results) {
        boolean foundSubCapture = false;
        int size = b.getSize();

        if (p.isMan()) {
            int forwardDir = p.isWhite() ? -1 : 1;
            int[][] directions = {
                {forwardDir, 0},
                {0, -1},
                {0, 1}
            };

            for (int[] d : directions) {
                Position jumpOver = new Position(currentPos.getRow() + d[0], currentPos.getCol() + d[1]);
                Position landPos = new Position(currentPos.getRow() + 2 * d[0], currentPos.getCol() + 2 * d[1]);

                if (!landPos.isValid(size) || !jumpOver.isValid(size)) {
                    continue;
                }

                Piece overPiece = b.get(jumpOver);
                if (overPiece.isEmpty() || overPiece.getColor() != p.getColor().opponent()) {
                    continue;
                }

                Piece landPiece = b.get(landPos);
                if (!landPiece.isEmpty()) {
                    continue;
                }

                foundSubCapture = true;

                b.set(jumpOver, Piece.EMPTY);
                b.set(currentPos, Piece.EMPTY);

                boolean promoted = false;
                Piece nextPiece = p;
                if ((p.isWhite() && landPos.getRow() == 0) || (p.isBlack() && landPos.getRow() == size - 1)) {
                    nextPiece = p.promote();
                    promoted = true;
                }
                b.set(landPos, nextPiece);

                List<Position> newPath = new ArrayList<>(path);
                newPath.add(landPos);
                List<Position> newCaptures = new ArrayList<>(captures);
                newCaptures.add(jumpOver);

                exploreCaptures(b, landPos, nextPiece, newPath, newCaptures, results);

                b.set(landPos, Piece.EMPTY);
                b.set(currentPos, p);
                b.set(jumpOver, overPiece);
            }
        } else if (p.isKing()) {
            int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
            };

            for (int[] d : directions) {
                int step = 1;
                Position enemyPos = null;
                Piece enemyPiece = null;

                while (true) {
                    Position checkPos = new Position(currentPos.getRow() + d[0] * step, currentPos.getCol() + d[1] * step);
                    if (!checkPos.isValid(size)) {
                        break;
                    }
                    Piece checkPiece = b.get(checkPos);
                    if (checkPiece.isEmpty()) {
                        step++;
                        continue;
                    }
                    if (checkPiece.getColor() == p.getColor()) {
                        break;
                    }
                    if (checkPiece.getColor() == p.getColor().opponent()) {
                        enemyPos = checkPos;
                        enemyPiece = checkPiece;
                        break;
                    }
                    step++;
                }

                if (enemyPos != null) {
                    int landStep = 1;
                    while (true) {
                        Position landPos = new Position(enemyPos.getRow() + d[0] * landStep, enemyPos.getCol() + d[1] * landStep);
                        if (!landPos.isValid(size)) {
                            break;
                        }
                        Piece landPiece = b.get(landPos);
                        if (!landPiece.isEmpty()) {
                            break;
                        }

                        foundSubCapture = true;

                        b.set(enemyPos, Piece.EMPTY);
                        b.set(currentPos, Piece.EMPTY);
                        b.set(landPos, p);

                        List<Position> newPath = new ArrayList<>(path);
                        newPath.add(landPos);
                        List<Position> newCaptures = new ArrayList<>(captures);
                        newCaptures.add(enemyPos);

                        exploreCaptures(b, landPos, p, newPath, newCaptures, results);

                        b.set(landPos, Piece.EMPTY);
                        b.set(currentPos, p);
                        b.set(enemyPos, enemyPiece);

                        landStep++;
                    }
                }
            }
        }

        if (!foundSubCapture && !captures.isEmpty()) {
            boolean isProm = (p.isKing() && !b.get(path.get(0)).isKing());
            results.add(new Move(path.get(0), currentPos, path, captures, true, isProm));
        }
    }

    public static final int MAX_HALF_MOVES = 92;

    public boolean isGameOver(Board board) {
        if (board.getWhiteCount() == 0 || board.getBlackCount() == 0) {
            return true;
        }
        if (getLegalMoves(board, board.getTurn()).isEmpty()) {
            return true;
        }
        return board.getHalfMoveClock() >= MAX_HALF_MOVES;
    }

    public PieceColor getWinner(Board board) {
        if (board.getWhiteCount() == 0) {
            return PieceColor.BLACK;
        }
        if (board.getBlackCount() == 0) {
            return PieceColor.WHITE;
        }
        List<Move> legalMoves = getLegalMoves(board, board.getTurn());
        if (legalMoves.isEmpty()) {
            return board.getTurn().opponent();
        }
        if (board.getHalfMoveClock() >= MAX_HALF_MOVES) {
            return PieceColor.NONE;
        }
        return PieceColor.NONE;
    }
}
