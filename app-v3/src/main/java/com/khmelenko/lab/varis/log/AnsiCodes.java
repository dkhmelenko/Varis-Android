package com.khmelenko.lab.varis.log;

final class AnsiCodes {
    private static String BLACK = "#4E4E4E";
    private static String RED = "#FF6C60";
    private static String GREEN = "#98D86A";
    private static String YELLOW = "#FFFFB6";
    private static String BLUE = "#96CBFE";
    private static String MAGENTA = "#FF73FD";
    private static String CYAN = "#50FFFF";
    private static String WHITE = "#E0E0E0";
    private static String GREY = "#969696";

    private static String BG_BLACK = "#4E4E4E";
    private static String BG_RED = "#FF6C60";
    private static String BG_GREEN = "#98D86A";
    private static String BG_YELLOW = "#FFFFB6";
    private static String BG_BLUE = "#96CBFE";
    private static String BG_MAGENTA = "#FF73FD";
    private static String BG_CYAN = "#50FFFF";
    private static String BG_WHITE = "#EEEEEE";

    // denied constructor
    private AnsiCodes() {

    }

    static void applyAnsiCode(FormattingOptions options, String code) {
        switch (code) {
            case "1":
                options.setBold(true);
                break;
            case "3":
                options.setItalic(true);
                break;
            case "4":
                options.setUnderline(true);
                break;
            case "22":
                options.setBold(false);
                break;
            case "23":
                options.setItalic(false);
                break;
            case "24":
                options.setUnderline(false);
                break;
            case "30":
                options.setTextColor(AnsiCodes.BLACK);
                break;
            case "31":
                options.setTextColor(AnsiCodes.RED);
                break;
            case "32":
                options.setTextColor(AnsiCodes.GREEN);
                break;
            case "33":
                options.setTextColor(AnsiCodes.YELLOW);
                break;
            case "34":
                options.setTextColor(AnsiCodes.BLUE);
                break;
            case "35":
                options.setTextColor(AnsiCodes.MAGENTA);
                break;
            case "36":
                options.setTextColor(AnsiCodes.CYAN);
                break;
            case "37":
                options.setTextColor(AnsiCodes.WHITE);
                break;
            case "39":
                options.setTextColor(null);
                break;
            case "49":
                options.setBackground(null);
                break;
            case "40":
                options.setBackground(AnsiCodes.BG_BLACK);
                break;
            case "41":
                options.setBackground(AnsiCodes.BG_RED);
                break;
            case "42":
                options.setBackground(AnsiCodes.BG_GREEN);
                break;
            case "43":
                options.setBackground(AnsiCodes.BG_YELLOW);
                break;
            case "44":
                options.setBackground(AnsiCodes.BG_BLUE);
                break;
            case "45":
                options.setBackground(AnsiCodes.BG_MAGENTA);
                break;
            case "46":
                options.setBackground(AnsiCodes.BG_CYAN);
                break;
            case "47":
                options.setBackground(AnsiCodes.BG_WHITE);
                break;
            case "90":
                options.setTextColor(AnsiCodes.GREY);
                break;
        }
    }
}
