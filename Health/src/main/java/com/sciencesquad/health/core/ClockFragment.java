package com.sciencesquad.health.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;

import android.app.Fragment;

import android.view.ViewGroup;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.ui.EmergencyNotification;

public class ClockFragment extends Fragment {
	public static final String TAG = ClockFragment.class.getSimpleName();

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_clock, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(view2 -> {
			if (EmergencyNotification.hasSent()) {
				EmergencyNotification.closeNotification(view2.getContext());
			}
			else {
				EmergencyNotification.sendNotification(view2.getContext(), "THIS IS AN EMERGENCY",
						"This is actually a pretty big deal, you should be concerned.");
			}
		});
    }
}
