package br.com.damas.turcas.game;

public enum PieceType {
    EMPTY,
    MAN,
    KING;

    public String getDisplayName() {
        switch (this) {
            case MAN:
                return "Pedra";
            case KING:
                return "Dama";
            default:
                return "Vazia";
        }
    }
}
