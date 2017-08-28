package com.khmelenko.lab.varis.log;

import com.khmelenko.lab.varis.log.LogEntryComposite;
import com.khmelenko.lab.varis.log.TextLeaf;

import junit.framework.Assert;

import org.junit.Test;

public class LogEntryCompositeTest {
    @Test
    public void toHtml() throws Exception {
        LogEntryComposite root = new LogEntryComposite(null);
        root.append(new TextLeaf("test"));
        Assert.assertEquals("<body style=\"background-color:black;\"><font color='#FFF'>test</font></body>",root.toHtml());
    }
}
