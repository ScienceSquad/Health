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

    public final long MAX_MILLIS = 1000;
    public final long MAX_SECONDS = 60;
    public final long MAX_MINUTES = 60;
    public final long MAX_HOURS = 24;

    public final float START_ANGLE = (float) Math.PI / 2;
    public final float WATCH_DIRECTION = -1;


    private Duration remaining;
    private Duration elapsed;
    private Duration total;

    private long prevTime = 0;

    private ArrayList<Duration> laps;

    private Handler handler = null;

    private boolean running = false;
    private boolean finished = false;

    private int interval = 1000 / 30;

    private Runnable onTimeChange = null;
    private Runnable onTimeFinish = null;

	/**
	 * Returns a runnable which is run at every interval
	 */
    public Runnable createRunnable(Stopwatch stopwatch) {
        return () -> {
			if (stopwatch.isRunning()) {
				stopwatch.updateTime();
				long now = SystemClock.uptimeMillis();
				long next = now + (stopwatch.interval - now % stopwatch.interval);
				getHandler().postAtTime(stopwatch.msTicker, next);
			} else {
				stopwatch.getHandler().removeCallbacks(msTicker);
			}
		};
    }

    private Runnable msTicker = createRunnable(this);

    public enum WatchMode {
        DOWN, UP
    }

    private WatchMode mode = WatchMode.UP;


    /** CONSTRUCTOR
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
        this.total = this.remaining;
    }

    public void minusMillis(long toSubtract) {
        this.remaining = this.remaining.minusMillis(toSubtract);
        this.total = this.remaining;
    }

    public void plusSeconds(long toAdd) {
        this.remaining = this.remaining.plusSeconds(toAdd);
        this.total = this.remaining;
    }

    public void minusSeconds(long toSubtract) {
        this.remaining = this.remaining.minusSeconds(toSubtract);
        this.total = this.remaining;
    }

    public void plusMinutes(long toAdd) {
        this.remaining = this.remaining.plusMinutes(toAdd);
        this.total = this.remaining;
    }

    public void minusMinutes(long toSubtract) {
        this.remaining = this.remaining.minusMinutes(toSubtract);
        this.total = this.remaining;
    }

    public void plusHours(long toAdd) {
        this.remaining = this.remaining.plusHours(toAdd);
        this.total = this.remaining;
    }

    public void minusHours(long toSubtract) {
        this.remaining = this.remaining.minusHours(toSubtract);
        this.total = this.remaining;
    }

    public void plusDays(long toAdd) {
        this.remaining = this.remaining.plusDays(toAdd);
        this.total = this.remaining;
    }

    public void minusDays(long toSubtract) {
        this.remaining = this.remaining.minusDays(toSubtract);
        this.total = this.remaining;
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

    public float getDotAngle() {
        if (this.mode == WatchMode.UP) {
            float millis = this.getMillis(this.elapsed, false);
            float seconds = this.getSeconds(this.elapsed, false) + (millis / MAX_MILLIS);
            return this.getAngle(seconds, MAX_SECONDS);
        }
        float totalMillis = this.getMillis(this.total, true);
        float remainingMillis = this.getMillis(this.remaining, true);
        return this.getAngle(remainingMillis, totalMillis);
    }

    /** printTime
     *
     * Prints the time in a nice format, with padded zeroes, colons, periods... the whole shebang!
     */
    public String getPrettyTime(Duration duration, boolean all) {
        long numSeconds = this.getSeconds(duration, false);
        long numMinutes = this.getMinutes(duration, false);
        long numHours = this.getHours(duration, false);
        long numDays = this.getDays(duration);
        String seconds = String.format("%02d", numSeconds);
        String minutes = String.format("%02d", numMinutes);
        String hours = String.format("%02d", numHours);
        String days = String.valueOf(this.getDays(duration));
        String prettyTime = minutes + " " + seconds + " ";
        if ((numHours > 0) && (!all)) {
            prettyTime = hours + " " + prettyTime;
        }
        else if (numDays > 0) {
            prettyTime = days + " " + hours + " " + prettyTime;
        }
        return prettyTime;
    }

    public String getMilliString(Duration duration) {
        return String.format("%03d", this.getMillis(duration, false));
    }

    public String getPrettyElapsed(boolean all) {
        return getPrettyTime(this.elapsed, all);
    }

    public String getPrettyRemaining(boolean all) {
        return getPrettyTime(this.remaining, all);
    }

    public void printTime() {
        System.out.println("Time remaining: " + this.getPrettyRemaining(false));
    }


    /** getMode
     *
     * Returns the current mode of the Stopwatch
     *
     * UP, DOWN, or ALARM
     * @return
     */
    public WatchMode getMode() {
        return this.mode;
    }

    /** setMode
     *
     * Uses the herein defined WatchMode enum to set the current mode
     */
    public void setMode(WatchMode mode) {
        this.mode = mode;
    }


	/**
	 * Returns the proper Duration object for the current mode
	 * @return
	 */
    public Duration getDurationForMode() {
        if (this.getMode() == WatchMode.UP) {
            return this.elapsed;
        }
        else {
            return this.remaining;
        }
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
                if (this.getMillis(this.remaining, true) < 0) {
                    this.remaining = Duration.ZERO;
                    this.finished = true;
                    this.finish();
                }
            }
            this.elapsed = this.elapsed.plusMillis(diff);
        }

        this.prevTime = currentTime;

        if (this.onTimeChange != null) {
            this.onTimeChange.run();
        }
    }

	/** setOnTimeChange
	 * Pass in a Runnable that will run every time the time is updated!
	 * @param onTimeChange
	 */
    public void setOnTimeChange(Runnable onTimeChange) {
        this.onTimeChange = onTimeChange;
    }

	/** setOnTimeFinish
	 * Pass in a Runnable that will run when the countdown timer finishes
	 * @param onTimeFinish
	 */
    public void setOnTimeFinish(Runnable onTimeFinish) {
        this.onTimeFinish = onTimeFinish;
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
        if (this.running || this.finished) return;
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
        if (!this.finished)
            this.updateTime();
        this.running = false;
    }

    public void finish() {
        this.pause();

        if (this.onTimeFinish != null) {
            this.onTimeFinish.run();
        }
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
		// TODO in Sprint 2, toodaloo
    }

    public boolean isRunning() {
        return this.running;
    }
	public boolean isFinished() { return this.finished; }

    public Duration getRemainingDuration() {
        return remaining;
    }

    public Duration getElapsedDuration() {
        return elapsed;
    }
}
