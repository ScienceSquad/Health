package com.sciencesquad.health.core.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java8.util.concurrent.CompletableFuture;
import java8.util.function.Supplier;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public enum Dispatcher {

	/**
	 * Priority of actions involved in updating the user interface.
	 */
	UI,

	/**
	 * Priority of actions when running a user interface that the user
	 * is currently interacting with.
	 */
	USER,

	/**
	 * Standard priority of actions with no context.
	 */
	DEFAULT,

	/**
	 * Priority of actions that have less of a chance chance to impact
	 * the responsiveness of the user interface.
	 */
	UTILITY,

	/**
	 * Priority for actions that really, really don't want to run if
	 * anything else is happening. Do not use frivolously.
	 */
	BACKGROUND;

	/**
	 * Currently only SERIAL and CONCURRENT execution is supported.
	 */
	public enum ExecutionMode {
		SERIAL, CONCURRENT;
	}

	/**
	 * Which mode of execution should be taken for actions.
	 */
	private ExecutionMode _executionMode;

	/**
	 * Execute an action on the current Dispatcher with a "rain-check".
	 *
	 * Using the Future provided, the action may become synchronous or asynchronous
	 * depending on whether `.then()` or `.wait()` is called. Asynchronous
	 * is preferred, unless a UI action or something similar requires sequential
	 * processing.
	 *
	 * @param action the action to execute on the Dispatcher
	 */
	@NonNull public CompletableFuture<Void> run(@NonNull Runnable action) {
		return this.run(() -> {
			action.run();
			return null;
		}, 0, null);
	}

	/**
	 * Execute an action on the current Dispatcher with a "rain-check".
	 * The Future returned contains the result of the action provided.
	 *
	 * Using the Future provided, the action may become synchronous or asynchronous
	 * depending on whether `.then()` or `.wait()` is called. Asynchronous
	 * is preferred, unless a UI action or something similar requires sequential
	 * processing.
	 *
	 * @param action the action to execute on the UI thread
	 */
	@NonNull public <T> CompletableFuture<T> run(@NonNull Supplier<T> action) {
		return this.run(action, 0, null);
	}

	/**
	 * Execute an action on the current Dispatcher with a "rain-check"
	 * after a specified duration, used as a scheduling delay.
	 *
	 * Using the Future provided, the action may become synchronous or asynchronous
	 * depending on whether `.then()` or `.wait()` is called. Asynchronous
	 * is preferred, unless a UI action or something similar requires sequential
	 * processing.
	 *
	 * @param action the action to execute on the UI thread
	 * @param duration the delay time after which the action is executed
	 * @param timeUnit the time unit corresponding to duration
	 */
	@NonNull public CompletableFuture<Void> run(@NonNull Runnable action,
										long duration, @Nullable TimeUnit timeUnit) {
		return this.run(() -> {
			action.run();
			return null;
		}, duration, timeUnit);
	}

	/**
	 * Execute an action on the current Dispatcher with a "rain-check"
	 * after a specified duration, used as a scheduling delay.
	 * The Future returned contains the result of the action provided.
	 *
	 * Using the Future provided, the action may become synchronous or asynchronous
	 * depending on whether `.then()` or `.wait()` is called. Asynchronous
	 * is preferred, unless a UI action or something similar requires sequential
	 * processing.
	 *
	 * @implNote Executing an action after a duration of time is not possible
	 * if the execution mode is set to SERIAL, inherently, and will be ignored.
	 *
	 * @param action the action to execute on the UI thread
	 * @param duration the delay time after which the action is executed
	 * @param timeUnit the time unit corresponding to duration
	 */
	@NonNull public <T> CompletableFuture<T> run(@NonNull Supplier<T> action,
										 long duration, @Nullable TimeUnit timeUnit) {
		final CompletableFuture<T> future = new CompletableFuture<>();

		// If duration and timeUnit aren't present, we're not scheduling the action.
		long time = (duration == 0 || timeUnit == null) ? 0 : timeUnit.toMillis(duration);

		// Wrap the callable or runnable to interact with the future.
		Runnable wrapper = () -> {

			// Cache and set the thread priority before execution.
			int _id = Process.getThreadPriority(Process.myTid());
			switch (this) {
				case UI: break; // Special handling.
				case USER:
					// FIXME: This might actually not work. See docs.
					Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND); break;
				case DEFAULT:
					Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT); break;
				case UTILITY:
					Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND); break;
				case BACKGROUND:
					Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST); break;
			}

			// Invoke the action, and fulfill the future by either completing with
			// the return value of the action, or exceptionally with the throwable.
			// Before returning, the thread MUST be returned to its original priority.
			try {
				future.complete(action.get());
			} catch (Exception e) {
				future.completeExceptionally(e);
			} finally {
				Process.setThreadPriority(_id);
			}
		};

		// Depending on which Dispatch we are, execute the action differently.
		switch (this) {
			case UI:
				MAINTHREAD.postDelayed(wrapper, time);
				break;
			case USER:
			case DEFAULT:
			case UTILITY:
			case BACKGROUND:
				if (this._executionMode == ExecutionMode.SERIAL)
					SERIAL.execute(wrapper);
				else CONCURRENT.schedule(wrapper, time, TimeUnit.MILLISECONDS);
				break;
		}
		return future;
	}

	/**
	 * Pauses the Dispatcher's execution of any current actions.
	 * Unsupported for Dispatcher.UI (the main thread).
	 *
	 * This is a dangerous call! Only perform on a Dispatcher you own.
	 */
	public void pause() {
		switch (this) {
			case UI: break; // Unsupported
			case USER:
			case DEFAULT:
			case UTILITY:
			case BACKGROUND:
				CONCURRENT.pause();
				break;
		}
	}

	/**
	 * Resumes the Dispatcher's execution of any current actions.
	 * Unsupported for Dispatcher.UI (the main thread).
	 *
	 * This is a dangerous call! Only perform on a Dispatcher you own.
	 */
	public void resume() {
		switch (this) {
			case UI: break; // Unsupported
			case USER:
			case DEFAULT:
			case UTILITY:
			case BACKGROUND:
				CONCURRENT.resume();
				break;
		}
	}

	/**
	 * Checks if the Dispatcher's execution is currently paused.
	 * Unsupported for Dispatcher.UI (the main thread).
	 *
	 * @return whether the Dispatcher is paused.
	 */
	public boolean isPaused() {
		switch (this) {
			case UI: return false; // Unsupported
			case USER:
			case DEFAULT:
			case UTILITY:
			case BACKGROUND:
				return CONCURRENT.isPaused;
		}
		return false;
	}

	/**
	 * Sets the execution mode for the Dispatcher.
	 * Unsupported for Dispatcher.UI (the main thread).
	 *
	 * @param executionMode the execution mode.
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		if (this != Dispatcher.UI)
			this._executionMode = executionMode;
	}

	/**
	 * Internal container for ScheduledThreadPoolExecutor that supports all
	 * the Dispatcher-required features (i.e. pause/resume, priorities).
	 */
	private static class PausableExecutor extends ScheduledThreadPoolExecutor {
		boolean isPaused;
		private ReentrantLock pauseLock = new ReentrantLock();
		private Condition unpaused = pauseLock.newCondition();

		public PausableExecutor(int corePoolSize) {
			super(corePoolSize);
		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			pauseLock.lock();
			try {
				while (isPaused) unpaused.await();
			} catch (InterruptedException ie) {
				t.interrupt();
			} finally {
				pauseLock.unlock();
			}
		}

		public void pause() {
			pauseLock.lock();
			try {
				isPaused = true;
			} finally {
				pauseLock.unlock();
			}
		}

		public void resume() {
			pauseLock.lock();
			try {
				isPaused = false;
				unpaused.signalAll();
			} finally {
				pauseLock.unlock();
			}
		}
	}

	/**
	 * Internal container for ScheduledThreadPoolExecutor that supports all
	 * the Dispatcher-required features (i.e. SERIAL execution mode).
	 */
	private static class SerialExecutor implements Executor {
		final ArrayDeque<Runnable> tasks = new ArrayDeque<>();
		final Executor internal;
		Runnable active;

		public SerialExecutor(Executor internal) {
			this.internal = internal;
		}

		public synchronized void execute(@NonNull final Runnable r) {
			tasks.offer(() -> {
				try {
					r.run();
				} finally {
					scheduleNext();
				}
			});

			if (active == null)
				scheduleNext();
		}

		protected synchronized void scheduleNext() {
			if ((active = tasks.poll()) != null)
				this.internal.execute(active);
		}
	}

	// Private Executor for non-MAIN priorities.
	private static int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2 + 1;
	private static Handler MAINTHREAD = new Handler(Looper.getMainLooper());
	private static PausableExecutor CONCURRENT = new PausableExecutor(PROCESSORS);
	private static SerialExecutor SERIAL = new SerialExecutor(CONCURRENT);
}
