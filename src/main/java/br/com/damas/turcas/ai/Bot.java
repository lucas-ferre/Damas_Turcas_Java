package br.com.damas.turcas.ai;

import br.com.damas.turcas.game.Board;

public interface Bot {
    String getName();
    BotType getType();
    BotResult selectMove(Board board);
}
