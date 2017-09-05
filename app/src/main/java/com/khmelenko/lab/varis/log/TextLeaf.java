package com.khmelenko.lab.varis.log;

import android.support.annotation.VisibleForTesting;
public class TextLeaf implements LogEntryComponent {

    @VisibleForTesting
    Integer textColor;
    @VisibleForTesting
    Integer background;
    @VisibleForTesting
    boolean bold;
    @VisibleForTesting
    boolean italic;
    @VisibleForTesting
    boolean underline;
    public String text = "";

    TextLeaf() {
    }

    public TextLeaf(String text) {
        this.text = text;
    }

    TextLeaf(TextLeaf textLeaf) {
        textColor = textLeaf.textColor;
        background = textLeaf.background;
        bold = textLeaf.bold;
        italic = textLeaf.italic;
        underline = textLeaf.underline;
    }

    void addAnsiCodes(String[] ansiStates) {
        for (String ansiCode : ansiStates) {
            translateAnsiCode(ansiCode);
        }
    }

    private void translateAnsiCode(String code) {
        switch (code) {
            case "1":
                bold = true;
                break;
            case "3":
                italic = true;
                break;
            case "4":
                underline = true;
                break;
            case "22":
                bold = false;
                break;
            case "23":
                italic = false;
                break;
            case "24":
                underline = false;
                break;
            case "30":
                textColor = AnsiColor.BLACK;
                break;
            case "31":
                textColor = AnsiColor.RED;
                break;
            case "32":
                textColor = AnsiColor.GREEN;
                break;
            case "33":
                textColor = AnsiColor.YELLOW;
                break;
            case "34":
                textColor = AnsiColor.BLUE;
                break;
            case "35":
                textColor = AnsiColor.MAGENTA;
                break;
            case "36":
                textColor = AnsiColor.CYAN;
                break;
            case "37":
                textColor = AnsiColor.WHITE;
                break;
            case "39":
                textColor = null;
                break;
            case "49":
                background = null;
                break;
            case "40":
                background = AnsiColor.BG_BLACK;
                break;
            case "41":
                background = AnsiColor.BG_RED;
                break;
            case "42":
                background = AnsiColor.BG_GREEN;
                break;
            case "43":
                background = AnsiColor.BG_YELLOW;
                break;
            case "44":
                background = AnsiColor.BG_BLUE;
                break;
            case "45":
                background = AnsiColor.BG_MAGENTA;
                break;
            case "46":
                background = AnsiColor.BG_CYAN;
                break;
            case "47":
                background = AnsiColor.BG_WHITE;
                break;
            case "90":
                textColor = AnsiColor.GREY;
                break;
        }
    }

    @Override
    public String toHtml() {
        String result = text.replaceAll("\\r\\n?|\\n", "<br />");
        if (textColor != null) {
            result = "<font color='#" + Integer
                    .toHexString(textColor) + "'>" + result + "</font>";
        } else {
            result = "<font color='#FFF'>" + result + "</font>";
        }
        if (bold) {
            result = "<b>" + result + "</b>";
        }
        if (italic) {
            result = "<i>" + result + "</i>";
        }
        return result;
    }

    @Override
    public String toString() {
        return "TextLeaf{" +
                "textColor=" + textColor +
                ", background=" + background +
                ", bold=" + bold +
                ", italic=" + italic +
                ", underline=" + underline +
                ", text='" + text + '\'' +
                '}';
    }
}
