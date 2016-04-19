package com.sciencesquad.health.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import com.sciencesquad.health.core.util.AlarmSender;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import static com.sciencesquad.health.core.util.AnimationUtils.*;

/**
 *
 */
public class SleepFragment extends BaseFragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	/**
	 * The tag ID for each tile to get the cycle out.
	 */
	private static final int TILE_ID = R.string.decline;

	/**
	 * The tag ID for each tile to get the cycle out.
	 */
	private static final int TILE_CYCLE = R.string.accept;

	/**
	 * Internal SleepModule reference.
	 */
	private SleepModule module;

	/**
	 * @see BaseFragment
	 */
	@Override
	protected Configuration getConfiguration() {
		String notUnderscore = SleepModule.TAG; // instantiates the Module...
		return new Configuration(
				TAG, "Sleep", R.drawable.ic_menu_sleep,
				R.style.AppTheme_Sleep, R.layout.fragment_sleep
		);
	}

	/**
	 * Our generated binding class is different...
	 * @see BaseFragment
	 */
	@Override @SuppressWarnings("unchecked")
	protected FragmentSleepBinding xml() {
		return super.xml();
	}

	/**
	 * To provide a Circular Reveal animation.
	 * @see BaseFragment
	 */
	@Override
	public void onSetupTransition() {
		this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
		this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
	}

	/**
	 * @see BaseFragment
	 */
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		xml().setModule((module = Module.moduleForClass(SleepModule.class)));

		// Prepare the sleep dialog.
		Drawable zzz = getTintedDrawable(this, R.drawable.ic_menu_sleep, Color.WHITE);
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

		// Setup the Toolbar, ViewPager, and FAB.
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());
		xml().fab.setImageDrawable(zzz);
		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		// Set up the alarm handler.
		xml().fab.setOnClickListener(v -> {
			// 15 min to fall asleep, 90 min cycles. FIXME
			TimePickerDialog tpd = TimePickerDialog.newInstance((l, h, m, s) -> {
				Toast.makeText(getActivity(), "Got " + h + " " + m + " " + s, Toast.LENGTH_LONG).show();
			}, 8, 30, false);
			tpd.show(getFragmentManager(), "TPD");

			/*
			AlarmSender sender = new AlarmSender();
			sender.setTimeInMillis(1000 * 3);
			sender.setAlarm(this, new Intent(getActivity(), SleepWakeUpReceiver.class)); */
		});

		// Set up a list of tiles for the ambience mixer.
		int colors[] = getThemeColors(getInflater().getContext());
		Stream<CardView> tiles = StreamSupport
				.of(xml().tile1, xml().tile2, xml().tile3, xml().tile4,
						xml().tile5, xml().tile6, xml().tile7, xml().tile8);

		// For each tile, configure the behavior like so:
		// 1. Snap between range [0%, (25%, 50%, 75%,) 100%]
		// 2. Save and load this cycle from the tile's tag.
		// 3. Interpolate the colorPrimaryDark and colorAccent.
		// 4. Determine proportion using cycle and animate it.
		// 5. Keep track of tile cycles and control volume levels.
		tiles.forEach(c -> {

			// Configure tags and text.
			int _id = Integer.valueOf((String) c.getTag());
			c.setTag(TILE_ID, _id);
			c.setTag(TILE_CYCLE, 0);
			TextView t = (TextView) c.findViewWithTag("text");
			if (_id < SoundService.wav_map.length)
				t.setText(SoundService.wav_map[_id]);
			else t.setText("other");

			c.setOnClickListener(v -> {
				int id = (Integer) c.getTag(TILE_ID);
				int cycle = (Integer) c.getTag(TILE_CYCLE);

				// Snaps the color cycle range: [0%, 25%, 50%, 75%, 100%]
				int color1 = interpolate(colors[1], colors[2], cycle / 4.0f);
				cycle = cycle >= 4 ? 0 : cycle + 1;
				int color2 = interpolate(colors[1], colors[2], cycle / 4.0f);
				animateCardViewColor(c, color1, color2).start();

				// Update the settings and trigger changes.
				c.setTag(TILE_CYCLE, cycle);
				this.module.setTileCycle(id, cycle);
			});
			c.setOnLongClickListener(v -> {
				int id = (Integer) c.getTag(TILE_ID);
				int cycle = (Integer) c.getTag(TILE_CYCLE);

				// Snaps the color cycle range: [0%, 100%]
				int color1 = interpolate(colors[1], colors[2], cycle / 4.0f);
				cycle = cycle > 0 ? 0 : 4;
				int color2 = interpolate(colors[1], colors[2], cycle / 4.0f);
				animateCardViewColor(c, color1, color2).start();

				// Update the settings and trigger changes.
				c.setTag(TILE_CYCLE, cycle);
				this.module.setTileCycle(id, cycle);
				return true;
			});
		});
	}
}
