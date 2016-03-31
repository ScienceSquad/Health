package com.sciencesquad.health;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.sciencesquad.health.core.BaseActivity;
import com.sciencesquad.health.core.ContextBinder;
import com.sciencesquad.health.core.Module;
import java8.util.stream.StreamSupport;

import android.widget.Toast;
import com.sciencesquad.health.activity.MapsActivity;
import com.sciencesquad.health.alarm.AlarmFragment;
import com.sciencesquad.health.events.BaseActivity;
import com.sciencesquad.health.workout.WorkoutFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.nutrition.NutritionViewModel;
import com.sciencesquad.health.workout.WorkoutActivity;
import com.sciencesquad.health.steps.StepsViewModel;

public class MainActivity extends BaseActivity
		implements NavigationView.OnNavigationItemSelectedListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(view ->
				Snackbar.make(view, "Spaghett!", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show());

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.health_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		StreamSupport.stream(Module.registeredModules())
		.filter(a -> a.identifier().second == id)
		.findFirst()
		.ifPresent(module -> {
			// code here
			ContextBinder.bind(this, null, module, R.layout.sleep_layout, 0, () -> {
				Log.d(TAG, "Module bound!");
			});
		});
		

		if (id == R.id.nav_run) {
			//setContentView(R.layout.maplayout);
			startActivity(new Intent(this, MapsActivity.class));
		} else if (id == R.id.nav_sleep) {
			
		} else if (id == R.id.nav_steps) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new StepsFragment(), "STEPS")
					.addToBackStack("STEPS")
					.commit();
		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		} else if (id == R.id.nav_workout) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content_container, new WorkoutFragment(), "WORKOUT")
					.addToBackStack("WORKOUT")
					.commit();
		} else if (id == R.id.nav_clock) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new ClockFragment(), "CLOCK")
					.addToBackStack("CLOCK")
					.commit();
		} else if (id == R.id.nav_alarm) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new AlarmFragment(), "ALARM")
					.addToBackStack("ALARM")
					.commit();
		} else if (id == R.id.nav_nutrition) {
			NutritionViewModel nextFrag= new NutritionViewModel();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.content_main_activity, nextFrag)
					.addToBackStack(null)
					.commit();
			//setContentView(R.layout.nutrition_layout);
		}

		if (drawer!=null) {
			drawer.closeDrawer(GravityCompat.START);
		}

		return true;

	}
}
