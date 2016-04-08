package com.sciencesquad.health.core;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.HostActivity;

public class ClockActivity extends HostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar
                .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
				.setAction("Action", null)
				.show());
    }

}