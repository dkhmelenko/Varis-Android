package com.khmelenko.lab.varis.log

internal class TravisCommandLeaf(val command: String, val type: String, val name: String) : LogEntryComponent {

    override fun toHtml(): String {
        return ""
    }
}
