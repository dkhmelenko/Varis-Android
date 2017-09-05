package com.khmelenko.lab.varis.log;

public class TextLeaf implements LogEntryComponent {

    private FormattingOptions mOptions;
    private String mText = "";

    TextLeaf() {
        this("");
    }

    TextLeaf(String text) {
        this(text, new FormattingOptions());
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
        this.mOptions = options;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    @Override
    public String toHtml() {
        String result = mText.replaceAll("\\r\\n?|\\n", "<br />");
        if (mOptions.getTextColor() != null) {
            result = "<font color='#" + Integer
                    .toHexString(mOptions.getTextColor()) + "'>" + result + "</font>";
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
