package com.khmelenko.lab.varis.log;

final class TextLeaf implements LogEntryComponent {

    private FormattingOptions mOptions;
    private String mText = "";

    TextLeaf() {
        this("");
    }

    TextLeaf(String text) {
        this(text, FormattingOptions.fromAnsiCodes());
    }

    TextLeaf(FormattingOptions options) {
        this("", options);
    }

    public TextLeaf(String text, FormattingOptions options) {
        mText = text;
        mOptions = options;
    }

    public FormattingOptions getOptions() {
        return mOptions;
    }

    public void setOptions(FormattingOptions options) {
        mOptions = options;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public String toHtml() {
        String result = mText.replaceAll("\\r\\n?|\\n", "<br />");
        if (mOptions.getTextColor() != null) {
            result = "<font color='" + mOptions.getTextColor() + "'>" + result + "</font>";
        } else {
            result = "<font color='#FFF'>" + result + "</font>";
        }
        if (mOptions.isBold()) {
            result = "<b>" + result + "</b>";
        }
        if (mOptions.isItalic()) {
            result = "<i>" + result + "</i>";
        }
        return result;
    }

    @Override
    public String toString() {
        return "TextLeaf{" +
                "options=" + mOptions +
                ", text='" + mText + '\'' +
                '}';
    }
}
