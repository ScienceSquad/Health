package com.sciencesquad.health;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java8.util.concurrent.CompletableFuture;

import java.util.ArrayDeque;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public enum Dispatcher {

	/**
	 *
	 */
	UI,

	/**
	 *
	 */
	USER,

	/**
	 *
	 */
	DEFAULT,

	/**
	 *
	 */
	UTILITY,

	/**
	 *
	 */
	BACKGROUND;

	/**
	 * A convenience method to execute a Runnable action on the UI thread.
	 * This should be used for all UI-related or concurrent interactions.
	 *
	 * Using the Future provided, the action may become synchronous or asynchronous
	 * depending on whether `.then()` or `.wait()` is called. Asynchronous
	 * is preferred, unless a UI action or something similar requires sequential
	 * processing.
	 *
	 * @param action the action to execute on the Dispatcher
	 */
	public CompletableFuture<Void> run(@NonNull Runnable action) {
		return this.run(action, 0, null);
	}

	/**
	 * A convenience method to execute a Runnable action on the UI thread.
	 * This should be used for all UI-related interactions.
	 *
	 * @param action the action to execute on the UI thread
	 */
	public <T> CompletableFuture<T> run(@NonNull Callable<T> action) {
		return this.run(action, 0, null);
	}

	/**
	 * A convenience method to execute a Runnable action on the UI thread
	 * after a duration of time (i.e. delayed action).
	 * This should be used for all UI-related interactions.
	 *
	 * @param action the action to execute on the UI thread
	 * @param duration the delay time after which the action is executed
	 * @param timeUnit the time unit corresponding to duration
	 */
	public CompletableFuture<Void> run(@NonNull Runnable action, long duration, @Nullable TimeUnit timeUnit) {
		return this.run(() -> {
			action.run();
			return null;
		}, duration, timeUnit);
	}

	/**
	 * A convenience method to execute a Runnable action on the UI thread
	 * after a duration of time (i.e. delayed action).
	 * This should be used for all UI-related interactions.
	 *
	 * @param action the action to execute on the UI thread
	 * @param duration the delay time after which the action is executed
	 * @param timeUnit the time unit corresponding to duration
	 */
	public <T> CompletableFuture<T> run(@NonNull Callable<T> action, long duration, @Nullable TimeUnit timeUnit) {
		final CompletableFuture<T> future = new CompletableFuture<>();

		// If duration and timeUnit aren't present, we're not scheduling the action.
		long time = (duration == 0 || timeUnit == null) ? 0 : timeUnit.toMillis(duration);

		// Wrap the callable or runnable to interact with the future.
		Runnable func = () -> {
			try {
				future.complete(action.call());
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		};

		// Depending on which Dispatch we are, execute the action differently.
		// TODO: Currently does not support non-UI priorities.
		// TODO: Currently does not support SERIAL/CONCURRENT setting.
		switch (this) {
			case UI:
				MAINTHREAD.postDelayed(func, time);
				break;
			case USER:
			case DEFAULT:
			case UTILITY:
			case BACKGROUND:
				CONCURRENT.schedule(func, time, TimeUnit.MILLISECONDS);
				break;
		}
		return future;
	}

	/**
	 * Pauses the Dispatcher's execution of any current blocks.
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
	 * Resumes the Dispatcher's execution of any current blocks.
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
	 * Internal container for ScheduledThreadPoolExecutor that supports all
	 * the Dispatcher-required features (i.e. pause/resume, priorities).
	 */
	private static class PausableExecutor extends ScheduledThreadPoolExecutor {
		private boolean isPaused;
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
	// TODO: Use this.
	private static class SerialExecutor implements Executor {
		final ArrayDeque<Runnable> tasks = new ArrayDeque<>();
		final ThreadPoolExecutor internal;
		Runnable active;

		public SerialExecutor(ThreadPoolExecutor internal) {
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
	// TODO: Support SynchronousQueue and PriorityBlockingQueue.
	private static int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;
	private static Handler MAINTHREAD = new Handler(Looper.getMainLooper());
	private static PausableExecutor CONCURRENT = new PausableExecutor(PROCESSORS);
	private static SerialExecutor SERIAL = new SerialExecutor(CONCURRENT);
}
