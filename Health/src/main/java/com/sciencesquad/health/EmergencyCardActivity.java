package com.sciencesquad.health;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sciencesquad.health.events.BaseActivity;
import com.sciencesquad.health.ui.EmergencyNotification;

public class EmergencyCardActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_card);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
			if (EmergencyNotification.hasSent())
				EmergencyNotification.closeNotification(view.getContext());
			else
				EmergencyNotification.sendNotification(view.getContext(),
						"Rad Notification", "The fish was delish and it made quite a dish");
		});
	}

}
