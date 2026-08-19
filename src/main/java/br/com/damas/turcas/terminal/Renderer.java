package br.com.damas.turcas.terminal;

import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.Piece;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.Position;

import java.util.List;

public final class Renderer {
    private final int size;

    public Renderer(int size) {
        this.size = size;
    }

    public String renderGame(Board b, List<String> history, String aiName, String aiEval, String message) {
        return renderGame(b, history, aiName, aiEval, message, PieceColor.WHITE);
    }

    public String renderGame(Board b, List<String> history, String aiName, String aiEval, String message, PieceColor playerColor) {
        String[] boardLines = renderBoardLines(b);
        String[] hudLines = renderHUDLines(b, history, aiName, aiEval, playerColor);

        int maxLines = Math.max(boardLines.length, hudLines.length);
        int boardWidth = boardLines.length > 0 ? Table.visibleLen(boardLines[0]) : 0;
        int hudWidth = hudLines.length > 0 ? Table.visibleLen(hudLines[0]) : 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLines; i++) {
            String bLine = i < boardLines.length ? boardLines[i] : " ".repeat(boardWidth);
            String hLine = i < hudLines.length ? hudLines[i] : " ".repeat(hudWidth);

            sb.append(bLine);
            sb.append("   ");
            sb.append(hLine);
            sb.append("\n");
        }

        if (message != null && !message.isEmpty()) {
            int totalWidth = Math.max(60, boardWidth + 3 + hudWidth);
            sb.append(renderStatusBanner(message, totalWidth));
            sb.append("\n");
        }

        return sb.toString();
    }

    private String[] renderBoardLines(Board b) {
        Table table = new Table();
        table.setBorderStyle(BorderStyle.UNICODE_DOUBLE);
        table.setPadding(0);

        String[] headers = new String[b.getSize() + 2];
        headers[0] = "  ";
        for (int c = 0; c < b.getSize(); c++) {
            headers[c + 1] = " " + (char) ('A' + c) + " ";
        }
        headers[b.getSize() + 1] = "  ";
        table.setHeaders(headers);

        for (int row = 0; row < b.getSize(); row++) {
            int rank = b.getSize() - row;
            String[] cells = new String[b.getSize() + 2];
            cells[0] = String.format("%2d", rank);
            cells[b.getSize() + 1] = String.format("%-2d", rank);

            for (int col = 0; col < b.getSize(); col++) {
                Position pos = new Position(row, col);
                Piece piece = b.get(pos);
                boolean isLight = (row + col) % 2 == 0;
                cells[col + 1] = formatPieceCell(piece, pos, isLight, b.getLastMove());
            }

            table.addRow(cells);
        }

        String rendered = table.render();
        return rendered.split("\n");
    }

    private String formatPieceCell(Piece p, Position pos, boolean isLight, Move lastMove) {
        String bg = isLight ? Colors.BG_LIGHT_SQUARE : Colors.BG_DARK_SQUARE;

        if (lastMove != null) {
            if (lastMove.getFrom().equals(pos) || lastMove.getTo().equals(pos)) {
                bg = Colors.BG_LAST_MOVE;
            }
        }

        String sym = "   ";
        if (!p.isEmpty()) {
            if (p.isWhite()) {
                if (p.isKing()) {
                    sym = " " + Colors.FG_BRIGHT_YELLOW + Colors.BOLD + "★" + Colors.RESET + bg + " ";
                } else {
                    sym = " " + Colors.FG_BRIGHT_CYAN + Colors.BOLD + "●" + Colors.RESET + bg + " ";
                }
            } else if (p.isBlack()) {
                if (p.isKing()) {
                    sym = " " + Colors.FG_BRIGHT_MAGENTA + Colors.BOLD + "☆" + Colors.RESET + bg + " ";
                } else {
                    sym = " " + Colors.FG_BRIGHT_RED + Colors.BOLD + "○" + Colors.RESET + bg + " ";
                }
            }
        }

        return bg + sym + Colors.RESET;
    }

    private String[] renderHUDLines(Board b, List<String> history, String aiName, String aiEval, PieceColor playerColor) {
        Table hud = new Table();
        hud.setBorderStyle(BorderStyle.UNICODE);
        hud.setTitle(" PAINEL DE CONTROLE ");
        hud.setHeaders("PROPRIEDADE", "VALOR");
        hud.setAlignments(Alignment.LEFT, Alignment.LEFT);

        PieceColor safePlayerColor = (playerColor != null && playerColor != PieceColor.NONE) ? playerColor : PieceColor.WHITE;
        PieceColor aiColor = safePlayerColor.opponent();

        String turnStr;
        if (b.getTurn() == safePlayerColor) {
            String sym = safePlayerColor == PieceColor.WHITE ? "Brancas ●" : "Pretas ○";
            String col = safePlayerColor == PieceColor.WHITE ? Colors.FG_BRIGHT_CYAN : Colors.FG_BRIGHT_RED;
            turnStr = Colors.colorize(col + Colors.BOLD, "Jogador (" + sym + ")");
        } else {
            String sym = aiColor == PieceColor.WHITE ? "Brancas ●" : "Pretas ○";
            String col = aiColor == PieceColor.WHITE ? Colors.FG_BRIGHT_CYAN : Colors.FG_BRIGHT_RED;
            turnStr = Colors.colorize(col + Colors.BOLD, "IA " + aiName + " (" + sym + ")");
        }

        hud.addRow("Vez da Jogada", turnStr);
        hud.addRow("Motor de IA", Colors.colorize(Colors.FG_BRIGHT_YELLOW, aiName));
        hud.addRow("Dimensão", b.getSize() + "x" + b.getSize() + " (" + (b.getSize() * 2) + " peças cada)");

        if (safePlayerColor == PieceColor.WHITE) {
            hud.addRow("Brancas (Você)", "Total: " + b.getWhiteCount() + " | Damas: " + b.getWhiteKingCount());
            hud.addRow("Pretas (IA)", "Total: " + b.getBlackCount() + " | Damas: " + b.getBlackKingCount());
        } else {
            hud.addRow("Brancas (IA)", "Total: " + b.getWhiteCount() + " | Damas: " + b.getWhiteKingCount());
            hud.addRow("Pretas (Você)", "Total: " + b.getBlackCount() + " | Damas: " + b.getBlackKingCount());
        }

        hud.addRow("Total Jogadas", String.valueOf(b.getMoveCount()));

        if (aiEval != null && !aiEval.isEmpty()) {
            hud.addRow("Análise da IA", Colors.colorize(Colors.FG_BRIGHT_GREEN, aiEval));
        }

        int start = 0;
        if (history != null && history.size() > 3) {
            start = history.size() - 3;
        }

        if (history == null || history.isEmpty()) {
            hud.addRow("Histórico (3 últ.)", "(nenhuma jogada)");
        } else {
            List<String> histSlice = history.subList(start, history.size());
            for (int i = 0; i < histSlice.size(); i++) {
                int idx = start + i + 1;
                hud.addRow("Jogada #" + idx, histSlice.get(i));
            }
        }

        String rendered = hud.render();
        return rendered.split("\n");
    }

    private String renderStatusBanner(String message, int targetWidth) {
        Table banner = new Table();
        banner.setBorderStyle(BorderStyle.UNICODE);
        banner.setPadding(1);
        String coloredMsg = Colors.colorize(Colors.FG_BRIGHT_YELLOW + Colors.BOLD, "» " + message);
        banner.addRow(coloredMsg);
        return banner.render().stripTrailing();
    }
}
