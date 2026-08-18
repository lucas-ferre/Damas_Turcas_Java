package br.com.damas.turcas.game;

public final class Board {
    public static final int DEFAULT_SIZE = 8;
    public static final int EXPANDED_SIZE = 10;

    private final int size;
    private final Piece[][] grid;
    private PieceColor turn;
    private int whiteCount;
    private int blackCount;
    private int whiteKingCount;
    private int blackKingCount;
    private int moveCount;
    private int halfMoveClock;
    private Move lastMove;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        if (size <= 0) {
            size = DEFAULT_SIZE;
        }
        if (size % 2 != 0) {
            size++;
        }
        this.size = size;
        this.grid = new Piece[size][size];
        this.turn = PieceColor.WHITE;
        this.lastMove = null;
        initPieces();
    }

    private Board(int size, Piece[][] grid, PieceColor turn, int whiteCount, int blackCount,
                  int whiteKingCount, int blackKingCount, int moveCount, int halfMoveClock, Move lastMove) {
        this.size = size;
        this.grid = grid;
        this.turn = turn;
        this.whiteCount = whiteCount;
        this.blackCount = blackCount;
        this.whiteKingCount = whiteKingCount;
        this.blackKingCount = blackKingCount;
        this.moveCount = moveCount;
        this.halfMoveClock = halfMoveClock;
        this.lastMove = lastMove;
    }

    public void initPieces() {
        whiteCount = 0;
        blackCount = 0;
        whiteKingCount = 0;
        blackKingCount = 0;
        moveCount = 0;
        halfMoveClock = 0;
        lastMove = null;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = Piece.EMPTY;
            }
        }

        for (int r = 1; r <= 2; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = new Piece(PieceColor.BLACK, PieceType.MAN);
                blackCount++;
            }
        }

        for (int r = size - 3; r <= size - 2; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = new Piece(PieceColor.WHITE, PieceType.MAN);
                whiteCount++;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void setTurn(PieceColor turn) {
        this.turn = turn;
    }

    public int getWhiteCount() {
        return whiteCount;
    }

    public int getBlackCount() {
        return blackCount;
    }

    public int getWhiteKingCount() {
        return whiteKingCount;
    }

    public int getBlackKingCount() {
        return blackKingCount;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Piece get(Position pos) {
        if (!pos.isValid(size)) {
            return Piece.EMPTY;
        }
        return grid[pos.getRow()][pos.getCol()];
    }

    public Piece get(int r, int c) {
        if (r < 0 || r >= size || c < 0 || c >= size) {
            return Piece.EMPTY;
        }
        return grid[r][c];
    }

    public void set(Position pos, Piece piece) {
        if (!pos.isValid(size)) {
            return;
        }
        grid[pos.getRow()][pos.getCol()] = piece != null ? piece : Piece.EMPTY;
    }

    public void set(int r, int c, Piece piece) {
        if (r < 0 || r >= size || c < 0 || c >= size) {
            return;
        }
        grid[r][c] = piece != null ? piece : Piece.EMPTY;
    }

    public Board clone() {
        Piece[][] newGrid = new Piece[size][size];
        for (int r = 0; r < size; r++) {
            System.arraycopy(grid[r], 0, newGrid[r], 0, size);
        }
        return new Board(size, newGrid, turn, whiteCount, blackCount, whiteKingCount, blackKingCount, moveCount, halfMoveClock, lastMove);
    }

    public void applyMove(Move m) {
        Piece piece = get(m.getFrom());
        set(m.getFrom(), Piece.EMPTY);

        if (m.isCapture()) {
            for (Position capPos : m.getCaptures()) {
                Piece captured = get(capPos);
                if (captured.isWhite()) {
                    whiteCount--;
                    if (captured.isKing()) {
                        whiteKingCount--;
                    }
                } else if (captured.isBlack()) {
                    blackCount--;
                    if (captured.isKing()) {
                        blackKingCount--;
                    }
                }
                set(capPos, Piece.EMPTY);
            }
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        boolean willPromote = false;
        if (piece.isMan()) {
            if (piece.isWhite() && m.getTo().getRow() == 0) {
                willPromote = true;
            } else if (piece.isBlack() && m.getTo().getRow() == size - 1) {
                willPromote = true;
            }
        }

        if (willPromote) {
            piece = piece.promote();
            if (piece.isWhite()) {
                whiteKingCount++;
            } else {
                blackKingCount++;
            }
            m = m.withPromotion(true);
        }

        set(m.getTo(), piece);
        this.lastMove = m;
        this.turn = this.turn.opponent();
        this.moveCount++;
    }

    public void recalculateCounts() {
        whiteCount = 0;
        blackCount = 0;
        whiteKingCount = 0;
        blackKingCount = 0;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Piece p = grid[r][c];
                if (p.isWhite()) {
                    whiteCount++;
                    if (p.isKing()) {
                        whiteKingCount++;
                    }
                } else if (p.isBlack()) {
                    blackCount++;
                    if (p.isKing()) {
                        blackKingCount++;
                    }
                }
            }
        }
    }
}
