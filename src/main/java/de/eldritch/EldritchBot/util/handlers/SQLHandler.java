package de.eldritch.EldritchBot.util.handlers;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SQLHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        System.out.println(record.getMillis() +
                " - " + record.getSourceClassName() +
                "#" + record.getSourceMethodName() +
                " - " + record.getMessage());
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
