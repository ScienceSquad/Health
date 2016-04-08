package com.sciencesquad.health.core;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.sciencesquad.health.R;
import com.sciencesquad.health.overview.OverviewFragment;
import com.sciencesquad.health.nutrition.DatabaseFragment;
import com.sciencesquad.health.prescriptions.PrescriptionFragment;
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
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		new OverviewFragment().open(transaction, R.id.drawer_layout).commit();
		this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		// Try to auto-launch a BaseFragment if provided in the AndroidManifest.
		// i.e. <meta-data android:name="fragment" android:value="CLASS" />
		try {
			ActivityInfo actInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
			String fragmentClass = actInfo.metaData.getString("fragment");
			if (fragmentClass == null) {
				ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
				fragmentClass = appInfo.metaData.getString("fragment");
			}

			Log.i(TAG, "Loading " + fragmentClass);
			BaseFragment fragment = (BaseFragment)Class.forName(fragmentClass).newInstance();
			fragment.open(getFragmentManager().beginTransaction(), R.id.drawer_layout).commit();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new OverviewFragment().open(transaction, R.id.drawer_layout).commit();
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
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new StepsFragment().open(transaction, R.id.drawer_layout).commit();
		} else if (id == R.id.nav_workout) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new WorkoutFragment(), WorkoutFragment.TAG)
					.addToBackStack(WorkoutFragment.TAG)
					.commit();
		} else if (id == R.id.nav_prescription) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new PrescriptionFragment().open(transaction, R.id.drawer_layout).commit();
		} else if (id == R.id.nav_nutrition) {
			this.getFragmentManager()
					.beginTransaction()
					.replace(R.id.content, new NutritionFragment(), NutritionFragment.TAG)
					.addToBackStack(NutritionFragment.TAG)
					.commit();
		} else if (id == R.id.nav_databases) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new DatabaseFragment().open(transaction, R.id.drawer_layout).commit();
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
