package com.sciencesquad.health.core;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sciencesquad.health.R;
import com.sciencesquad.health.overview.OverviewFragment;
import com.sciencesquad.health.run.RunFragment;
import com.sciencesquad.health.core.alarm.AlarmFragment;
import com.sciencesquad.health.nutrition.NutritionFragment;
import com.sciencesquad.health.run.RunLandingFragment;
import com.sciencesquad.health.sleep.SleepFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.workout.WorkoutFragment;

public class HostActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
	private static final String TAG = HostActivity.class.getSimpleName();

	private DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
		if (id == R.id.nav_overview) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new OverviewFragment(), OverviewFragment.TAG)
					.addToBackStack(OverviewFragment.TAG)
					.commit();
		} else if (id == R.id.nav_run) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new RunLandingFragment(), RunLandingFragment.TAG)
					.addToBackStack(RunLandingFragment.TAG)
					.commit();
		} else if (id == R.id.nav_sleep) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new SleepFragment().open(transaction, R.id.drawer_layout).commit();
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