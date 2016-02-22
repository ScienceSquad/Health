package com.sciencesquad.health.core;

import java.util.concurrent.ForkJoinPool;

public class Dispatch {
	private static final String TAG = Dispatch.class.getSimpleName();

	private static int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;
	private static ForkJoinPool POOL = new ForkJoinPool(PROCESSORS);

	/**
	 * Returns the concurrent execution service for this Module.
	 *
	 * @return a ForkJoinPool to be used for computationally intensive tasks.
	 */
	protected ForkJoinPool executor() {
		return Dispatch.POOL;
	}
}
