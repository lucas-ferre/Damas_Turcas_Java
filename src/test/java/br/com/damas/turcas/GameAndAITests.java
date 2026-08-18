package br.com.damas.turcas;

import br.com.damas.turcas.ai.Bot;
import br.com.damas.turcas.ai.BotFactory;
import br.com.damas.turcas.ai.BotResult;
import br.com.damas.turcas.ai.BotType;
import br.com.damas.turcas.ai.astar.AStarSolver;
import br.com.damas.turcas.ai.astar.SearchStats;
import br.com.damas.turcas.ai.hillclimbing.ClimbStats;
import br.com.damas.turcas.ai.hillclimbing.HillClimber;
import br.com.damas.turcas.ai.hybrid.HybridBot;
import br.com.damas.turcas.ai.mdp.MDPSolver;
import br.com.damas.turcas.ai.mdp.MDPStats;
import br.com.damas.turcas.game.Board;
import br.com.damas.turcas.game.Move;
import br.com.damas.turcas.game.NotationParser;
import br.com.damas.turcas.game.Piece;
import br.com.damas.turcas.game.PieceColor;
import br.com.damas.turcas.game.PieceType;
import br.com.damas.turcas.game.Position;
import br.com.damas.turcas.game.RulesEngine;
import br.com.damas.turcas.terminal.Colors;
import br.com.damas.turcas.terminal.Table;

import java.util.List;

public final class GameAndAITests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Iniciando suíte de testes de Damas Turcas...\n");

        runTest("testBoardInitialization8x8", GameAndAITests::testBoardInitialization8x8);
        runTest("testBoardInitialization10x10", GameAndAITests::testBoardInitialization10x10);
        runTest("testOrthogonalMovesOnly", GameAndAITests::testOrthogonalMovesOnly);
        runTest("testPieceSimpleCaptureAndPromotion", GameAndAITests::testPieceSimpleCaptureAndPromotion);
        runTest("testMultiCaptureAndMajorityRule", GameAndAITests::testMultiCaptureAndMajorityRule);
        runTest("testTurkishFlyingKingMovesAndCaptures", GameAndAITests::testTurkishFlyingKingMovesAndCaptures);
        runTest("testNotationParser", GameAndAITests::testNotationParser);
        runTest("testTableRenderAndVisibleLen", GameAndAITests::testTableRenderAndVisibleLen);
        runTest("testMDPSolverDecision", GameAndAITests::testMDPSolverDecision);
        runTest("testAStarSolverDecision", GameAndAITests::testAStarSolverDecision);
        runTest("testHillClimberDecision", GameAndAITests::testHillClimberDecision);
        runTest("testHybridBotDecision", GameAndAITests::testHybridBotDecision);
        runTest("testAICapturePriority", GameAndAITests::testAICapturePriority);
        runTest("testDrawAfter46Rounds", GameAndAITests::testDrawAfter46Rounds);

        System.out.printf("\nResultado dos Testes: %d Passaram | %d Falharam\n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, Runnable test) {
        try {
            test.run();
            System.out.printf("  [PASS] %s\n", testName);
            passed++;
        } catch (Throwable t) {
            System.out.printf("  [FAIL] %s: %s\n", testName, t.getMessage());
            t.printStackTrace(System.out);
            failed++;
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " - Esperado: " + expected + ", Obtido: " + actual);
    }

    public static void testBoardInitialization8x8() {
        Board b = new Board(8);
        assertEquals(8, b.getSize(), "Tamanho deve ser 8");
        assertEquals(16, b.getWhiteCount(), "Brancas devem ser 16");
        assertEquals(16, b.getBlackCount(), "Pretas devem ser 16");
        assertEquals(PieceColor.WHITE, b.getTurn(), "Turno inicial deve ser Brancas");

        for (int c = 0; c < 8; c++) {
            assertTrue(b.get(1, c).isBlack() && b.get(1, c).isMan(), "Linha 1 deve ter peças pretas");
            assertTrue(b.get(2, c).isBlack() && b.get(2, c).isMan(), "Linha 2 deve ter peças pretas");
            assertTrue(b.get(5, c).isWhite() && b.get(5, c).isMan(), "Linha 5 deve ter peças brancas");
            assertTrue(b.get(6, c).isWhite() && b.get(6, c).isMan(), "Linha 6 deve ter peças brancas");
            assertTrue(b.get(0, c).isEmpty(), "Linha 0 deve ser vazia");
            assertTrue(b.get(3, c).isEmpty(), "Linha 3 deve ser vazia");
            assertTrue(b.get(4, c).isEmpty(), "Linha 4 deve ser vazia");
            assertTrue(b.get(7, c).isEmpty(), "Linha 7 deve ser vazia");
        }
    }

    public static void testBoardInitialization10x10() {
        Board b = new Board(10);
        assertEquals(10, b.getSize(), "Tamanho deve ser 10");
        assertEquals(20, b.getWhiteCount(), "Brancas devem ser 20");
        assertEquals(20, b.getBlackCount(), "Pretas devem ser 20");

        for (int c = 0; c < 10; c++) {
            assertTrue(b.get(1, c).isBlack() && b.get(1, c).isMan(), "Linha 1 preta");
            assertTrue(b.get(2, c).isBlack() && b.get(2, c).isMan(), "Linha 2 preta");
            assertTrue(b.get(7, c).isWhite() && b.get(7, c).isMan(), "Linha 7 branca");
            assertTrue(b.get(8, c).isWhite() && b.get(8, c).isMan(), "Linha 8 branca");
            assertTrue(b.get(0, c).isEmpty(), "Linha 0 vazia");
            assertTrue(b.get(9, c).isEmpty(), "Linha 9 vazia");
        }
    }

    public static void testOrthogonalMovesOnly() {
        Board b = new Board(8);
        RulesEngine rules = new RulesEngine();
        List<Move> legalMoves = rules.getLegalMoves(b, PieceColor.WHITE);

        assertTrue(!legalMoves.isEmpty(), "Deve haver movimentos legais iniciais");
        for (Move m : legalMoves) {
            int dRow = m.getTo().getRow() - m.getFrom().getRow();
            int dCol = Math.abs(m.getTo().getCol() - m.getFrom().getCol());
            assertTrue((dRow == -1 && dCol == 0) || (dRow == 0 && dCol == 1),
                    "Lances de peão branco devem ser apenas para frente (row - 1) ou lados (col +- 1)");
            assertTrue(dRow != 1, "Peão não pode mover para trás");
            assertTrue(Math.abs(dRow) + dCol == 1, "Não pode mover na diagonal");
        }
    }

    public static void testPieceSimpleCaptureAndPromotion() {
        Board b = new Board(8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                b.set(r, c, Piece.EMPTY);
            }
        }
        b.set(1, 2, new Piece(PieceColor.WHITE, PieceType.MAN));
        b.set(1, 3, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.recalculateCounts();
        b.setTurn(PieceColor.WHITE);

        RulesEngine rules = new RulesEngine();
        List<Move> captures = rules.getLegalMoves(b, PieceColor.WHITE);
        assertEquals(1, captures.size(), "Deve haver exatamente 1 captura lateral");
        Move cap = captures.get(0);
        assertTrue(cap.isCapture(), "Deve ser captura");
        assertEquals(new Position(1, 4), cap.getTo(), "Destino deve ser (1,4)");

        b.applyMove(cap);
        assertEquals(Piece.EMPTY, b.get(1, 3), "Peça capturada deve ser removida");
        assertEquals(0, b.getBlackCount(), "Contagem de pretas deve ser 0");

        b.set(1, 4, new Piece(PieceColor.WHITE, PieceType.MAN));
        b.set(0, 4, Piece.EMPTY);
        b.setTurn(PieceColor.WHITE);
        List<Move> promoMoves = rules.getLegalMoves(b, PieceColor.WHITE);
        Move promoMove = promoMoves.stream()
                .filter(m -> m.getFrom().equals(new Position(1, 4)) && m.getTo().equals(new Position(0, 4)))
                .findFirst().orElseThrow();
        b.applyMove(promoMove);
        assertTrue(b.get(0, 4).isKing(), "Peça que atinge a linha 0 deve ser promovida a Dama");
    }

    public static void testMultiCaptureAndMajorityRule() {
        Board b = new Board(8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                b.set(r, c, Piece.EMPTY);
            }
        }
        b.set(4, 2, new Piece(PieceColor.WHITE, PieceType.MAN));
        b.set(3, 2, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.set(2, 3, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.set(4, 5, new Piece(PieceColor.WHITE, PieceType.MAN));
        b.set(3, 5, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.recalculateCounts();
        b.setTurn(PieceColor.WHITE);

        RulesEngine rules = new RulesEngine();
        List<Move> moves = rules.getLegalMoves(b, PieceColor.WHITE);

        for (Move m : moves) {
            assertEquals(2, m.getCaptureCount(), "Lei da Maioria deve forçar a captura máxima de 2 peças");
            assertEquals(new Position(4, 2), m.getFrom(), "Deve ser a peça em (4,2)");
        }
    }

    public static void testTurkishFlyingKingMovesAndCaptures() {
        Board b = new Board(8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                b.set(r, c, Piece.EMPTY);
            }
        }
        b.set(7, 2, new Piece(PieceColor.WHITE, PieceType.KING));
        b.set(3, 2, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.recalculateCounts();
        b.setTurn(PieceColor.WHITE);

        RulesEngine rules = new RulesEngine();
        List<Move> kingCaptures = rules.getLegalMoves(b, PieceColor.WHITE);

        assertTrue(!kingCaptures.isEmpty(), "Dama voadora deve ter opções de salto à distância");
        for (Move m : kingCaptures) {
            assertTrue(m.isCapture(), "Deve ser captura");
            assertEquals(new Position(3, 2), m.getCaptures().get(0), "Captura em (3,2)");
            assertTrue(m.getTo().getRow() < 3, "Pouso deve ser além da peça capturada (linhas 2, 1 ou 0)");
        }
    }

    public static void testNotationParser() {
        NotationParser parser = new NotationParser(8);
        List<Move> sampleMoves = List.of(
                new Move(new Position(5, 2), new Position(4, 2)),
                new Move(new Position(5, 4), new Position(4, 4))
        );

        Move m1 = parser.parseInput("C3 para C4", sampleMoves);
        assertEquals(new Position(5, 2), m1.getFrom(), "Origem C3");
        assertEquals(new Position(4, 2), m1.getTo(), "Destino C4");

        Move m2 = parser.parseInput("e3 e4", sampleMoves);
        assertEquals(new Position(5, 4), m2.getFrom(), "Origem E3");
        assertEquals(new Position(4, 4), m2.getTo(), "Destino E4");
    }

    public static void testTableRenderAndVisibleLen() {
        String colored = Colors.colorize(Colors.FG_BRIGHT_RED + Colors.BOLD, "TESTE");
        assertEquals(5, Table.visibleLen(colored), "Tamanho visível deve ignorar tags ANSI");

        Table table = new Table();
        table.setHeaders("COL1", "COL2");
        table.addRow("Val1", "Val2");
        String rendered = table.render();
        assertTrue(rendered.contains("COL1"), "Render deve conter cabeçalho");
        assertTrue(rendered.contains("Val1"), "Render deve conter dados");
    }

    public static void testMDPSolverDecision() {
        Board b = new Board(8);
        MDPSolver solver = new MDPSolver(2, 0.90);
        MDPStats stats = solver.findBestMove(b, PieceColor.WHITE);
        assertTrue(stats.getBestMove() != null, "MDP deve escolher um movimento");
        assertTrue(stats.getStatesEvaluated() > 0, "Deve avaliar estados");
    }

    public static void testAStarSolverDecision() {
        Board b = new Board(8);
        AStarSolver solver = new AStarSolver(300, 3);
        SearchStats stats = solver.findBestMove(b, PieceColor.WHITE);
        assertTrue(stats.getBestMove() != null, "A* deve encontrar um movimento");
        assertTrue(stats.getNodesExpanded() > 0, "Deve expandir nós");
    }

    public static void testHillClimberDecision() {
        Board b = new Board(8);
        HillClimber hc = new HillClimber(10, 8);
        ClimbStats stats = hc.findBestMove(b, PieceColor.WHITE);
        assertTrue(stats.getBestMove() != null, "Hill Climbing deve encontrar um movimento");
        assertTrue(stats.getTotalIterations() > 0, "Deve realizar iterações");
    }

    public static void testHybridBotDecision() {
        Board b = new Board(8);
        Bot bot = BotFactory.createBot(BotType.HYBRID, 2);
        BotResult res = bot.selectMove(b);
        assertTrue(res.getMove() != null, "Bot Híbrido deve escolher lance");
        assertTrue(res.getEvaluationInfo() != null && !res.getEvaluationInfo().isEmpty(), "Deve conter info de avaliação");
    }

    public static void testAICapturePriority() {
        Board b = new Board(8);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                b.set(r, c, Piece.EMPTY);
            }
        }
        b.set(4, 2, new Piece(PieceColor.BLACK, PieceType.MAN));
        b.set(5, 2, new Piece(PieceColor.WHITE, PieceType.MAN));
        b.recalculateCounts();
        b.setTurn(PieceColor.BLACK);

        Bot bot = BotFactory.createBot(BotType.MDP, 2);
        BotResult res = bot.selectMove(b);
        assertTrue(res.getMove() != null && res.getMove().isCapture(), "A IA deve priorizar capturas obrigatórias");
    }

    public static void testDrawAfter46Rounds() {
        Board b = new Board(8);
        RulesEngine rules = new RulesEngine();

        assertTrue(!rules.isGameOver(b), "Jogo não deve estar encerrado no início");

        for (int i = 0; i < RulesEngine.MAX_HALF_MOVES; i++) {
            Move m = new Move(new Position(0, 0), new Position(0, 1));
            b.applyMove(m);
        }

        assertEquals(RulesEngine.MAX_HALF_MOVES, b.getHalfMoveClock(), "Relógio deve estar em 92 meios-lances (46 rodadas)");
        assertTrue(rules.isGameOver(b), "Jogo deve empatar após 46 rodadas (92 meios-lances)");
        assertEquals(PieceColor.NONE, rules.getWinner(b), "Vencedor deve ser NONE em caso de empate");
    }
}
