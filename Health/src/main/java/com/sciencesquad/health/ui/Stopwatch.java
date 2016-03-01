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

    private final long MAX_MILLIS = 1000;
    private final long MAX_SECONDS = 60;
    private final long MAX_MINUTES = 60;
    private final long MAX_HOURS = 24;
    private final float START_ANGLE = (float) Math.PI / 2;
    private final float WATCH_DIRECTION = -1;


    private Duration remaining;
    private Duration elapsed;

    private long prevTime = 0;

    private ArrayList<Duration> laps;

    private Handler handler = null;

    private boolean running = false;

    private int interval = 1000 / 30;

    private boolean debug = false;

    private int pauseAfter = 0;
    private int resumeAfter = 0;

    private Runnable onTimeChange = null;

    public Runnable createRunnable(Stopwatch stopwatch) {
        return new Runnable() {
            public void run() {
                if (stopwatch.isRunning()) {
                    stopwatch.updateTime();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (stopwatch.interval - now % stopwatch.interval);
                    getHandler().postAtTime(stopwatch.msTicker, next);
                }
                else {
                    stopwatch.getHandler().removeCallbacks(msTicker);
                }
            }
        };
    }

    private Runnable msTicker = createRunnable(this);

    public enum WatchMode {
        DOWN, UP, ALARM
    }

    private WatchMode mode = WatchMode.UP;


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

    /** get[Unit]Remaining
     * If total == true,
     *  return total time
     * else
     *  return the corresponding component
     *
     * Example:
     *  If the total time is 354.056 seconds,
     *      getMillisRemaining(false) returns 56
     *      getMillisRemaining(true) returns 354056
     *      getSecondsRemaining(false) returns 54
     *      getSecondsRemaining(true) returns 354
     *      getMinutesRemaining(false) returns 5
     *      getMinutesRemaining(true) returns 5
     */
    public long getMillis(Duration duration, boolean total) {
        long totalMillis = duration.toMillis();
        if (total) {
            return totalMillis;
        }
        return totalMillis % MAX_MILLIS;
    }

    public long getMillisElapsed(boolean total) {
        return getMillis(this.elapsed, total);
    }

    public long getMillisRemaining(boolean total) {
        return getMillis(this.remaining, total);
    }

    public long getSeconds(Duration duration, boolean total) {
        long totalSeconds = duration.minusMillis(this.getMillis(duration, false))
                .getSeconds();
        if (total) {
            return totalSeconds;
        }
        return totalSeconds % MAX_SECONDS;
    }

    public long getSecondsElapsed(boolean total) {
        return getSeconds(this.elapsed, total);
    }

    public long getSecondsRemaining(boolean total) {
        return getSeconds(this.remaining, total);
    }

    public long getMinutes(Duration duration, boolean total) {
        long totalMinutes = duration.minusSeconds(this.getSeconds(duration, false))
                .toMinutes();
        if (total) {
            return totalMinutes;
        }
        return totalMinutes % MAX_MINUTES;
    }

    public long getMinutesElapsed(boolean total) {
        return getMinutes(this.elapsed, total);
    }

    public long getMinutesRemaining(boolean total) {
        return getMinutes(this.remaining, total);
    }

    public long getHours(Duration duration, boolean total) {
        long totalHours = duration.minusMinutes(this.getMinutes(duration, false))
                .toHours();
        if (total) {
            return totalHours;
        }
        return totalHours % MAX_HOURS;
    }

    public long getHoursElapsed(boolean total) {
        return getHours(this.elapsed, total);
    }

    public long getHoursRemaining(boolean total) {
        return getHours(this.remaining, total);
    }

    public long getDays(Duration duration) {
        return duration.minusHours(this.getHours(duration, false))
                .toDays();
    }

    public long getDaysElapsed() {
        return getDays(this.elapsed);
    }

    public long getDaysRemaining() {
        return getDays(this.remaining);
    }


	/**
     * Functions for clock hands
     * Return the angle of the hand (NOT relative to "12 o'clock")
     */

    public float getAngle(float value, float maxValue) {
        return this.START_ANGLE + (this.WATCH_DIRECTION * 2 * (float) Math.PI * value / maxValue);
    }

    public float getMillisecondHandAngle() {
        Duration duration = this.getDurationForMode();
        float millis = this.getMillis(duration, false);
        return this.getAngle(millis, MAX_MILLIS);
    }

    public float getSecondHandAngle() {
        Duration duration = this.getDurationForMode();
        float millis = this.getMillis(duration, false);
        float seconds = this.getSeconds(duration, false) + (millis / MAX_MILLIS);
        return this.getAngle(seconds, MAX_SECONDS);
    }

    public float getMinuteHandAngle() {
        Duration duration = this.getDurationForMode();
        float seconds = this.getSeconds(duration, false);
        float minutes = this.getMinutes(duration,false) + (seconds / MAX_SECONDS);
        return this.getAngle(minutes, MAX_MINUTES);
    }

    public float getHourHandAngle() {
        Duration duration = this.getDurationForMode();
        float minutes = this.getMinutes(duration, false);
        float hours = this.getHours(duration, false) + (minutes / MAX_MINUTES);
        return this.getAngle(hours, MAX_HOURS);
    }

    /** printTime
     *
     * Prints the time in a nice format, with padded zeroes, colons, periods... the whole shebang!
     */
    public String getPrettyTime(Duration duration) {
        String milliseconds = String.format("%03d", this.getMillis(duration, false));
        String seconds = String.format("%02d", this.getSeconds(duration, false));
        String minutes = String.format("%02d", this.getMinutes(duration, false));
        String hours = String.format("%02d", this.getHours(duration, false));
        String days = String.valueOf(this.getDays(duration));
        String prettyTime = days + ":" + hours + ":"
                + minutes + ":" + seconds + "." + milliseconds;
        return prettyTime;
    }

    public String getPrettyElapsed() {
        return getPrettyTime(this.elapsed);
    }

    public String getPrettyRemaining() {
        return getPrettyTime(this.remaining);
    }

    public void printTime() {
        System.out.println("Time remaining: " + this.getPrettyRemaining());
    }


    /** getMode
     *
     * Returns the current mode of the Stopwatch as a string
     *
     * UP, DOWN, or ALARM
     * @return
     */
    public WatchMode getMode() {
        return this.mode;
    }

    /** setMode I
     *
     * Uses the herein defined WatchMode enum to set the current mode
     */
    public void setMode(WatchMode mode) {
        this.mode = mode;
    }

    public Duration getDurationForMode() {
        if (this.getMode() == WatchMode.UP) {
            return this.elapsed;
        }
        else {
            return this.remaining;
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
    }

    private void debugStuff() {
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

        if (this.onTimeChange != null) {
            this.onTimeChange.run();
        }
    }

    public void setOnTimeChange(Runnable onTimeChange) {
        this.onTimeChange = onTimeChange;
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
        if (this.running) return;
        this.prevTime = System.currentTimeMillis();
        this.running = true;
        this.init_interval();
    }

    /**
     * Though this calls resume, it is actually different from resume.
     * It starts the "otherWatch" if necessary.
     *
     * In actual production use of the class, though, it probably won't be
     * any different.
     */
    public void start() {
        this.resume();
    }

    public void pause() {
        this.updateTime();
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
