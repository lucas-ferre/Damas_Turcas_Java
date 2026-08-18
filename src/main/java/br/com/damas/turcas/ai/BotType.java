package br.com.damas.turcas.ai;

public enum BotType {
    MDP("Processo de Decisão de Markov (MDP)"),
    A_STAR("Busca A* Tática"),
    HILL_CLIMBING("Hill Climbing (Random Restarts)"),
    HYBRID("Modo Híbrido Mestre (MDP + A* + HC)");

    private final String displayName;

    BotType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
