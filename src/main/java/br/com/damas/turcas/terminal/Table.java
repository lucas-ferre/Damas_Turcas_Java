package br.com.damas.turcas.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class Table {
    private String title;
    private final List<String> headers;
    private final List<List<String>> rows;
    private final List<Alignment> alignments;
    private BorderStyle borderStyle;
    private int padding;

    private static final Pattern ANSI_PATTERN = Pattern.compile("\\x1B\\[[0-9;]*[a-zA-Z]");

    public Table() {
        this.title = null;
        this.headers = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.alignments = new ArrayList<>();
        this.borderStyle = BorderStyle.UNICODE;
        this.padding = 1;
    }

    public static int visibleLen(String s) {
        if (s == null) {
            return 0;
        }
        String clean = ANSI_PATTERN.matcher(s).replaceAll("");
        return clean.codePointCount(0, clean.length());
    }

    public Table setTitle(String title) {
        this.title = title;
        return this;
    }

    public Table setBorderStyle(BorderStyle style) {
        this.borderStyle = style;
        return this;
    }

    public Table setPadding(int padding) {
        this.padding = Math.max(0, padding);
        return this;
    }

    public Table setHeaders(String... headers) {
        this.headers.clear();
        this.headers.addAll(Arrays.asList(headers));
        while (this.alignments.size() < this.headers.size()) {
            this.alignments.add(Alignment.LEFT);
        }
        return this;
    }

    public Table addRow(String... cells) {
        List<String> row = new ArrayList<>(Arrays.asList(cells));
        this.rows.add(row);
        while (this.alignments.size() < row.size()) {
            this.alignments.add(Alignment.LEFT);
        }
        return this;
    }

    public Table setAlignments(Alignment... aligns) {
        this.alignments.clear();
        this.alignments.addAll(Arrays.asList(aligns));
        return this;
    }

    public String render() {
        int numCols = headers.size();
        for (List<String> row : rows) {
            if (row.size() > numCols) {
                numCols = row.size();
            }
        }

        if (numCols == 0) {
            return "";
        }

        int[] colWidths = new int[numCols];
        for (int i = 0; i < headers.size(); i++) {
            int vl = visibleLen(headers.get(i));
            if (vl > colWidths[i]) {
                colWidths[i] = vl;
            }
        }

        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                if (i < numCols) {
                    int vl = visibleLen(row.get(i));
                    if (vl > colWidths[i]) {
                        colWidths[i] = vl;
                    }
                }
            }
        }

        int totalInnerWidth = 0;
        for (int w : colWidths) {
            totalInnerWidth += w + (padding * 2);
        }
        totalInnerWidth += (numCols - 1);

        StringBuilder sb = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            sb.append(borderStyle.topLeft);
            sb.append(borderStyle.horizontal.repeat(totalInnerWidth));
            sb.append(borderStyle.topRight).append("\n");

            int titlePad = totalInnerWidth - visibleLen(title);
            int leftPad = Math.max(0, titlePad / 2);
            int rightPad = Math.max(0, titlePad - leftPad);

            sb.append(borderStyle.vertical);
            sb.append(" ".repeat(leftPad));
            sb.append(title);
            sb.append(" ".repeat(rightPad));
            sb.append(borderStyle.vertical).append("\n");

            sb.append(borderStyle.midLeft);
            for (int i = 0; i < numCols; i++) {
                sb.append(borderStyle.horizontal.repeat(colWidths[i] + padding * 2));
                if (i < numCols - 1) {
                    sb.append(borderStyle.topMid);
                }
            }
            sb.append(borderStyle.midRight).append("\n");
        } else {
            sb.append(borderStyle.topLeft);
            for (int i = 0; i < numCols; i++) {
                sb.append(borderStyle.horizontal.repeat(colWidths[i] + padding * 2));
                if (i < numCols - 1) {
                    sb.append(borderStyle.topMid);
                }
            }
            sb.append(borderStyle.topRight).append("\n");
        }

        if (!headers.isEmpty()) {
            sb.append(borderStyle.vertical);
            for (int i = 0; i < numCols; i++) {
                String headerText = i < headers.size() ? headers.get(i) : "";
                Alignment align = Alignment.CENTER;
                if (i < alignments.size()) {
                    align = alignments.get(i);
                }
                sb.append(" ".repeat(padding));
                sb.append(formatCell(headerText, colWidths[i], align));
                sb.append(" ".repeat(padding));
                sb.append(borderStyle.vertical);
            }
            sb.append("\n");

            sb.append(borderStyle.midLeft);
            for (int i = 0; i < numCols; i++) {
                sb.append(borderStyle.horizontal.repeat(colWidths[i] + padding * 2));
                if (i < numCols - 1) {
                    sb.append(borderStyle.midMid);
                }
            }
            sb.append(borderStyle.midRight).append("\n");
        }

        for (int rIdx = 0; rIdx < rows.size(); rIdx++) {
            List<String> row = rows.get(rIdx);
            sb.append(borderStyle.vertical);
            for (int i = 0; i < numCols; i++) {
                String cellText = i < row.size() ? row.get(i) : "";
                Alignment align = Alignment.LEFT;
                if (i < alignments.size()) {
                    align = alignments.get(i);
                }
                sb.append(" ".repeat(padding));
                sb.append(formatCell(cellText, colWidths[i], align));
                sb.append(" ".repeat(padding));
                sb.append(borderStyle.vertical);
            }
            sb.append("\n");

            if (rIdx < rows.size() - 1) {
                sb.append(borderStyle.midLeft);
                for (int i = 0; i < numCols; i++) {
                    sb.append(borderStyle.horizontal.repeat(colWidths[i] + padding * 2));
                    if (i < numCols - 1) {
                        sb.append(borderStyle.midMid);
                    }
                }
                sb.append(borderStyle.midRight).append("\n");
            }
        }

        sb.append(borderStyle.botLeft);
        for (int i = 0; i < numCols; i++) {
            sb.append(borderStyle.horizontal.repeat(colWidths[i] + padding * 2));
            if (i < numCols - 1) {
                sb.append(borderStyle.botMid);
            }
        }
        sb.append(borderStyle.botRight).append("\n");

        return sb.toString();
    }

    private static String formatCell(String text, int width, Alignment align) {
        int vLen = visibleLen(text);
        if (vLen >= width) {
            return text;
        }
        int diff = width - vLen;
        switch (align) {
            case RIGHT:
                return " ".repeat(diff) + text;
            case CENTER:
                int left = diff / 2;
                int right = diff - left;
                return " ".repeat(left) + text + " ".repeat(right);
            default:
                return text + " ".repeat(diff);
        }
    }
}
