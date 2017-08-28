package com.khmelenko.lab.varis.log;

public class TravisCommandLeaf extends LogEntryComponent {
    public String command;

    public TravisCommandLeaf(String command) {
        this.command = command;
    }
}
