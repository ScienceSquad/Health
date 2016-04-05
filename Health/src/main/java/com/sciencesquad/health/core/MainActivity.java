package com.sciencesquad.health.core;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sciencesquad.health.R;
import com.sciencesquad.health.run.RunFragment;
import com.sciencesquad.health.run.RunLandingFragment;
import com.sciencesquad.health.core.alarm.AlarmFragment;
import com.sciencesquad.health.nutrition.NutritionFragment;
import com.sciencesquad.health.sleep.SleepFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.workout.WorkoutFragment;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	private Toolbar toolbar;
	private DrawerLayout drawer;
	private NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Obtain all the view references.
		this.toolbar = (Toolbar) findViewById(R.id.toolbar);
		this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.navigationView = (NavigationView) findViewById(R.id.nav_view);

		// Configure things the way we want.
		setSupportActionBar(toolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
				toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		if (this.drawer.isDrawerOpen(GravityCompat.START))
			this.drawer.closeDrawer(GravityCompat.START);
		else super.onBackPressed();
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();

		/*
		// TODO: Experimental code...
		StreamSupport.stream(Module.registeredModules())
		.filter(a -> a.identifier().second == id)
		.findFirst()
		.ifPresent(module -> {
			// code here
			ContextBinder.bind(this, null, module, R.layout.sleep_layout, 0, () -> {
				Log.d(TAG, "Module bound!");
			});
		});
		//*/

		if (id == R.id.nav_run) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new RunLandingFragment(), RunLandingFragment.TAG)
					.addToBackStack(RunFragment.TAG)
					.commit();
		} else if (id == R.id.nav_sleep) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new SleepFragment(), SleepFragment.TAG)
					.addToBackStack(SleepFragment.TAG)
					.commit();
		} else if (id == R.id.nav_steps) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new StepsFragment(), StepsFragment.TAG)
					.addToBackStack(StepsFragment.TAG)
					.commit();
		} else if (id == R.id.nav_workout) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new WorkoutFragment(), WorkoutFragment.TAG)
					.addToBackStack(WorkoutFragment.TAG)
					.commit();
		} else if (id == R.id.nav_clock) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new ClockFragment(), ClockFragment.TAG)
					.addToBackStack(ClockFragment.TAG)
					.commit();
		} else if (id == R.id.nav_alarm) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new AlarmFragment(), AlarmFragment.TAG)
					.addToBackStack(AlarmFragment.TAG)
					.commit();
		} else if (id == R.id.nav_nutrition) {
			this.getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new NutritionFragment(), NutritionFragment.TAG)
					.addToBackStack(NutritionFragment.TAG)
					.commit();
		} else if (id == R.id.nav_manage) {
			// NOTHING HERE
		} else if (id == R.id.nav_share) {
			// NOTHING HERE
		} else if (id == R.id.nav_send) {
			// NOTHING HERE
		}

		this.drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
