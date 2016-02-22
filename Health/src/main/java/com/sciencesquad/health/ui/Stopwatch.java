package com.sciencesquad.health.ui;


import org.threeten.bp.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 2/21/16.
 */
public class Stopwatch {

    private Duration remaining;
    private Duration elapsed;
    private long prevTime;


    public Stopwatch() {
        remaining = Duration.ZERO;
        elapsed = Duration.ZERO;
    }


    // START add/subtract time
    public void addTime(long toAdd, TimeUnit tu) {
        this.remaining.plusMillis(tu.toMillis(toAdd));
    }

    public void subtractTime(long toSubtract, TimeUnit tu) {
        this.remaining.minusMillis(tu.toMillis(toSubtract));
    }

    public void plusMillis(long toAdd) {
        this.remaining.plusMillis(toAdd);
    }

    public void minusMillis(long toSubtract) {
        this.remaining.minusMillis(toSubtract);
    }

    public void plusSeconds(long toAdd) {
        this.remaining.plusSeconds(toAdd);
    }

    public void minusSeconds(long toSubtract) {
        this.remaining.minusSeconds(toSubtract);
    }

    public void plusMinutes(long toAdd) {
        this.remaining.plusMinutes(toAdd);
    }

    public void minusMinutes(long toSubtract) {
        this.remaining.minusMinutes(toSubtract);
    }

    public void plusHours(long toAdd) {
        this.remaining.plusHours(toAdd);
    }

    public void minusHours(long toSubtract) {
        this.remaining.minusHours(toSubtract);
    }

    public void plusDays(long toAdd) {
        this.remaining.plusDays(toAdd);
    }

    public void minusDays(long toSubtract) {
        this.remaining.minusDays(toSubtract);
    }
    // END add/subtract time


    public void updateTime() {
        long currentTime = System.currentTimeMillis();
        this.remaining.minusMillis(currentTime - this.prevTime);
        this.elapsed.plusMillis(currentTime - this.prevTime);
    }


    public void resume() {

    }

    public void start() {
        this.prevTime = System.currentTimeMillis();
    }

    public void pause() {

    }
}
