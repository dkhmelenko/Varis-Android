package com.khmelenko.lab.varis.log;

import java.util.Stack;

/**
 * Parser for ANSI escape sequences.
 *
 * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 *
 * */
public class AnsiParser {

    private Stack<TextLeaf> result = new Stack<>();
    private TextLeaf textLeaf = new TextLeaf();
    private String[] ansiStates = new String[]{};

    /**
     * Parses the given log for ANSI escape sequences and builds a list of text chunks, which
     * share the same color and text formatting.
     */
    public static Stack<TextLeaf> parseText(String text) {
        return new AnsiParser().parse(text);
    }

    /**
     * @see #parseText(String)
     */
    private Stack<TextLeaf> parse(String text) {
        // Remove character when followed by a BACKSPACE character
        while (text.contains("\b")) {
            text = text.replaceAll("^\b+|[^\b]\b", "");
        }

        int controlStartPosition;
        int lastControlPosition = 0;
        int controlEndPosition;
        while (true) {
            controlStartPosition = text.indexOf("\033[", lastControlPosition);
            if (controlStartPosition == -1) {
                break;
            }

            textLeaf.setText(text.substring(lastControlPosition, controlStartPosition));

            if (isResetLineEscape(text, controlStartPosition)) {
                controlEndPosition = text.indexOf('K', controlStartPosition + 2);
                int i;
                while (true) {
                    i = Math.max(textLeaf.getText().lastIndexOf("\r"), textLeaf.getText().lastIndexOf("\n"));
                    if (i != -1) {
                        break;
                    }
                    if(result.isEmpty()) {
                        break;
                    }
                    textLeaf = result.pop();
                }
                if (textLeaf.getText().length() > i && i > 0) {
                    String end = textLeaf.getText().substring(i - 1, i + 1);
                    textLeaf.setText(textLeaf.getText().substring(0, i));
                    if (end.equals("\r\n") || end.equals("\n\r")) {
                        textLeaf.setText(textLeaf.getText().substring(0, i - 1));
                    }
                } else {
                    textLeaf.setText("");
                }
            } else {
                controlEndPosition = text.indexOf('m', controlStartPosition + 2);
                if (controlEndPosition == -1) {
                    break;
                }
                String matchingData = text.substring(controlStartPosition + 2, controlEndPosition);
                ansiStates = matchingData.split(";");
            }

            // Emit new textLeaf
            if (!textLeaf.getText().isEmpty()) {
                result.push(textLeaf);
            }
            textLeaf = new TextLeaf(FormattingOptions.fromAnsiCodes(ansiStates));
            lastControlPosition = controlEndPosition + 1;
        }

        textLeaf.setText(text.substring(lastControlPosition));
        if (!textLeaf.getText().isEmpty() || result.isEmpty()) {
            result.push(textLeaf);
        }
        return result;
    }

    private boolean isResetLineEscape(String str, int controlStartPosition) {
        final String substring = str.substring(controlStartPosition);
        return substring.startsWith("\033[0K") || substring.startsWith("\033[K");
    }
}
