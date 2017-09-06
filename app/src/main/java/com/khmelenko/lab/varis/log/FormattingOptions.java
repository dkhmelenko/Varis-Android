package com.khmelenko.lab.varis.log;

public class FormattingOptions {
    private Integer mTextColor;
    private Integer mBackground;
    private boolean mBold;
    private boolean mItalic;
    private boolean mUnderline;

    public static FormattingOptions fromAnsiCodes(String[] ansiStates) {
        FormattingOptions options = new FormattingOptions();
        for (String ansiCode : ansiStates) {
            AnsiCodes.applyAnsiCode(options, ansiCode);
        }
        return options;
    }

    public Integer getTextColor() {
        return mTextColor;
    }

    public void setTextColor(Integer textColor) {
        mTextColor = textColor;
    }

    public Integer getBackground() {
        return mBackground;
    }

    public void setBackground(Integer background) {
        mBackground = background;
    }

    public boolean isBold() {
        return mBold;
    }

    public void setBold(boolean bold) {
        mBold = bold;
    }

    public boolean isItalic() {
        return mItalic;
    }

    public void setItalic(boolean italic) {
        mItalic = italic;
    }

    public boolean isUnderline() {
        return mUnderline;
    }

    public void setUnderline(boolean underline) {
        mUnderline = underline;
    }

    @Override
    public String toString() {
        return "FormattingOptions{" +
                "textColor=" + mTextColor +
                ", background=" + mBackground +
                ", bold=" + mBold +
                ", italic=" + mItalic +
                ", underline=" + mUnderline +
                '}';
    }
}
