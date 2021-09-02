package de.eldritch.EldritchBot.util.handlers;

import java.io.IOException;
import java.util.logging.FileHandler;

public class CustomFileHandler extends FileHandler {
    protected String _CustomFileHandler_Pattern;

    public CustomFileHandler(String pattern) throws IOException, SecurityException {
        super(pattern);

        _CustomFileHandler_Pattern = pattern;
    }

    public String getCustomFileHandlerPattern() {
        return _CustomFileHandler_Pattern;
    }
}
