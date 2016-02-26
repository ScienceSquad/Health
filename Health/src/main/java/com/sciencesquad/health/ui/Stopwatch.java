package com.sciencesquad.health.ui;


import android.os.SystemClock;
import android.os.Handler;

import org.threeten.bp.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 2/21/16.
 */
public class Stopwatch {

    private Duration remaining;
    private Duration elapsed;

    private long prevTime = 0;

    private ArrayList<Duration> laps;

    private Handler handler = null;

    private boolean running = false;

    private int interval = 100;

    private boolean debug = false;

    private int pauseAfter = 0;
    private int resumeAfter = 0;

    private Stopwatch otherWatch = null;

    public Runnable createRunnable(Stopwatch stopwatch) {
        return new Runnable() {
            public void run() {
                stopwatch.updateTime();
                long now = SystemClock.uptimeMillis();
                long next = now + (stopwatch.interval - now % stopwatch.interval);
                getHandler().postAtTime(stopwatch.msTicker, next);
            }
        };
    }

    private Runnable msTicker = createRunnable(this);

    enum WatchMode {
        DOWN, UP, ALARM
    }

    WatchMode mode;


    public Stopwatch() {
        remaining = Duration.ZERO;
        elapsed = Duration.ZERO;
    }


    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    // START add/subtract time
    public void addTime(long toAdd, TimeUnit tu) {
        this.remaining = this.remaining.plusMillis(tu.toMillis(toAdd));
    }

    public void subtractTime(long toSubtract, TimeUnit tu) {
        this.remaining = this.remaining.minusMillis(tu.toMillis(toSubtract));
    }

    public void plusMillis(long toAdd) {
        this.remaining = this.remaining.plusMillis(toAdd);
    }

    public void minusMillis(long toSubtract) {
        this.remaining = this.remaining.minusMillis(toSubtract);
    }

    public void plusSeconds(long toAdd) {
        this.remaining = this.remaining.plusSeconds(toAdd);
    }

    public void minusSeconds(long toSubtract) {
        this.remaining = this.remaining.minusSeconds(toSubtract);
    }

    public void plusMinutes(long toAdd) {
        this.remaining = this.remaining.plusMinutes(toAdd);
    }

    public void minusMinutes(long toSubtract) {
        this.remaining = this.remaining.minusMinutes(toSubtract);
    }

    public void plusHours(long toAdd) {
        this.remaining = this.remaining.plusHours(toAdd);
    }

    public void minusHours(long toSubtract) {
        this.remaining = this.remaining.minusHours(toSubtract);
    }

    public void plusDays(long toAdd) {
        this.remaining = this.remaining.plusDays(toAdd);
    }

    public void minusDays(long toSubtract) {
        this.remaining = this.remaining.minusDays(toSubtract);
    }
    // END add/subtract time

    public long getMillisRemaining() {
        return this.remaining.toMillis() % 1000;
    }

    public long getSecondsRemaining() {
        return this.remaining.minusMillis(this.getMillisRemaining())
                .getSeconds()
                % 60;
    }

    public long getMinutesRemaining() {
        return this.remaining.minusSeconds(this.getSecondsRemaining())
                .toMinutes()
                % 60;
    }

    public long getHoursRemaining() {
        return this.remaining.minusMinutes(this.getMinutesRemaining())
                .toHours()
                % 24;
    }

    public long getDaysRemaining() {
        return this.remaining.minusHours(this.getHoursRemaining())
                .toDays();
    }

    public void init_interval() {
        msTicker.run();
    }

    public void printTime() {
        String milliseconds = String.format("%04d", this.getMillisRemaining());
        String seconds = String.format("%02d", this.getSecondsRemaining());
        String minutes = String.format("%02d", this.getMinutesRemaining());
        String hours = String.format("%02d", this.getHoursRemaining());
        String days = String.valueOf(this.getDaysRemaining());
        System.out.println("Time remaining: " + days + ":" + hours + ":"
                + minutes + ":" + seconds + "." + milliseconds);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug) {
            this.otherWatch = new Stopwatch();
        }
    }

    public void setPauseAfter(int pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    public void setResumeAfter(int resumeAfter) {
        if (resumeAfter > this.pauseAfter) this.resumeAfter = resumeAfter;
    }

    public void updateTime() {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - this.prevTime;
        if (this.prevTime > 0) {
            if (this.mode == WatchMode.DOWN) {
                this.remaining = this.remaining.minusMillis(diff);
            }
            this.elapsed = this.elapsed.plusMillis(diff);
        }
        this.prevTime = currentTime;

        /** DEBUG!!
         */
        if (this.debug && (this.pauseAfter != 0) && (this.elapsed.getSeconds() >= pauseAfter)) this.pause();
        if (this.debug && (this.pauseAfter != 0) && (this.elapsed.getSeconds() >= pauseAfter)) this.pause();
        if (debug) this.printTime();
        /** end DEBUG **/

        if (!running) {
            this.getHandler().removeCallbacks(msTicker);
        }
    }

    public void addLap() {

    }

    public void reset() {
        if (this.mode == WatchMode.DOWN) {
            this.remaining = this.remaining.plusMillis(this.elapsed.toMillis());
        }
        this.elapsed = Duration.ZERO;
    }

    public void resume() {
        this.prevTime = System.currentTimeMillis();
        this.running = true;
        this.init_interval();
    }

    public void start() {
        if (!this.running) this.resume();
    }

    public void pause() {
        this.running = false;
    }

    public Duration getElapsedDuration() {
        return elapsed;
    }

    public boolean isRunning() {
        return this.running;
    }

    public Duration getRemainingDuration() {
        return remaining;
    }
}
