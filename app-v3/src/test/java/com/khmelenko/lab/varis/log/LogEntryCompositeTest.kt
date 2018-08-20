package com.khmelenko.lab.varis.log

import junit.framework.Assert

import org.junit.Test

class LogEntryCompositeTest {
    @Test
    fun toHtml() {
        val root = LogEntryComposite(null)
        root.append(TextLeaf("test"))
        Assert.assertEquals("<body style=\"background-color:#222222;\"><font color='#FFF'>test</font></body>", root.toHtml())
    }
}
