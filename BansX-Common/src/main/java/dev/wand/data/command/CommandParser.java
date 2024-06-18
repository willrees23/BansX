package dev.wand.data.command;

import dev.wand.util.TimeUtil;

public class CommandParser {

    /**
     * Parses a punish command.
     *
     * @param args The arguments of the command
     * @return The parsed command.
     */
    public static ParsedCommand parsePunishCommand(String[] args) {
        String timeStringRegex = "(\\d+[msywdh]+)";
        String modifierRegex = "(-[sp])";

        long time = -1;
        String timeString = null;
        StringBuilder reason = null;
        String modifier = null;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (arg.matches(timeStringRegex) && timeString == null) {
                timeString = arg;
            } else if (arg.matches(modifierRegex) && modifier == null) {
                modifier = arg;
            } else {
                if (reason == null) {
                    reason = new StringBuilder(arg);
                } else {
                    reason.append(" ").append(arg);
                }
            }
        }

        if (timeString != null) {
            time = TimeUtil.timeToMillis(timeString);
        }

        if (reason == null) {
            reason = new StringBuilder("No reason specified.");
        }

        return new ParsedCommand(time, reason.toString(), modifier);
    }
}


