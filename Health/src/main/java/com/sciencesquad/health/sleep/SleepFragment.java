package com.sciencesquad.health.sleep;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.transition.Visibility;
import android.util.Log;
import android.util.Property;
import android.view.View;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.AnimationUtils;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

public class SleepFragment extends BaseFragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	@Override
	protected Configuration getConfiguration() {
		String _ = SleepModule.TAG; // instantiates the Module...
		return new Configuration(
				TAG, "Sleep", R.drawable.ic_menu_sleep,
				R.style.AppTheme_Sleep, R.layout.fragment_sleep
		);
	}

	// Our generated binding class is different...
	@Override @SuppressWarnings("unchecked")
	protected FragmentSleepBinding xml() {
		return super.xml();
	}

	@Override
	public void onSetupTransition() {
		this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
		this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		xml().setModule(Module.moduleForClass(SleepModule.class));

		// Grab a white-tinted sleep icon.
		Drawable zzz = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_sleep);
		zzz.setTint(Color.WHITE);

		// Prepare the sleep dialog.
		bus(b -> track(b.subscribe("SoundServiceStopEvent", null, ev -> {
			new MaterialStyledDialog(getActivity())
					.setIcon(zzz)
					.setCustomView(getInflater().inflate(R.layout.fragment_sleep_userinput, null))
					.withDialogAnimation(true, Duration.FAST)
					.setCancelable(false)
					.setPositive(getResources().getString(R.string.accept),
							(dialog, which) -> Log.d(TAG, "Accepted!"))
					.setNegative(getResources().getString(R.string.decline),
							(dialog, which) -> Log.d(TAG, "Declined!"))
					.show();
		})));

		// Setup the Toolbar.
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		// Configure the FAB.
		xml().fab.setImageDrawable(zzz);
		xml().fab.setOnClickListener(view2 -> {
			if (!SoundService.isSoundServiceActive())
				SoundService.startSoundService();
			else SoundService.stopSoundService();
		});

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		// Disgusting code for cool animations!
		// Use a view tag to keep track of activation state,
		// and animate the card's background when long-pressed.
		// TODO: Use 5 volume levels (25% step) triggered by onClick
		// TODO: Blend off and on colors and animate when tapped
		int colors[] = getThemeColors(getInflater().getContext());
		Stream<CardView> tiles = StreamSupport
				.of(xml().tile1, xml().tile2, xml().tile3, xml().tile4,
					xml().tile5, xml().tile6, xml().tile7, xml().tile8);
		tiles.forEach(c -> {
			c.setTag(R.string.accept, false);
			c.setOnLongClickListener(v -> {
				if((Boolean)c.getTag(R.string.accept)) {
					animateCardView(c, colors[2], colors[1]).start();
					c.setTag(R.string.accept, false);
				} else {
					animateCardView(c, colors[1], colors[2]).start();
					c.setTag(R.string.accept, true);
				}
				return true;
			});
		});
	}

	//
	// ---
	//

	// fancy animation!!
	private static Animator animateCardView(final CardView ctx, final int oldColor, final int newColor) {
		ObjectAnimator animator = ObjectAnimator.ofInt(ctx, new Property<CardView, Integer>(int.class, "cardBackgroundColor") {
			int prevColor = oldColor;
			public Integer get(CardView ctx) {
				return prevColor;
			}
			public void set(CardView ctx, Integer value) {
				ctx.setCardBackgroundColor((prevColor = value));
			}
		}, newColor);
		animator.setDuration(350L);
		animator.setEvaluator(new ArgbEvaluator());
		animator.setInterpolator(AnimationUtils.MaterialInterpolator.getInstance());
		return animator;
	}
}
