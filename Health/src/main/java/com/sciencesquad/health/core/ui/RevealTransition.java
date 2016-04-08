package com.sciencesquad.health.core.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import java.util.ArrayList;

public class RevealTransition extends Visibility {

	public RevealTransition() {
		super();
        /*
        <transition class="com.galaxas0.Quartz.ui.RevealTransition"
                    android:duration="350"
                    android:transitionVisibilityMode="mode_out"/>
        */
	}

	public RevealTransition(int mode) {
		super();
		setMode(mode);
	}

	public RevealTransition(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public Animator onAppear(ViewGroup sceneRoot,
							 final View view, TransitionValues startValues,
							 TransitionValues endValues) {
		float originalAlpha = view.getAlpha();
		view.setAlpha(0f);

		float radius = calculateMaxRadius(view);
		Animator reveal = createAnimator(view, 0, radius);

		reveal.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				view.setAlpha(originalAlpha);
			}
		});
		return reveal;
	}

	@Override
	public Animator onDisappear(ViewGroup sceneRoot,
								final View view, TransitionValues startValues,
								TransitionValues endValues) {
		return createAnimator(view, calculateMaxRadius(view), 0);
	}

	private static float calculateMaxRadius(View view) {
		double rx = view.getWidth() / 2;
		double ry = view.getHeight() / 2;
		return (float) Math.sqrt(rx * rx + ry * ry);
	}

	private static Animator createAnimator(View view, float startR, float endR) {
		float cx = ((float) view.getWidth()) / 2;
		float cy = ((float) view.getHeight()) / 2;
		Animator anim = ViewAnimationUtils.createCircularReveal(view, (int)cx, (int)cy, startR, endR);
		return new NoPauseAnimator(anim);
	}

	private static class NoPauseAnimator extends Animator {
		private final Animator mAnimator;
		private final ArrayMap<AnimatorListener, AnimatorListener> mListeners = new ArrayMap<>();

		public NoPauseAnimator(Animator animator) {
			mAnimator = animator;
		}

		@Override
		public void addListener(AnimatorListener listener) {
			AnimatorListener wrapper =
					new AnimatorListenerWrapper(mAnimator, listener);
			if (!mListeners.containsKey(listener)) {
				mListeners.put(listener, wrapper);
				mAnimator.addListener(listener);
			}
		}

		@Override
		public void cancel() {
			mAnimator.cancel();
		}

		@Override
		public void end() {
			mAnimator.end();
		}

		@Override
		public long getDuration() {
			return mAnimator.getDuration();
		}

		@Override
		public TimeInterpolator getInterpolator() {
			return mAnimator.getInterpolator();
		}

		@Override
		public ArrayList<AnimatorListener> getListeners() {
			return new ArrayList<>(mListeners.keySet());
		}

		@Override
		public long getStartDelay() {
			return mAnimator.getStartDelay();
		}

		@Override
		public boolean isPaused() {
			return mAnimator.isPaused();
		}

		@Override
		public boolean isRunning() {
			return mAnimator.isRunning();
		}

		@Override
		public void pause() {
			// don't allow pause
		}

		@Override
		public void removeAllListeners() {
			mListeners.clear();
			mAnimator.removeAllListeners();
		}

		@Override
		public void removeListener(AnimatorListener listener) {
			AnimatorListener wrapper = mListeners.get(listener);
			if (wrapper != null) {
				mListeners.remove(listener);
				mAnimator.removeListener(wrapper);
			}
		}

		@Override
		public void resume() {
			// don't allow resume
		}

		@Override
		public Animator setDuration(long durationMS) {
			mAnimator.setDuration(durationMS);
			return this;
		}

		@Override
		public void setInterpolator(TimeInterpolator timeInterpolator) {
			mAnimator.setInterpolator(timeInterpolator);
		}

		@Override
		public void setStartDelay(long delayMS) {
			mAnimator.setStartDelay(delayMS);
		}

		@Override
		public void setTarget(Object target) {
			mAnimator.setTarget(target);
		}

		@Override
		public void setupEndValues() {
			mAnimator.setupEndValues();
		}

		@Override
		public void setupStartValues() {
			mAnimator.setupStartValues();
		}

		@Override
		public void start() {
			mAnimator.start();
		}
	}

	private static class AnimatorListenerWrapper
			implements Animator.AnimatorListener {
		private final Animator mAnimator;
		private final Animator.AnimatorListener mListener;

		public AnimatorListenerWrapper(Animator animator,
									   Animator.AnimatorListener listener) {
			mAnimator = animator;
			mListener = listener;
		}

		@Override
		public void onAnimationStart(Animator animator) {
			mListener.onAnimationStart(mAnimator);
		}

		@Override
		public void onAnimationEnd(Animator animator) {
			mListener.onAnimationEnd(mAnimator);
		}

		@Override
		public void onAnimationCancel(Animator animator) {
			mListener.onAnimationCancel(mAnimator);
		}

		@Override
		public void onAnimationRepeat(Animator animator) {
			mListener.onAnimationRepeat(mAnimator);
		}
	}
}
