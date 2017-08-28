package com.khmelenko.lab.varis.log;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AnsiParserTest {

    @Test
    public void parseTextEmpty() throws Exception {
        assertEquals(AnsiParser.parseText(""), new TextLeaf());
    }

    @Test
    public void parseTextSimpleText() throws Exception {
        assertEquals(AnsiParser.parseText("Hello World"), new TextLeaf("Hello World"));
    }

    @Test
    public void parseTextSimpleTextMultiline() throws Exception {
        assertEquals(AnsiParser.parseText("Hello World\nHello Mars"), new TextLeaf("Hello World\nHello Mars"));
    }

    @Test
    public void parseTextHeader() throws Exception {
        TextLeaf textLeaf = new TextLeaf("Worker information");
        textLeaf.bold = true;
        textLeaf.textColor = 0xFFFFB6;
        assertEquals(AnsiParser.parseText("\u001B[33;1mWorker information\u001B[0m"), textLeaf);
    }

    @Test
    public void parseTextComplexHeader() throws Exception {
        TextLeaf headerLeaf = new TextLeaf("Build system information");
        headerLeaf.textColor = 0x96CBFE;
        assertEquals(AnsiParser.parseText("\u001B[0Ktravis_fold:start:system_info\n" +
                        "\u001B[0K\u001B[34mBuild system information\u001B[0m\n" +
                        "Build language: android\n"), new TextLeaf("travis_fold:start:system_info"),
                headerLeaf, new TextLeaf("\nBuild language: android\n"));
    }

    @Test
    public void parseTextResetEmptyLine() throws Exception {
        assertEquals(AnsiParser.parseText("\u001B[0K"), new TextLeaf());
    }

    @Test
    public void parseTextResetSingleLine() throws Exception {
        assertEquals(AnsiParser.parseText("Hello World\u001B[0K"), new TextLeaf());
    }

    @Test
    public void parseTextResetMultiLine() throws Exception {
        assertEquals(AnsiParser.parseText("Hello World\nHello Mars\u001B[0KTest"), new TextLeaf("Hello World"), new TextLeaf("Test"));
    }

    @Test
    public void parseTextBackspace() throws Exception {
        assertEquals(AnsiParser.parseText("Hello World\b\b\b\b\b\n\bMars"), new TextLeaf("Hello Mars"));
    }

    private void assertEquals(List<TextLeaf> list, TextLeaf... leafs) {
        Assert.assertEquals(Arrays.asList(leafs).toString(), list.toString());
    }
}
