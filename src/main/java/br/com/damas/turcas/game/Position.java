package br.com.damas.turcas.game;

public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isValid(int size) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public String toAlgebraic(int size) {
        if (!isValid(size)) {
            return "??";
        }
        char colChar = (char) ('A' + col);
        int rank = size - row;
        return "" + colChar + rank;
    }

    public static Position parseAlgebraic(String s, int size) {
        if (s == null) {
            throw new IllegalArgumentException("Posição nula");
        }
        String clean = s.trim().toUpperCase();
        if (clean.length() < 2 || clean.length() > 3) {
            throw new IllegalArgumentException("Formato de coordenada inválido: " + s);
        }
        char colChar = clean.charAt(0);
        if (colChar < 'A' || colChar >= (char) ('A' + size)) {
            throw new IllegalArgumentException("Coluna fora do tabuleiro: " + colChar);
        }
        int col = colChar - 'A';
        String rowStr = clean.substring(1);
        int rank;
        try {
            rank = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número de linha inválido: " + rowStr);
        }
        if (rank < 1 || rank > size) {
            throw new IllegalArgumentException("Linha fora dos limites: " + rank);
        }
        int row = size - rank;
        return new Position(row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
