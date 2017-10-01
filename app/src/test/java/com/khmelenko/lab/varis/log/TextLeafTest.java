package com.khmelenko.lab.varis.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextLeafTest {
    @Test
    public void addAnsiCodes() throws Exception {
        TextLeaf actual = new TextLeaf("Magenta Italic", FormattingOptions.fromAnsiCodes(new String[]{"35", "3"}));
        TextLeaf expected = new TextLeaf("Magenta Italic");
        expected.getOptions().setTextColor("#FF73FD");
        expected.getOptions().setItalic(true);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void toHtml() throws Exception {
        TextLeaf text = new TextLeaf("T", FormattingOptions.fromAnsiCodes(new String[]{"34", "1"}));
        assertEquals("<b><font color='#96CBFE'>T</font></b>", text.toHtml());
    }

}
