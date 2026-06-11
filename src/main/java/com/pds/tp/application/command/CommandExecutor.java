package com.pds.tp.application.command;

import com.pds.tp.domain.state.ScrimContext;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Executes commands and keeps a per-lobby history stack for undo support.
 * Thread-safe: each lobby has its own independent command stack.
 */
@Component
public class CommandExecutor {
    private final Map<UUID, Deque<ScrimCommand>> historyByLobby = new ConcurrentHashMap<>();

    public void executeCommand(ScrimCommand command, ScrimContext context) {
        command.execute(context);
        UUID lobbyId = context.getLobby().getId();
        historyByLobby.computeIfAbsent(lobbyId, k -> new ArrayDeque<>()).push(command);
    }

    public void undoLastCommand(ScrimContext context) {
        UUID lobbyId = context.getLobby().getId();
        Deque<ScrimCommand> history = historyByLobby.get(lobbyId);
        if (history != null && !history.isEmpty()) {
            ScrimCommand lastCommand = history.pop();
            lastCommand.undo(context);
        }
    }

    /**
     * Clears the command history for a given lobby (e.g., after scrim starts or is cancelled).
     */
    public void clearHistory(UUID lobbyId) {
        historyByLobby.remove(lobbyId);
    }
}
