package com.sciencesquad.health.sleep;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.ui.EmergencyNotification;

// TODO: Preference for roommate/partner sleeping in same bed, other room, none.
public class SleepFragment extends Fragment {
	public static final String TAG = SleepFragment.class.getSimpleName();

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sleep, container, false);
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
