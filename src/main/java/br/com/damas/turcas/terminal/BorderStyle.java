package br.com.damas.turcas.terminal;

public final class BorderStyle {
    public final String topLeft;
    public final String topMid;
    public final String topRight;
    public final String midLeft;
    public final String midMid;
    public final String midRight;
    public final String botLeft;
    public final String botMid;
    public final String botRight;
    public final String horizontal;
    public final String vertical;

    public BorderStyle(String topLeft, String topMid, String topRight,
                       String midLeft, String midMid, String midRight,
                       String botLeft, String botMid, String botRight,
                       String horizontal, String vertical) {
        this.topLeft = topLeft;
        this.topMid = topMid;
        this.topRight = topRight;
        this.midLeft = midLeft;
        this.midMid = midMid;
        this.midRight = midRight;
        this.botLeft = botLeft;
        this.botMid = botMid;
        this.botRight = botRight;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public static final BorderStyle UNICODE = new BorderStyle(
        "┌", "┬", "┐",
        "├", "┼", "┤",
        "└", "┴", "┘",
        "─", "│"
    );

    public static final BorderStyle UNICODE_DOUBLE = new BorderStyle(
        "╔", "╦", "╗",
        "╠", "╬", "╣",
        "╚", "╩", "╝",
        "═", "║"
    );

    public static final BorderStyle ASCII = new BorderStyle(
        "+", "+", "+",
        "+", "+", "+",
        "+", "+", "+",
        "-", "|"
    );
}
