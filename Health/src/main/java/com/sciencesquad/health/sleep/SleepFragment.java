package com.sciencesquad.health.sleep;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.sciencesquad.health.core.util.X;
import rx.Subscription;

// TODO: Preference for roommate/partner sleeping in same bed, other room, none.
public class SleepFragment extends Fragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	private View internalDialog;
	private Subscription stopEvent;
	private BottomBar mBottomBar;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		internalDialog = inflater.inflate(R.layout.fragment_sleep_userinput, null);
		return inflater.inflate(R.layout.fragment_sleep, container, false);
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
		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.setImageDrawable(zzz);
		fab.setOnClickListener(view2 -> {
			if (!SoundService.isSoundServiceActive())
				SoundService.startSoundService();
			else SoundService.stopSoundService();
		});

		// TODO: Use attachShy...
		mBottomBar = BottomBar.attach(view, savedInstanceState);
		mBottomBar.setItemsFromMenu(R.menu.menu_sleep_nav, new OnMenuTabClickListener() {
			@Override
			public void onMenuTabSelected(@IdRes int menuItemId) {
				if (menuItemId == R.id.item1) {
					// The user selected item number one.
				}
			}

			@Override
			public void onMenuTabReSelected(@IdRes int menuItemId) {
				if (menuItemId == R.id.item1) {
					// The user reselected item number one, scroll your content to top.
				}
			}
		});

		mBottomBar.mapColorForTab(0, 0xFF0000FF);
		mBottomBar.mapColorForTab(1, 0xFF5D40FF);
		mBottomBar.mapColorForTab(2, 0x7B1FA2FF);
		mBottomBar.mapColorForTab(3, 0xFF5D40FF);
		mBottomBar.mapColorForTab(4, 0x7B1FA2FF);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mBottomBar.onSaveInstanceState(outState); // eww
	}
}
