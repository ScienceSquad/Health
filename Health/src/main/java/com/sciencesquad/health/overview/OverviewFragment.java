package com.sciencesquad.health.overview;

import android.app.Fragment;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.databinding.FragmentSleepBinding;
import com.sciencesquad.health.sleep.SleepModule;

public class OverviewFragment extends BaseFragment {
    public static final String TAG = OverviewFragment.class.getSimpleName();

    @Override
    protected BaseFragment.Configuration getConfiguration() {
        String _ = OverviewModule.TAG; // instantiates the Module...
        return new BaseFragment.Configuration(
                TAG, "Overview", R.drawable.ic_menu_overview,
                R.style.AppTheme_Overview, R.layout.fragment_overview
        );
    }

    @Override
    public void onSetupTransition() {
        this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
        this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //xml().setModule(Module.moduleForClass(OverviewModule.class));

        // view or getView()?
        PieChart chart = (PieChart) view.findViewById(R.id.overviewChart);

        // FABulous!!!
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.overviewFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setSelected(fab.isSelected());
                fab.setImageResource(R.drawable.ic_plus);
                Drawable drawable = fab.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
    }
}
