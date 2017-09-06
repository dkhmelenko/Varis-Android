package com.khmelenko.lab.varis.log;

final class AnsiCodes {
    private static int BLACK = 0x4E4E4E;
    private static int RED = 0xFF6C60;
    private static int GREEN = 0x00AA00;
    private static int YELLOW = 0xFFFFB6;
    private static int BLUE = 0x96CBFE;
    private static int MAGENTA = 0xFF73FD;
    private static int CYAN = 0x50FFFF;
    private static int WHITE = 0xE0E0E0;
    private static int GREY = 0x969696;

    private static int BG_BLACK = 0x4E4E4E;
    private static int BG_RED = 0xFF6C60;
    private static int BG_GREEN = 0x00AA00;
    private static int BG_YELLOW = 0xFFFFB6;
    private static int BG_BLUE = 0x96CBFE;
    private static int BG_MAGENTA = 0xFF73FD;
    private static int BG_CYAN = 0x00AAAA;
    private static int BG_WHITE = 0xEEEEEE;

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
