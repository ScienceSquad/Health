package com.sciencesquad.health.sleep;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import static com.sciencesquad.health.core.util.AnimationUtils.animateCardViewColor;
import static com.sciencesquad.health.core.util.AnimationUtils.interpolate;

public class SleepFragment extends BaseFragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	private static final int TILE_ID = R.string.accept;

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
		SleepModule m = new SleepModule();
		m.init();
		xml().setModule(m); // FIXME

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
		xml().fab.setOnClickListener(v -> {
			// other thing here now.
		});

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		// Set up a list of tiles for the ambience mixer.
		int colors[] = getThemeColors(getInflater().getContext());
		Stream<CardView> tiles = StreamSupport
				.of(xml().tile1, xml().tile2, xml().tile3, xml().tile4,
					xml().tile5, xml().tile6, xml().tile7, xml().tile8);

		// For each tile, configure the behavior like so:
		// 1. Snap between range [0%, 25%, 50%, 75%, 100%]
		// 2. Save and load this cycle from the tile's tag.
		// 3. Interpolate the colorPrimaryDark and colorAccent.
		// 4. Determine proportion using cycle and animate it.
		// 5. Keep track of tile cycles and control volume levels.
		tiles.forEach(c -> {
			c.setTag(TILE_ID, 0);
			c.setOnClickListener(v -> {
				int cycle = (Integer)c.getTag(TILE_ID);

				// Interpolate the previous cycle into the next cycle color.
				// Then animate a transition to that color.
				// Snaps the color cycle range: [0%, 25%, 50%, 75%, 100%]
				int color1 = interpolate(colors[1], colors[2], cycle / 4.0f);
				cycle = cycle >= 4 ? 0 : cycle + 1;
				int color2 = interpolate(colors[1], colors[2], cycle / 4.0f);
				animateCardViewColor(c, color1, color2).start();

				c.setTag(TILE_ID, cycle);
			});
			c.setOnLongClickListener(v -> {
				int cycle = (Integer)c.getTag(TILE_ID);

				// Interpolate the previous cycle into the next cycle color.
				// Then animate a transition to that color.
				// Snaps the color cycle range: [0%, 25%, 50%, 75%, 100%]
				int color1 = interpolate(colors[1], colors[2], cycle / 4.0f);
				cycle = cycle > 0 ? 0 : 4;
				int color2 = interpolate(colors[1], colors[2], cycle / 4.0f);
				animateCardViewColor(c, color1, color2).start();

				c.setTag(TILE_ID, cycle);
				return true;
			});
		});
	}

	private void poo() {

		//if (!SoundService.isSoundServiceActive())
		//	SoundService.startSoundService();
		//else SoundService.stopSoundService();
	}
}
