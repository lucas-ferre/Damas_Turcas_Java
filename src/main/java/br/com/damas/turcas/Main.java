package br.com.damas.turcas;

import br.com.damas.turcas.ai.Bot;
import br.com.damas.turcas.ai.BotFactory;
import br.com.damas.turcas.ai.BotResult;
import br.com.damas.turcas.ai.BotType;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.NotationParser;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.RulesEngine;
import br.com.damas.turcas.terminal.Colors;
import br.com.damas.turcas.terminal.Renderer;
import br.com.damas.turcas.terminal.Table;
import br.com.damas.turcas.terminal.BorderStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearScreen();
            printBanner();

            int size = selectBoardSize(scanner);
            BotSelection botSelection = selectBotAndDifficulty(scanner);

            Bot bot = BotFactory.createBot(botSelection.botType, botSelection.difficulty);
            Board board = new Board(size);
            RulesEngine rules = new RulesEngine();
            NotationParser parser = new NotationParser(size);
            Renderer renderer = new Renderer(size);

            List<String> history = new ArrayList<>();
            String lastAiEval = "";
            String statusMessage = "Jogo iniciado! Boa sorte.";

            while (true) {
                clearScreen();
                System.out.print(renderer.renderGame(board, history, bot.getName(), lastAiEval, statusMessage));
                statusMessage = "";

                if (rules.isGameOver(board)) {
                    printGameOver(rules.getWinner(board));
                    break;
                }

                if (board.getTurn() == PieceColor.WHITE) {
                    List<Move> legalMoves = rules.getLegalMoves(board, PieceColor.WHITE);
                    if (legalMoves.isEmpty()) {
                        statusMessage = "Você não possui movimentos legais.";
                        continue;
                    }

                    System.out.print(Colors.colorize(Colors.FG_BRIGHT_CYAN + Colors.BOLD,
                            "\nSua vez (Brancas)! Digite sua jogada (ex: E3 para E4 ou C3 D3) [ou '?', 'cls', 'sair']: "));
                    String input = scanner.nextLine();
                    if (input == null) {
                        break;
                    }

                    input = input.trim();
                    if (input.equalsIgnoreCase("sair") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                        System.out.println("\nPartida encerrada pelo jogador.");
                        return;
                    }

                    if (isHelpCommand(input)) {
                        statusMessage = formatHelpGuide(legalMoves, parser);
                        continue;
                    }

                    if (isClearCommand(input)) {
                        clearScreen();
                        statusMessage = "Tela limpa e estado do jogo atualizado.";
                        continue;
                    }

                    try {
                        Move move = parser.parseInput(input, legalMoves);
                        String moveFormatted = parser.formatMove(move);
                        history.add("Jogador: " + moveFormatted);
                        board.applyMove(move);
                    } catch (IllegalArgumentException e) {
                        statusMessage = "Erro: " + e.getMessage();
                    }
                } else {
                    System.out.print(Colors.colorize(Colors.FG_BRIGHT_RED + Colors.BOLD,
                            "\nIA pensando... calculando melhor jogada..."));
                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException ignored) {
                    }

                    BotResult result = bot.selectMove(board);
                    Move aiMove = result.getMove();
                    lastAiEval = result.getEvaluationInfo();

                    if (aiMove == null) {
                        statusMessage = "IA sem movimentos.";
                        continue;
                    }

                    String moveFormatted = parser.formatMove(aiMove);
                    history.add("IA (" + bot.getName() + "): " + moveFormatted);
                    board.applyMove(aiMove);
                }
            }

            System.out.print("\nDeseja jogar novamente? (S/N): ");
            String rematch = scanner.nextLine();
            if (rematch == null) {
                break;
            }
            rematch = rematch.trim().toUpperCase();
            if (!rematch.equals("S") && !rematch.equals("SIM") && !rematch.equals("Y") && !rematch.equals("YES")) {
                System.out.println("\nObrigado por jogar Damas Turcas! Até a próxima.");
                break;
            }
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void printBanner() {
        Table table = new Table();
        table.setBorderStyle(BorderStyle.UNICODE_DOUBLE);
        table.setTitle(" JOGO DE DAMAS TURCAS NO TERMINAL (JAVA) ");
        table.addRow(Colors.colorize(Colors.FG_BRIGHT_YELLOW + Colors.BOLD,
                "  Inteligência Artificial: MDP (Bellman), Busca A* e Hill Climbing  "));
        table.addRow("  Movimentos Ortogonais (Frente/Lados) & Notação Algébrica (ex: E3 para E4)  ");
        System.out.println(table.render());
    }

    private static int selectBoardSize(Scanner scanner) {
        Table menu = new Table();
        menu.setBorderStyle(BorderStyle.UNICODE);
        menu.setTitle(" DIMENSÃO DO TABULEIRO ");
        menu.setHeaders("OPÇÃO", "TAMANHO", "DESCRIÇÃO");
        menu.addRow("1", "8x8", "Tabuleiro Padrão de Damas Turcas (16 peças cada) [Recomendado]");
        menu.addRow("2", "10x10", "Tabuleiro Maior / Expandido (20 peças cada)");
        System.out.println(menu.render());

        while (true) {
            System.out.print("Escolha a dimensão do tabuleiro [1-2, padrão: 1]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("1")) {
                return 8;
            }
            if (input.equals("2")) {
                return 10;
            }
            System.out.println("Opção inválida. Tente novamente.");
        }
    }

    private static BotSelection selectBotAndDifficulty(Scanner scanner) {
        Table menuAI = new Table();
        menuAI.setBorderStyle(BorderStyle.UNICODE);
        menuAI.setTitle(" MOTOR DE INTELIGÊNCIA ARTIFICIAL ");
        menuAI.setHeaders("OPÇÃO", "MOTOR DE IA", "CARACTERÍSTICA");
        menuAI.addRow("1", "Modo Híbrido Mestre", "Combina A* (Táticas), MDP (Estratégia) e Hill Climbing [Recomendado]");
        menuAI.addRow("2", "Processo de Decisão de Markov (MDP)", "Modelagem probabilística com Softmax e Bellman Iteration");
        menuAI.addRow("3", "Busca A* (A-Star)", "Busca tática em árvore com PriorityQueue Min-Heap");
        menuAI.addRow("4", "Hill Climbing com Reinicialização", "Otimização heurística local com Random Restarts");
        System.out.println(menuAI.render());

        BotType chosenAI = BotType.HYBRID;
        while (true) {
            System.out.print("Escolha o Motor de IA do adversário [1-4, padrão: 1]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("1")) {
                chosenAI = BotType.HYBRID;
                break;
            }
            if (input.equals("2")) {
                chosenAI = BotType.MDP;
                break;
            }
            if (input.equals("3")) {
                chosenAI = BotType.A_STAR;
                break;
            }
            if (input.equals("4")) {
                chosenAI = BotType.HILL_CLIMBING;
                break;
            }
            System.out.println("Opção inválida. Tente novamente.");
        }

        Table menuDiff = new Table();
        menuDiff.setBorderStyle(BorderStyle.UNICODE);
        menuDiff.setTitle(" NÍVEL DE DIFICULDADE ");
        menuDiff.setHeaders("OPÇÃO", "NÍVEL", "PROFUNDIDADE");
        menuDiff.addRow("1", "Fácil", "Exploração rápida e baixa profundidade");
        menuDiff.addRow("2", "Médio", "Equilíbrio tático e estratégico [Padrão]");
        menuDiff.addRow("3", "Difícil", "Cálculo profundo de variantes e múltiplas reinicializações");
        System.out.println(menuDiff.render());

        int difficulty = 2;
        while (true) {
            System.out.print("Escolha a dificuldade [1-3, padrão: 2]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("2")) {
                difficulty = 2;
                break;
            }
            if (input.equals("1")) {
                difficulty = 1;
                break;
            }
            if (input.equals("3")) {
                difficulty = 3;
                break;
            }
            System.out.println("Opção inválida. Tente novamente.");
        }

        return new BotSelection(chosenAI, difficulty);
    }

    private static void printGameOver(PieceColor winner) {
        Table banner = new Table();
        banner.setBorderStyle(BorderStyle.UNICODE_DOUBLE);
        banner.setTitle(" FIM DE JOGO ");

        if (winner == PieceColor.WHITE) {
            banner.addRow(Colors.colorize(Colors.FG_BRIGHT_GREEN + Colors.BOLD, "  PARABÉNS! VOCÊ VENCEU A PARTIDA!  "));
        } else if (winner == PieceColor.BLACK) {
            banner.addRow(Colors.colorize(Colors.FG_BRIGHT_RED + Colors.BOLD, "  VITÓRIA DA INTELIGÊNCIA ARTIFICIAL!  "));
        } else {
            banner.addRow(Colors.colorize(Colors.FG_BRIGHT_YELLOW + Colors.BOLD, "  PARTIDA EMPATADA!  "));
        }

        System.out.println("\n" + banner.render());
    }

    private static boolean isHelpCommand(String input) {
        String clean = input.replaceAll("\\s+", "").toLowerCase();
        return clean.equals("?") || clean.equals("<?>") || clean.equals("<help>") ||
               clean.equals("help") || clean.equals("ajuda") || clean.equals("<ajuda>");
    }

    private static boolean isClearCommand(String input) {
        String clean = input.trim().toLowerCase();
        return clean.equals("cls") || clean.equals("clear") || clean.equals("limpar") || clean.equals("l");
    }

    private static String formatHelpGuide(List<Move> legalMoves, NotationParser parser) {
        StringBuilder sb = new StringBuilder();
        sb.append("FORMAS DE COMANDAR UMA PEÇA:\n");
        sb.append("  • Por extenso: 'E3 para E4', 'E3 to E4'\n");
        sb.append("  • Espaço / Hífen: 'C3 D3', 'C3-C4', 'C3->C4'\n");
        sb.append("  • Captura simples: 'E3 x E5', 'E3:E5'\n");
        sb.append("  • Captura múltipla: 'A3:A5:C5', 'A3 C5'\n");
        sb.append("  • Limpar tela: 'cls' ou 'limpar'\n");
        sb.append("  • Encerrar: 'sair' ou 'exit'\n");
        sb.append("  » Lances legais agora (").append(legalMoves.size()).append("): ");
        List<String> list = new ArrayList<>();
        for (Move m : legalMoves) {
            list.add(parser.formatMove(m));
        }
        sb.append(String.join(", ", list));
        return sb.toString();
    }

    private static final class BotSelection {
        final BotType botType;
        final int difficulty;

        BotSelection(BotType botType, int difficulty) {
            this.botType = botType;
            this.difficulty = difficulty;
        }
    }
}
