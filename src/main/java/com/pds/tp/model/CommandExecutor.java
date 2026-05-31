package com.pds.tp.model;

import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class CommandExecutor {
    private final Stack<ScrimCommand> history = new Stack<>();

    public void executeCommand(ScrimCommand command, ScrimContext context) {
        command.execute(context);
        history.push(command);
    }

    public void undoLastCommand(ScrimContext context) {
        if (!history.isEmpty()) {
            ScrimCommand lastCommand = history.pop();
            lastCommand.undo(context);
        }
    }
}