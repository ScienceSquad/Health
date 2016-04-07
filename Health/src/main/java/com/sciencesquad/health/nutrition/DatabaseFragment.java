package com.sciencesquad.health.nutrition;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentDatabaseBinding;

/**
 * Created by andrew on 4/6/16.
 */
public class DatabaseFragment extends BaseFragment {
	public static final String TAG = DatabaseFragment.class.getSimpleName();

	@Override
	public Configuration getConfiguration() {
		return new Configuration(TAG, "Nutrition Databases",
				R.drawable.ic_menu_nutrition, R.style.AppTheme_Nutrition,
				R.layout.fragment_database);
	}

	// Our generated binding class is different...
	@Override @SuppressWarnings("unchecked")
	protected FragmentDatabaseBinding xml() {
		return super.xml();
	}

	@Override
	public void onSetupTransition() {
		this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
		this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
	}

	private void callRecipeQueryDialog(View view) {
		View recipeDialog = getInflater().inflate(R.layout.fragment_database_recipe_dialog, null);
		new MaterialStyledDialog(getActivity())
				.setCustomView(recipeDialog)
				.withDialogAnimation(true, Duration.FAST)
				.setCancelable(false)
				.setPositive(getResources().getString(R.string.accept),
						(dialog, which) -> {
							Log.d(TAG, "Accepted!");
							EditText searchQuery = (EditText) recipeDialog.findViewById(R.id.search_query);
							String queryString = searchQuery.getText().toString();
							Snackbar.make(view, "Query string: " + queryString, Snackbar.LENGTH_LONG)
									.setAction("Action", null).show();
						})
				.setNegative(getResources().getString(R.string.decline),
						(dialog, which) -> Log.d(TAG, "Declined!"))
				.show();
	}

	private void callNutrientQueryDialog(View view) {
		View nutrientDialog = getInflater().inflate(R.layout.fragment_database_nutrient_dialog, null);
		new MaterialStyledDialog(getActivity())
				.setCustomView(nutrientDialog)
				.withDialogAnimation(true, Duration.FAST)
				.setCancelable(false)
				.setPositive(getResources().getString(R.string.accept),
						(dialog, which) -> {
							Log.d(TAG, "Accepted!");
							EditText searchQuery = (EditText) nutrientDialog.findViewById(R.id.search_query);
							String queryString = searchQuery.getText().toString();
							Snackbar.make(view, "Query string: " + queryString, Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
						})
				.setNegative(getResources().getString(R.string.decline),
						(dialog, which) -> Log.d(TAG, "Declined!"))
				.show();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Drawable search = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_nutrition);
		search.setTint(Color.WHITE);

		// TODO get nav open working
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		// Configure the FAB.
		xml().fab.setImageDrawable(search);
		xml().fab.setOnClickListener(view2 -> {
			int currentTab = xml().pager.getCurrentItem();
			if (currentTab == 0) callRecipeQueryDialog(view2);
			else callNutrientQueryDialog(view2);
		});

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}

}
