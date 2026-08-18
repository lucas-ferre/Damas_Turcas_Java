package br.com.damas.turcas.game;

public enum PieceColor {
    NONE,
    WHITE,
    BLACK;

    public PieceColor opponent() {
        if (this == WHITE) {
            return BLACK;
        }
        if (this == BLACK) {
            return WHITE;
        }
        return NONE;
    }

    public String getDisplayName() {
        switch (this) {
            case WHITE:
                return "Brancas";
            case BLACK:
                return "Pretas";
            default:
                return "Nenhuma";
        }
    }
}
