package me.gimme.gimmehcf.gameevent;

import org.bukkit.event.Listener;

public abstract class GameEvent implements Listener {

    private boolean active = false;

    protected boolean isActive() {
        return active;
    }

    void countdownPeriod() {
        active = false;
        onCountdownStart();
    }

    void start() {
        active = true;
        onStart();
    }

    void finish() {
        onFinish();
    }

    protected abstract void onCountdownStart();

    protected abstract void onStart();

    protected abstract void onFinish();

}
