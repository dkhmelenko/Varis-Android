package com.khmelenko.lab.varis.log;

    public String command;
public class TravisCommandLeaf implements LogEntryComponent {

    public TravisCommandLeaf(String command) {
        this.command = command;
    }

    @Override
    public String toHtml() {
        return "";
    }
}
