package com.khmelenko.lab.varis.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

public class TravisCommands {

    private static final String TRAVIS_TIME = "travis_time";
    private static final String TRAVIS_FOLD = "travis_fold";
    private static final String START = "start";
    private static final String END = "end";
    private static final Pattern TRAVIS_COMMAND = Pattern.compile('(' + TRAVIS_FOLD + '|' + TRAVIS_TIME + "):(" + START + '|' + END + "):(\\S+)");

    /**
     * Takes a list of TextLeafs and splits them when it contains a travis command into a
     * TextLeaf + TravisCommandLeaf + TextLeaf.
     */
    public static List<LogEntryComponent> preProcessTextLeafs(List<TextLeaf> logEntryComponents) {
        return Observable.fromIterable(logEntryComponents)
                .flatMap(logEntryComponent -> Observable.fromIterable(splitAtTravisCommands(logEntryComponent)))
                .toList()
                .blockingGet();
    }

    /**
     * Turns a single TextLeaf with any number of travis_fold:... or travis_time:... commands into
     * multiple LogEntryComponents ensuring that each command has its own TravisCommandLeaf.
     */
    private static List<LogEntryComponent> splitAtTravisCommands(TextLeaf textLeaf) {
        List<LogEntryComponent> result = new ArrayList<>();
        Matcher matcher = TRAVIS_COMMAND.matcher(textLeaf.getText());
        while (matcher.find()) {
            // Create a new node for the part before the travis command (if non-empty)
            if (matcher.start() > 0) {
                TextLeaf splitFront = new TextLeaf(textLeaf.getOptions());
                splitFront.setText(textLeaf.getText().substring(0, matcher.start()));
                result.add(splitFront);
            }

            // Create a node for the command
            result.add(new TravisCommandLeaf(matcher.group(1), matcher.group(2), matcher.group(3)));

            // Set text to what remains after the command
            textLeaf.setText(textLeaf.getText().substring(matcher.end()));
            matcher.reset(textLeaf.getText());
        }
        result.add(textLeaf);
        return result;
    }

    /**
     * Converts a list of LogEntryComponents containing TextLeafs and TravisCommandLeafs into a
     * hierarchically structured LogEntryComposite.
     */
    public static LogEntryComposite parseFoldTree(List<LogEntryComponent> logEntryComponents) {
        LogEntryComposite root = new LogEntryComposite(null);
        Stack<LogEntryComposite> foldStack = new Stack<>();
        foldStack.push(root);
        for (LogEntryComponent logEntryComponent : logEntryComponents) {
            if (logEntryComponent instanceof TravisCommandLeaf) {
                TravisCommandLeaf command = (TravisCommandLeaf) logEntryComponent;
                if (command.getCommand().equals(TRAVIS_FOLD)) {
                    if (command.getType().equals(START)) {
                        LogEntryComposite logEntryComposite = new LogEntryComposite(command.getName());
                        foldStack.peek().append(logEntryComposite);
                        foldStack.push(logEntryComposite);
                    } else if (command.getType().equals(END)) {
                        foldStack.pop();
                    }
                }
            } else {
                foldStack.peek().append(logEntryComponent);
            }
        }
        return root;
    }

}
