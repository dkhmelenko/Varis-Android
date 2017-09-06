package com.khmelenko.lab.varis.log;

import java.util.Stack;

/**
 * Parser for ANSI escape sequences.
 *
 * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 *
 * */
public class AnsiParser {

    private Stack<TextLeaf> mResult = new Stack<>();
    private FormattingOptions mOptions = new FormattingOptions();

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

            String textBeforeEscape = text.substring(lastControlPosition, controlStartPosition);
            emitText(textBeforeEscape);

            if (isResetLineEscape(text, controlStartPosition)) {
                controlEndPosition = text.indexOf('K', controlStartPosition + 2);
                removeCurrentLine();
                mOptions = new FormattingOptions();
            } else {
                controlEndPosition = text.indexOf('m', controlStartPosition + 2);
                if (controlEndPosition == -1) {
                    break;
                }
                String matchingData = text.substring(controlStartPosition + 2, controlEndPosition);
                String[] ansiStates = matchingData.split(";");
                mOptions = FormattingOptions.fromAnsiCodes(ansiStates);
            }

            lastControlPosition = controlEndPosition + 1;
        }

        emitText(text.substring(lastControlPosition));
        if (mResult.isEmpty()) {
            mResult.push(new TextLeaf());
        }
        return mResult;
    }

    private void emitText(String text) {
        if (!text.isEmpty()) {
            mResult.push(new TextLeaf(text, mOptions));
        }
    }

    private void removeCurrentLine() {
        if(mResult.isEmpty()) {
            return;
        }
        TextLeaf textLeaf = mResult.peek();
        int i;
        while (true) {
            i = Math.max(textLeaf.getText().lastIndexOf("\r"), textLeaf.getText().lastIndexOf("\n"));
            if (i != -1) {
                break;
            }
            mResult.pop();
            if(mResult.isEmpty()) {
                break;
            }
            textLeaf = mResult.peek();
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
    }

    private boolean isResetLineEscape(String str, int controlStartPosition) {
        final String substring = str.substring(controlStartPosition);
        return substring.startsWith("\033[0K") || substring.startsWith("\033[K");
    }
}
