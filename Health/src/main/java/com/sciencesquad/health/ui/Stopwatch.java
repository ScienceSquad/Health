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

    private enum WatchMode {
        DOWN, UP, ALARM
    }

    private WatchMode mode;


    /** CONSTRUCTOR
     * (no arguments, yet)
     */
    public Stopwatch() {
        remaining = Duration.ZERO;
        elapsed = Duration.ZERO;
    }


    // INTERVAL MANAGEMENT
    // Functions for running the clock

    /** getHandler
     * A singleton getter for a handler
     * - necessary for the stopwatch to run
     *
     * @return new Handler
     */
    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    /** addTime/subtractTime
     * These functions use the TimeUnit class to convert from any time unit to milliseconds and add
     * to the stopwatch time.
     * I don't think these will be particularly useful, but they're here if you want them.
     */
    public void addTime(long toAdd, TimeUnit tu) {
        this.remaining = this.remaining.plusMillis(tu.toMillis(toAdd));
    }

    public void subtractTime(long toSubtract, TimeUnit tu) {
        this.remaining = this.remaining.minusMillis(tu.toMillis(toSubtract));
    }

    /** plus[Unit]/minus[Unit]
     * These functions use functions in the Java Time package's Duration class
     * to add time to the stopwatch.
     * All relevant units are available
     * Nanoseconds are too small.
     * Years are too large.
     */

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



    /** printTime
     *
     * Prints the time in a nice format, with padded zeroes, colons, periods... the whole shebang!
     */
    public void printTime() {
        String milliseconds = String.format("%04d", this.getMillisRemaining());
        String seconds = String.format("%02d", this.getSecondsRemaining());
        String minutes = String.format("%02d", this.getMinutesRemaining());
        String hours = String.format("%02d", this.getHoursRemaining());
        String days = String.valueOf(this.getDaysRemaining());
        System.out.println("Time remaining: " + days + ":" + hours + ":"
                + minutes + ":" + seconds + "." + milliseconds);
    }


    /** getMode
     *
     * Returns the current mode of the Stopwatch as a string
     *
     * UP, DOWN, or ALARM
     * @return
     */
    public String getMode() {
        switch (this.mode) {
            case DOWN: return "DOWN";
            case UP: return "UP";
            case ALARM: return "ALARM";
            default: return "";
        }
    }

    /** setMode I
     *
     * Uses the herein defined WatchMode enum to set the current mode
     */
    private void setMode(WatchMode mode) {
        this.mode = mode;
    }

    /** setMode II
     *
     * Takes a string argument and passes the corresponding WatchMode enum
     * to the other setMode function
     */
    public void setMode(String mode) {
        switch (mode) {
            case "DOWN": this.setMode(WatchMode.DOWN);
                break;
            case "UP": this.setMode(WatchMode.UP);
                break;
            case "ALARM": this.setMode(WatchMode.ALARM);
                break;
            default: return;
        }
    }


    /** Debug functions
     *
     * These are pretty much just for me
     */
    public void setDebug(boolean debug) {
        // Probably shouldn't be swapping to debug mode while it's running...
        if (this.running) return;

        this.debug = debug;

        if (debug) {
            this.otherWatch = new Stopwatch();
        }
    }

    public void setPauseAfter(int pauseAfter) {
        // Probably don't want to use this unless debug is on...
        if (!this.debug) return;

        this.pauseAfter = pauseAfter;
    }

    public void setResumeAfter(int resumeAfter) {
        // Probably don't want to use this unless debug is on...
        if (!this.debug) return;

        // Probably don't want the watch to resume before it pauses...
        if (resumeAfter > this.pauseAfter) this.resumeAfter = resumeAfter;
    }

    private void debugStuff() {
        if ((this.pauseAfter > 0) && (this.otherWatch.getElapsedDuration().getSeconds() >= this.pauseAfter)) this.pause();
        if ((this.resumeAfter > 0) && (this.otherWatch.getElapsedDuration().getSeconds() >= this.resumeAfter)) {
            this.resume();
            this.otherWatch.pause();
        }
        this.printTime();
    }


    /** Things to do at every interval
     * Occurs every "this.interval" milliseconds while the stopwatch is running
     */
    private void updateTime() {

        long currentTime = System.currentTimeMillis();
        long diff = currentTime - this.prevTime;

        if (this.prevTime > 0) {
            if (this.mode == WatchMode.DOWN) {
                this.remaining = this.remaining.minusMillis(diff);
            }
            this.elapsed = this.elapsed.plusMillis(diff);
        }

        this.prevTime = currentTime;

        if (this.debug) {
            debugStuff();
        }

        if (!this.running) {
            this.getHandler().removeCallbacks(msTicker);
        }
    }

    /** init_interval
     *
     * Just calls the Runnable to get the stopwatch running.
     * Probably could have just called the runnable directly instead of putting it in
     * another function, but phooey to that. I like this better.
     */
    private void init_interval() {
        msTicker.run();
    }

    /** Start controls
     * Functions for actually starting / stopping the stopwatch
     * Bind these to pretty buttons!
     */
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

    public void reset() {
        if (this.mode == WatchMode.DOWN) {
            this.remaining = this.remaining.plusMillis(this.elapsed.toMillis());
        }
        this.elapsed = Duration.ZERO;
    }

    /** Add a lap
     *
     * Still have yet to do this.
     * Once I write it, it'll probably add the elapsed time to a list and restart the stopwatch
     */
    public void addLap() {

    }

    public boolean isRunning() {
        return this.running;
    }

    public Duration getRemainingDuration() {
        return remaining;
    }

    public Duration getElapsedDuration() {
        return elapsed;
    }
}
