package dev.wand.data.command;

import dev.wand.util.TimeUtil;
import lombok.Getter;

/**
 * Represents a parsed punish command.
 */
@Getter
public class ParsedCommand {
    private long time;
    private String reason;
    private String modifier;

    public ParsedCommand(long time, String reason, String modifier) {
        this.time = time;
        this.reason = reason;
        this.modifier = modifier;
    }
}
