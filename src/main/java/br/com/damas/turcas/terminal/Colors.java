package br.com.damas.turcas.terminal;

public final class Colors {
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String DIM = "\033[2m";
    public static final String ITALIC = "\033[3m";
    public static final String UNDERLINE = "\033[4m";

    public static final String FG_BLACK = "\033[30m";
    public static final String FG_RED = "\033[31m";
    public static final String FG_GREEN = "\033[32m";
    public static final String FG_YELLOW = "\033[33m";
    public static final String FG_BLUE = "\033[34m";
    public static final String FG_MAGENTA = "\033[35m";
    public static final String FG_CYAN = "\033[36m";
    public static final String FG_WHITE = "\033[37m";

    public static final String FG_BRIGHT_BLACK = "\033[90m";
    public static final String FG_BRIGHT_RED = "\033[91m";
    public static final String FG_BRIGHT_GREEN = "\033[92m";
    public static final String FG_BRIGHT_YELLOW = "\033[93m";
    public static final String FG_BRIGHT_BLUE = "\033[94m";
    public static final String FG_BRIGHT_MAGENTA = "\033[95m";
    public static final String FG_BRIGHT_CYAN = "\033[96m";
    public static final String FG_BRIGHT_WHITE = "\033[97m";

    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_MAGENTA = "\033[45m";
    public static final String BG_CYAN = "\033[46m";
    public static final String BG_WHITE = "\033[47m";

    public static final String BG_DARK_SQUARE = "\033[48;5;236m";
    public static final String BG_LIGHT_SQUARE = "\033[48;5;244m";
    public static final String BG_HIGHLIGHT = "\033[48;5;28m";
    public static final String BG_LAST_MOVE = "\033[48;5;58m";

    private Colors() {}

    public static String colorize(String color, String text) {
        return color + text + RESET;
    }
}
