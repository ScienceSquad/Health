package com.sciencesquad.health;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sciencesquad.health.activity.ActivityFragment;
import com.sciencesquad.health.alarm.AlarmFragment;
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

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.health_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//*/

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
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new ActivityFragment(), "ACTIVITY")
					.addToBackStack("ACTIVITY")
					.commit();
		} else if (id == R.id.nav_sleep) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new SleepFragment(), "SLEEP")
					.addToBackStack("SLEEP")
					.commit();
		} else if (id == R.id.nav_steps) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new StepsFragment(), "STEPS")
					.addToBackStack("STEPS")
					.commit();
		} else if (id == R.id.nav_workout) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_container, new WorkoutFragment(), "WORKOUT")
					.addToBackStack("WORKOUT")
					.commit();
		} else if (id == R.id.nav_clock) {
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content, new ClockFragment(), "CLOCK")
					.addToBackStack("CLOCK")
					.commit();
		} else if (id == R.id.nav_alarm) {
			getFragmentManager()
					.beginTransaction()
					.replace(android.R.id.content, new AlarmFragment(), "ALARM")
					.addToBackStack("ALARM")
					.commit();
		} else if (id == R.id.nav_nutrition) {
			this.getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_main_activity, new NutritionFragment(), "NUTRITION")
					.addToBackStack("NUTRITION")
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
