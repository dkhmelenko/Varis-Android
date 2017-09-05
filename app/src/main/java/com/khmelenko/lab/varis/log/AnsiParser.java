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
    private TextLeaf mTextLeaf = new TextLeaf();
    private String[] mAnsiStates = new String[]{};

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

            mTextLeaf.setText(text.substring(lastControlPosition, controlStartPosition));

            if (isResetLineEscape(text, controlStartPosition)) {
                controlEndPosition = text.indexOf('K', controlStartPosition + 2);
                int i;
                while (true) {
                    i = Math.max(mTextLeaf.getText().lastIndexOf("\r"), mTextLeaf.getText().lastIndexOf("\n"));
                    if (i != -1) {
                        break;
                    }
                    if(mResult.isEmpty()) {
                        break;
                    }
                    mTextLeaf = mResult.pop();
                }
                if (mTextLeaf.getText().length() > i && i > 0) {
                    String end = mTextLeaf.getText().substring(i - 1, i + 1);
                    mTextLeaf.setText(mTextLeaf.getText().substring(0, i));
                    if (end.equals("\r\n") || end.equals("\n\r")) {
                        mTextLeaf.setText(mTextLeaf.getText().substring(0, i - 1));
                    }
                } else {
                    mTextLeaf.setText("");
                }
            } else {
                controlEndPosition = text.indexOf('m', controlStartPosition + 2);
                if (controlEndPosition == -1) {
                    break;
                }
                String matchingData = text.substring(controlStartPosition + 2, controlEndPosition);
                mAnsiStates = matchingData.split(";");
            }

            // Emit new textLeaf
            if (!mTextLeaf.getText().isEmpty()) {
                mResult.push(mTextLeaf);
            }
            mTextLeaf = new TextLeaf(FormattingOptions.fromAnsiCodes(mAnsiStates));
            lastControlPosition = controlEndPosition + 1;
        }

        mTextLeaf.setText(text.substring(lastControlPosition));
        if (!mTextLeaf.getText().isEmpty() || mResult.isEmpty()) {
            mResult.push(mTextLeaf);
        }
        return mResult;
    }

    private boolean isResetLineEscape(String str, int controlStartPosition) {
        final String substring = str.substring(controlStartPosition);
        return substring.startsWith("\033[0K") || substring.startsWith("\033[K");
    }
}
