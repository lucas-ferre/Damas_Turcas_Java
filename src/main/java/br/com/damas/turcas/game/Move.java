package br.com.damas.turcas.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Move {
    private final Position from;
    private final Position to;
    private final List<Position> path;
    private final List<Position> captures;
    private final boolean isCapture;
    private final boolean promotion;

    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
        this.path = List.of(from, to);
        this.captures = Collections.emptyList();
        this.isCapture = false;
        this.promotion = false;
    }

    public Move(Position from, Position to, List<Position> path, List<Position> captures, boolean isCapture, boolean promotion) {
        this.from = from;
        this.to = to;
        this.path = path != null ? List.copyOf(path) : List.of(from, to);
        this.captures = captures != null ? List.copyOf(captures) : Collections.emptyList();
        this.isCapture = isCapture;
        this.promotion = promotion;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public List<Position> getPath() {
        return path;
    }

    public List<Position> getCaptures() {
        return captures;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public int getCaptureCount() {
        return captures.size();
    }

    public Move withPromotion(boolean promo) {
        return new Move(from, to, path, captures, isCapture, promo);
    }

    public String format(int size) {
        if (path.isEmpty()) {
            return from.toAlgebraic(size) + " -> " + to.toAlgebraic(size);
        }
        StringBuilder sb = new StringBuilder();
        String sep = isCapture ? " x " : " -> ";
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(path.get(i).toAlgebraic(size));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return isCapture == move.isCapture &&
                Objects.equals(from, move.from) &&
                Objects.equals(to, move.to) &&
                Objects.equals(captures, move.captures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, captures, isCapture);
    }

    @Override
    public String toString() {
        return "Move{" + from + " -> " + to + ", cap=" + captures.size() + "}";
    }
}
