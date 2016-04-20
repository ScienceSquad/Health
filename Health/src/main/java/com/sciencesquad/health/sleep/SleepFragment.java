package com.sciencesquad.health.sleep;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.EventBus;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.AlarmSender;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import java8.util.function.Function;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.concurrent.TimeUnit;

import static com.sciencesquad.health.core.util.AnimationUtils.*;

/**
 * TODO: Alarms should be set-any, repeat-any.
 */
public class SleepFragment extends BaseFragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	/**
	 * The tag ID for each tile to get the cycle out.
	 */
	private static final int TILE_CYCLE = R.string.accept;

	/**
	 * Internal SleepModule reference.
	 */
	private SleepModule module;

	/**
	 * Cached theme colors.
	 */
	private int[] colors;

	/**
	 * @see BaseFragment
	 */
	@Override
	protected Configuration getConfiguration() {
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
		xml().setModule((module = Module.of(SleepModule.class)));
		xml().setFragment(this);

		// Prepare resources for configuration.
		Drawable zzz = getTintedDrawable(this, R.drawable.ic_menu_sleep, Color.WHITE);
		colors = getThemeColors(getInflater().getContext());
		Stream<CardView> tiles = StreamSupport
				.of(xml().tile1, xml().tile2, xml().tile3, xml().tile4,
						xml().tile5, xml().tile6, xml().tile7, xml().tile8);
		Stream<CardView> alarms = StreamSupport
				.of(xml().alarm1, xml().alarm2, xml().alarm3, xml().alarm4,
						xml().alarm5, xml().alarm6, xml().alarm7);

		// Setup the Toolbar, ViewPager, and FAB.
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());
		xml().fab.setImageDrawable(zzz);
		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		// Prepare to show a dialog when waking up.
		bus().subscribe("SleepWakeAlarmEvent", null, ev -> {
			SleepMonitoringService.stopMonitoringService();
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
		});

		// Prepare to show a time picker on each alarm.
		alarms.forEach(a -> a.setOnClickListener(v -> {
			int day = DayOfWeek.from(LocalDateTime.now()).getValue();
			LocalTime alarm = this.module.alarms[day];

			new TimePickerDialog(getInflater().getContext(), (picker, h, m) -> {
				app().display("Setting alarm to " + h + ":" + m, false);
				this.module.alarms[day] = LocalTime.of(h, m);

				TextView fixme = (TextView)a.findViewWithTag("time");
				//Log.i(TAG, "got " + fixme + " -> " + this.module.timeForDayOfWeek(day));
				fixme.setText(this.module.timeForDayOfWeek(day));
			}, alarm.getHour(), alarm.getMinute(), false).show();
		}));

		// Set up the sleep now FAB.
		// 15 min to fall asleep, 90 min cycles. FIXME
		xml().fab.setOnClickListener(v -> {
			AlarmSender sender = new AlarmSender();
			sender.setTimeInMillis(TimeUnit.MINUTES.toMillis(1));
			sender.setAlarm(this, EventBus.intentForEvent(app(), "SleepWakeAlarmEvent"));
			SleepMonitoringService.startMonitoringService();

			int day = DayOfWeek.from(LocalDateTime.now()).getValue();
			LocalTime alarm = this.module.alarms[day];
			LocalTime now = LocalTime.now();
			long min = now.until(alarm, ChronoUnit.MINUTES);
			//app().display("min = " + min + " | diff = " + min % 90, false);
			now = now.plus(min - min % 90, ChronoUnit.MINUTES);

			String txt = now.format(DateTimeFormatter.ofPattern("h:mm a"));
			Snackbar.make(v, "Good night! I'll wake you up at " + txt + "!", Snackbar.LENGTH_LONG).show();
		});

		// For each tile, configure their behavior.
		tiles.forEach(c -> {
			int _id = Integer.valueOf((String)c.getTag());
			c.setTag(TILE_CYCLE, 0);
			TextView t = (TextView) c.findViewWithTag("text");
			t.setText(SleepModule.nameForPosition(_id));
		});

		// Quick bootstrap to stop all tiles.
		bus().subscribe("StoppedSleepSoundsEvent", null, ev -> {
			tiles.forEach(c -> handleTileCycle(c, o -> 0));
		});
	}

	// Snaps the color cycle range: [0%, 25%, 50%, 75%, 100%]
	public void onTileClick(View c) {
		handleTileCycle((CardView)c, o -> o >= 4 ? 0 : o + 1);
	}

	// Snaps the color cycle range: [0%, 100%]
	public boolean onTileLongClick(View c) {
		handleTileCycle((CardView)c, o -> o > 0 ? 0 : 4);
		return true;
	}

	// 1. Snap between range [0%, (25%, 50%, 75%,) 100%]
	// 2. Save and load this cycle from the tile's tag.
	// 3. Interpolate the colorPrimaryDark and colorAccent.
	// 4. Determine proportion using cycle and animate it.
	// 5. Keep track of tile cycles and control volume levels.
	private void handleTileCycle(CardView c, Function<Integer, Integer> cycler) {
		int id = Integer.valueOf((String)c.getTag());
		int oldCycle = (Integer) c.getTag(TILE_CYCLE);
		int newCycle = cycler.apply(oldCycle);

		// Update the settings and trigger changes.
		int color1 = interpolate(colors[1], colors[2], oldCycle / 4.0f);
		int color2 = interpolate(colors[1], colors[2], newCycle / 4.0f);
		animateCardViewColor(c, color1, color2).start();
		c.setTag(TILE_CYCLE, newCycle);
		this.module.setPlayerVolume(id, newCycle);
	}
}
