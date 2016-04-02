package com.sciencesquad.health.core.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;

public class AnimationUtils {
	private static final Property<View, Integer> BG_COLOR_PROPERTY =
			new Property<View, Integer>(int.class, "backgroundColor") {
				@Override
				public Integer get(View object) {
					ColorDrawable a = (ColorDrawable) object.getBackground();
					return a != null ? a.getColor() : Color.TRANSPARENT;
				}

				@Override
				public void set(View object, Integer value) {
					object.setBackgroundColor(value);
				}
			};

	private static final Property<Activity, Integer> STATUS_PROPERTY =
			new Property<Activity, Integer>(int.class, "statusBarColor") {
				@Override
				public Integer get(Activity ctx) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
						return ctx.getWindow().getStatusBarColor();
					return Color.BLACK;
				}

				@Override
				public void set(Activity ctx, Integer value) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
						ctx.getWindow().setStatusBarColor(value);
				}
			};

	private static final Property<Activity, Integer> NAVBAR_PROPERTY =
			new Property<Activity, Integer>(int.class, "navigationBarColor") {
				@Override
				public Integer get(Activity ctx) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
						return ctx.getWindow().getNavigationBarColor();
					return Color.BLACK;
				}

				@Override
				public void set(Activity ctx, Integer value) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
						ctx.getWindow().setNavigationBarColor(value);
				}
			};

	public static Animator animateBackgroundColor(View view, int color) {
		ObjectAnimator animator = ObjectAnimator.ofInt(view, BG_COLOR_PROPERTY, color);
		animator.setDuration(350L);
		animator.setEvaluator(new ArgbEvaluator());
		animator.setInterpolator(MaterialInterpolator.getInstance());
		return animator;
	}

	public static Animator animateStatusBarColor(final Activity ctx, final int color) {
		ObjectAnimator animator = ObjectAnimator.ofInt(ctx, STATUS_PROPERTY, color);
		animator.setDuration(350L);
		animator.setEvaluator(new ArgbEvaluator());
		animator.setInterpolator(MaterialInterpolator.getInstance());
		return animator;
	}

	public static Animator animateNavigationBarColor(final Activity ctx, final int color) {
		ObjectAnimator animator = ObjectAnimator.ofInt(ctx, NAVBAR_PROPERTY, color);
		animator.setDuration(350L);
		animator.setEvaluator(new ArgbEvaluator());
		animator.setInterpolator(MaterialInterpolator.getInstance());
		return animator;
	}

	public static Animator animateCircularReveal(View v, int cx, int cy, float sr, float er) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			return ViewAnimationUtils.createCircularReveal(v, cx, cy, sr, er);
		return ObjectAnimator.ofFloat(v, View.ALPHA, 1.0f, 0.0f);
	}

	public static Animator animateParallelly(Animator... a) {
		Animator list[] = new Animator[a.length];
		System.arraycopy(a, 0, list, 0, a.length);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list);
		return set;
	}

	public static Animator animateSequentially(Animator... a) {
		Animator list[] = new Animator[a.length];
		System.arraycopy(a, 0, list, 0, a.length);

		AnimatorSet set = new AnimatorSet();
		set.playSequentially(list);
		return set;
	}

	public static ActivityOptions scaleUp(View view) {
		view.setDrawingCacheEnabled(true);
		view.setPressed(false);
		view.refreshDrawableState();
		Bitmap bitmap = view.getDrawingCache();
		bitmap = bitmap.copy(bitmap.getConfig(), false);
		ActivityOptions opts = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0);
		view.setDrawingCacheEnabled(false);
		return opts;
	}

	public static class MaterialInterpolator implements Interpolator {
		private static final MaterialInterpolator INSTANCE = new MaterialInterpolator();

		public static MaterialInterpolator getInstance() {
			return INSTANCE;
		}

		@Override
		public float getInterpolation(float x) {
			return (float) (6 * Math.pow(x, 2) - 8 * Math.pow(x, 3) + 3 * Math.pow(x, 4));
		}
	}
}