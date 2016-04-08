package com.sciencesquad.health.core;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.prescriptions.UserModel;
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
		if (id == R.id.nav_run) {
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
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			new WorkoutFragment().open(transaction, R.id.drawer_layout).commit();
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
