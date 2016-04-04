package com.sciencesquad.health.sleep;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.core.util.X;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import rx.Subscription;

// TODO: Preference for roommate/partner sleeping in same bed, other room, none.
public class SleepFragment extends BaseFragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	private View internalDialog;
	private Subscription stopEvent;

	@Override
	protected Configuration getConfiguration() {
		return new Configuration(
				TAG, "Sleep", SleepModule.class, R.drawable.ic_menu_sleep,
				R.style.AppTheme_Sleep, R.layout.fragment_sleep
		);
	}

	// Our generated binding class is different...
	@Override @SuppressWarnings("unchecked")
	protected FragmentSleepBinding xml() {
		return super.xml();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		xml().setModule(new SleepModule()); // FIXME: oops...

		// Grab a white-tinted sleep icon.
		Drawable zzz = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_sleep);
		zzz.setTint(Color.WHITE);

		// Prepare the sleep dialog.
		// FIXME: Just a demo.
		X.of(BaseApp.app()).map(BaseApp::eventBus).let(bus -> {
			stopEvent = bus.subscribe(SoundServiceStopEvent.class, null, ev -> {
				new MaterialStyledDialog(getActivity())
						.setIcon(zzz)
						.setCustomView(internalDialog)
						.withDialogAnimation(true, Duration.FAST)
						.setCancelable(false)
						.setPositive(getResources().getString(R.string.accept),
								(dialog, which) -> Log.d(TAG, "Accepted!"))
						.setNegative(getResources().getString(R.string.decline),
								(dialog, which) -> Log.d(TAG, "Declined!"))
						.show();
			});
		});

		// Configure the FAB.
		xml().fab.setImageDrawable(zzz);
		xml().fab.setOnClickListener(view2 -> {
			if (!SoundService.isSoundServiceActive())
				SoundService.startSoundService();
			else SoundService.stopSoundService();
		});

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);
	}
}
