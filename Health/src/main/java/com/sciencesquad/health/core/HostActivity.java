package com.sciencesquad.health.core;

import android.app.AlertDialog;
import android.content.Context;

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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.overview.OverviewFragment;
import com.sciencesquad.health.prescriptions.UserModel;
import com.sciencesquad.health.run.RunFragment;
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
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new WorkoutFragment().open(transaction, R.id.drawer_layout).commit();
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
			// OH BABY DIS GUN B DURTI
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogLayout = inflater.inflate(R.layout.user_profile_dialog, null);
			builder.setView(dialogLayout);
			builder.setTitle("Edit User Info");
			EditText userNameField = (EditText) dialogLayout.findViewById(R.id.user_name_field);
			userNameField.setInputType(InputType.TYPE_CLASS_TEXT);
			EditText userAgeField = (EditText) dialogLayout.findViewById(R.id.user_age_field);
			userAgeField.setInputType(InputType.TYPE_CLASS_NUMBER);
			EditText userWeightField = (EditText) dialogLayout.findViewById(R.id.user_weight_field);
			userWeightField.setInputType(InputType.TYPE_CLASS_NUMBER);

			// Add Dialog buttons
			builder.setPositiveButton("Save", (dialog, whichButton) -> {
				UserModel user = new UserModel();
				user.setName(userNameField.getText().toString());
				user.setAge(Integer.parseInt(userAgeField.getText().toString()));
				user.setWeight(Float.parseFloat(userWeightField.getText().toString()));

				getSharedPreferences("USER_MODEL", Context.MODE_PRIVATE)
						.edit()
						.putString("USER_MODEL", user.toString())
						.apply();

			});
			builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
			});
			builder.create().show();
		} else if (id == R.id.nav_share) {
			// NOTHING HERE
		} else if (id == R.id.nav_send) {
			// NOTHING HERE
		}
		this.drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
