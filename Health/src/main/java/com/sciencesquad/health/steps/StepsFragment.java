package com.sciencesquad.health.steps;

import android.os.Bundle;
import android.view.View;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.databinding.FragmentStepsBinding;

public class StepsFragment extends BaseFragment {
	public static final String TAG = StepsFragment.class.getSimpleName();

	@Override
	protected BaseFragment.Configuration getConfiguration() {
		return new BaseFragment.Configuration(
				TAG, "Steps", R.drawable.ic_menu_steps,
				R.style.AppTheme_Steps, R.layout.fragment_steps
		);
	}

	// Our generated binding class is different...
	@Override @SuppressWarnings("unchecked")
	protected FragmentStepsBinding xml() {
		return super.xml();
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());
    }
}