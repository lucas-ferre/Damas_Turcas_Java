package br.com.damas.turcas.game;

import java.util.Objects;

public final class Piece {
    private final PieceColor color;
    private final PieceType type;

    public static final Piece EMPTY = new Piece(PieceColor.NONE, PieceType.EMPTY);

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isEmpty() {
        return color == PieceColor.NONE || type == PieceType.EMPTY;
    }

    public boolean isWhite() {
        return color == PieceColor.WHITE;
    }

    public boolean isBlack() {
        return color == PieceColor.BLACK;
    }

    public boolean isKing() {
        return type == PieceType.KING;
    }

    public boolean isMan() {
        return type == PieceType.MAN;
    }

    public Piece promote() {
        if (isEmpty()) {
            return this;
        }
        return new Piece(this.color, PieceType.KING);
    }

    public String getSymbol() {
        if (isEmpty()) {
            return " ";
        }
        if (color == PieceColor.WHITE) {
            return isKing() ? "★" : "●";
        }
        return isKing() ? "☆" : "○";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.color && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return ".";
        }
        return (color == PieceColor.WHITE ? "W" : "B") + (type == PieceType.KING ? "K" : "M");
    }
}
