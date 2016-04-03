package com.sciencesquad.health.sleep;

import android.app.Fragment;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.core.util.X;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import rx.Subscription;

// TODO: Preference for roommate/partner sleeping in same bed, other room, none.
public class SleepFragment extends Fragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	private View internalDialog;
	private Subscription stopEvent;
	private FragmentSleepBinding binding;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		internalDialog = inflater.inflate(R.layout.fragment_sleep_userinput, null);
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sleep, container, false);
		binding.setModule(new SleepModule()); // TODO: Grab the Module singleton.
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

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
		this.binding.fab.setImageDrawable(zzz);
		this.binding.fab.setOnClickListener(view2 -> {
			if (!SoundService.isSoundServiceActive())
				SoundService.startSoundService();
			else SoundService.stopSoundService();
		});

		StaticPagerAdapter.install(this.binding.pager);
		this.binding.tabs.setupWithViewPager(this.binding.pager);
	}
}
