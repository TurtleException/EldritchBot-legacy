package de.eldritch.EldritchBot.modules.timer;

public abstract class Timer {
    public Timer(String[] timerAliases) {
        // aliases = timerAliases;
    }

    public abstract void onTimer();
}
