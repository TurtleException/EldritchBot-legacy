package de.eldritch.EldritchBot.util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class IdGenerator {
    public static int generateTicketId() {
        int returnID = 0;
        try {
            int looper = 0;
            while (looper < 1024) {
                looper++;   // give up after 1024 tries --> FIX LATER, THIS IS LAZY!!! <-- TODO

                StringBuilder str = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 8; i++)
                    str.append(random.nextInt(10));
                int num = Integer.parseInt(str.toString());

                ResultSet res = sqlConnector.execute("SELECT COUNT(*) FROM tickets WHERE ID = " + num);
                res.first();

                if (num > 9999999 && res.getInt(1) == 0) {
                    returnID = num;
                    break;
                }
            }
        } catch (SQLException e) {
            logger.warning("Unable to generate new ticket ID due to a SQLException!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
            return 0;
        }

        return returnID;
    }

    public static int getLogFileId(File logPath, String prePattern) {
        File[] files = logPath.listFiles();

        if (files == null || files.length == 0)
            return 0;

        int max = 0;

        for (File file : files) {
            if (file.getName().startsWith(prePattern) && file.getName().endsWith(".log")) {
                int tmp;
                try {
                    tmp = Integer.parseInt(file.getName().substring(file.getName().length() - 5, file.getName().length() - 4));

                    if (tmp > max)
                        max = tmp;
                } catch (NumberFormatException ignored) {

                }
            }
        }

        return max + 1;
    }
}
