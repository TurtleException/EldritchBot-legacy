package de.eldritch.EldritchBot.util;

import de.eldritch.EldritchBot.EldritchBot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String time     = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String level    = String.format("%7s", record.getLevel().getName());

        StringBuilder str      = new StringBuilder();

        if (record.getThrown() == null) {
            str.append("[")
                    .append(time)
                    .append(" ")
                    .append(level)
                    .append("]: ")
                    .append("[")
                    // removes the "de.eldritch.EldritchBot" from the start of every log
                    .append(record.getSourceClassName().substring(EldritchBot.class.getPackage().getName().length() + 1))
                    .append("]  ")
                    .append(record.getMessage())
                    .append("\n");
        } else {
            str.append("\t").append(record.getThrown().getMessage()).append("\n");

            StackTraceElement[] stackTrace = record.getThrown().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                str.append("\t\t")
                        .append(stackTraceElement)
                        .append("\n");
            }
        }

        return str.toString();
    }
}
