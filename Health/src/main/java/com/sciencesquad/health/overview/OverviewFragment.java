package com.sciencesquad.health.overview;

import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentOverviewBinding;

public class OverviewFragment extends BaseFragment {
    public static final String TAG = OverviewFragment.class.getSimpleName();

    private FloatingActionButton fab;
    private FloatingActionButton fab2; // dummy
    private FloatingActionButton fab3; // dummy
    private FloatingActionButton fab4; // dummy
    private Boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    PieChart mPieChart;
    private float[] yData = {5, 10, 15, 20, 25};
    private String[] xData = {"Nutrition", "Run & Cycle", "Sleep", "Steps", "Workout"};

    @Override
    protected BaseFragment.Configuration getConfiguration() {
        String overviewTag = OverviewModule.TAG; // instantiates the Module...
        return new BaseFragment.Configuration(
                TAG, "Overview", R.drawable.ic_menu_overview,
                R.style.AppTheme_Overview, R.layout.fragment_overview
        );
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentOverviewBinding xml() {
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
        //xml().setModule(Module.moduleForClass(OverviewModule.class));

        // Create tabs
        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        // Temporary code. This grabs the pie chart easily thanks to the xml() method
        mPieChart = (PieChart) xml().overviewChart;
        xml().page1.addView(mPieChart);
        mPieChart.setDescription("Daily Overview");
        mPieChart.setDescriptionColor(R.color.amber_50);
        mPieChart.setTouchEnabled(true);
        PieData data;
        mPieChart.setCenterText("78");
        mPieChart.invalidate();



        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backward);

        // FABulous!!!
        fab = xml().overviewFab;
        fab2 = xml().overviewFab2;
        fab2.hide();
        fab3 = xml().overviewFab3;
        fab3.hide();
        fab4 = xml().overviewFab4;
        fab4.hide();

        fab.setOnClickListener(v -> {
            if (!isFabOpen) {
                isFabOpen = true;
                animateFab();
                fab2.show();
                fab3.show();
                fab4.show();
            } else {
                isFabOpen = false;
                animateFab();
                fab2.hide();
                fab3.hide();
                fab4.hide();
            }
        });


        /** fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setSelected(fab.isSelected());
                fab.setImageResource(R.drawable.ic_plus);
                Drawable drawable = fab.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        }); */
    }


    public void animateFab() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            Log.d("Colin", "close");
        } else {
            fab.startAnimation(rotate_forward);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            Log.d("Colin","open");
        }
    }
}
