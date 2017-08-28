package com.khmelenko.lab.varis.log;

import com.khmelenko.lab.varis.log.TextLeaf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextLeafTest {
    @Test
    public void addAnsiCodes() throws Exception {
        TextLeaf text = new TextLeaf("T");
        text.addAnsiCodes(new String[]{"34", "1"});
        assertEquals("TextLeaf{textColor=9882622, background=null, bold=true, italic=false, underline=false, text='T'}", text.toString());
    }

    @Test
    public void toHtml() throws Exception {
        TextLeaf text = new TextLeaf("T");
        text.addAnsiCodes(new String[]{"34", "1"});
        assertEquals("<b><font color='#96cbfe'>T</font></b>", text.toHtml());
    }

}
