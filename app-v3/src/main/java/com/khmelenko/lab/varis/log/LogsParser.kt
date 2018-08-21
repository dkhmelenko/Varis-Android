package com.khmelenko.lab.varis.log

import java.util.ArrayList
import java.util.Stack
import java.util.regex.Pattern

import io.reactivex.Observable

private const val TRAVIS_TIME = "travis_time"
private const val TRAVIS_FOLD = "travis_fold"
private const val START = "start"
private const val END = "end"
private val TRAVIS_COMMAND = Pattern.compile('('.toString() + TRAVIS_FOLD + '|'.toString() + TRAVIS_TIME + "):(" + START + '|'.toString() + END + "):(\\S+)")

class LogsParser {

    fun parseLog(logInput: String): LogEntryComponent {
        val logEntryComponents = AnsiParser.parseText(logInput)
        val res = preProcessTextLeafs(logEntryComponents)
        return parseFoldTree(res)
    }

    /**
     * Takes a list of TextLeafs and splits them when it contains a travis command into a
     * TextLeaf + TravisCommandLeaf + TextLeaf.
     */
    private fun preProcessTextLeafs(logEntryComponents: List<TextLeaf>): List<LogEntryComponent> {
        return Observable.fromIterable(logEntryComponents)
                .flatMap { logEntryComponent -> Observable.fromIterable(splitAtTravisCommands(logEntryComponent)) }
                .toList()
                .blockingGet()
    }

    /**
     * Turns a single TextLeaf with any number of travis_fold:... or travis_time:... commands into
     * multiple LogEntryComponents ensuring that each command has its own TravisCommandLeaf.
     */
    private fun splitAtTravisCommands(textLeaf: TextLeaf): List<LogEntryComponent> {
        val result = ArrayList<LogEntryComponent>()
        val matcher = TRAVIS_COMMAND.matcher(textLeaf.text)
        while (matcher.find()) {
            // Create a new node for the part before the travis command (if non-empty)
            if (matcher.start() > 0) {
                val splitFront = TextLeaf(textLeaf.options)
                splitFront.text = textLeaf.text.substring(0, matcher.start())
                result.add(splitFront)
            }

            // Create a node for the command
            result.add(TravisCommandLeaf(matcher.group(1), matcher.group(2), matcher.group(3)))

            // Set text to what remains after the command
            textLeaf.text = textLeaf.text.substring(matcher.end())
            matcher.reset(textLeaf.text)
        }
        result.add(textLeaf)
        return result
    }

    /**
     * Converts a list of LogEntryComponents containing TextLeafs and TravisCommandLeafs into a
     * hierarchically structured LogEntryComposite.
     */
    private fun parseFoldTree(logEntryComponents: List<LogEntryComponent>): LogEntryComposite {
        val root = LogEntryComposite(null)
        val foldStack = Stack<LogEntryComposite>()
        foldStack.push(root)
        for (logEntryComponent in logEntryComponents) {
            if (logEntryComponent is TravisCommandLeaf) {
                handleTravisCommand(foldStack, logEntryComponent)
            } else {
                foldStack.peek().append(logEntryComponent)
            }
        }
        return root
    }

    private fun handleTravisCommand(foldStack: Stack<LogEntryComposite>, command: TravisCommandLeaf) {
        if (command.command == TRAVIS_FOLD) {
            if (command.type == START) {
                val logEntryComposite = LogEntryComposite(command.name)
                foldStack.peek().append(logEntryComposite)
                foldStack.push(logEntryComposite)
            } else if (command.type == END) {
                foldStack.pop()
            }
        }
    }
}
