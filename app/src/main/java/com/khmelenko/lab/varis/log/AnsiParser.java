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

            textLeaf.text = text.substring(lastControlPosition, controlStartPosition);

            if (isResetLineEscape(text, controlStartPosition)) {
                controlEndPosition = text.indexOf('K', controlStartPosition + 2);
                int i;
                while (true) {
                    i = Math.max(textLeaf.text.lastIndexOf("\r"), textLeaf.text.lastIndexOf("\n"));
                    if (i != -1) {
                        break;
                    }
                    if(result.isEmpty()) {
                        break;
                    }
                    textLeaf = result.pop();
                }
                if (textLeaf.text.length() > i && i > 0) {
                    String end = textLeaf.text.substring(i - 1, i + 1);
                    textLeaf.text = textLeaf.text.substring(0, i);
                    if (end.equals("\r\n") || end.equals("\n\r")) {
                        textLeaf.text = textLeaf.text.substring(0, i - 1);
                    }
                } else {
                    textLeaf.text = "";
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
            if (!textLeaf.text.isEmpty()) {
                result.add(textLeaf);
                textLeaf = new TextLeaf();
            }
            textLeaf.addAnsiCodes(ansiStates);
            lastControlPosition = controlEndPosition + 1;
        }

        textLeaf.text = text.substring(lastControlPosition);
        if (!textLeaf.text.isEmpty() || result.isEmpty()) {
            result.add(textLeaf);
        }
        return result;
    }

    private boolean isResetLineEscape(String str, int controlStartPosition) {
        final String substring = str.substring(controlStartPosition);
        return substring.startsWith("\033[0K") || substring.startsWith("\033[K");
    }
}
