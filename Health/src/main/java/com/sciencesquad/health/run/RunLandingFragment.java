package com.sciencesquad.health.run;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.databinding.FragmentRunLandingBinding;

//import android.app.Fragment;

public class RunLandingFragment extends BaseFragment{
    public static final String TAG = RunLandingFragment.class.getSimpleName();


    @Override
    protected Configuration getConfiguration() {
        String notAnUnderscore = RunLandingFragment.TAG; // instantiates the Module...
        return new Configuration(
                TAG, "RunLandingFragment", R.drawable.ic_fitness_center_24dp,
                R.style.AppTheme_Run, R.layout.fragment_run_landing
        );
    }

    /**
     * Our generated binding class is different...
     * @see BaseFragment
     */
    @Override @SuppressWarnings("unchecked")
    protected FragmentRunLandingBinding xml() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run_landing, container, false);
        //Button
        final Button button1 = (Button) view.findViewById(R.id.buttonFreeRun);
        final Button button2 = (Button) view.findViewById(R.id.buttonBuildRoute);
        final Button button3 = (Button) view.findViewById(R.id.buttonTrainingProgram);
        //Button Click
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button1Clicked(v);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button2Clicked(v);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button3Clicked(v);
            }
        });
        return view;
    }

    public void button1Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new RunFragment(), RunFragment.TAG)
                .addToBackStack(RunFragment.TAG)
                .commit();
    }
    public void button2Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new CreateRouteFragment(), CreateRouteFragment.TAG)
                .addToBackStack(CreateRouteFragment.TAG)
                .commit();
    }
    public void button3Clicked(View view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new RunTrainingFragment(), RunTrainingFragment.TAG)
                .addToBackStack(RunTrainingFragment.TAG)
                .commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the Toolbar, ViewPager, and FAB.
        //xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());
        //StaticPagerAdapter.install(xml().pager);
        //xml().tabs.setupWithViewPager(xml().pager);
    }

}
