package com.sciencesquad.health.nutrition;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentDatabaseBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andrew on 4/6/16.
 */
public class DatabaseFragment extends BaseFragment {
	public static final String TAG = DatabaseFragment.class.getSimpleName();

	public class ItemContent {
		public String title;
		public String content;
		public String url;
	}

	public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

		private ArrayList<ItemContent> mDataset;

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class ViewHolder extends RecyclerView.ViewHolder {
			// each data item is just a string in this case
			public LinearLayout mLinearLayout;
			public ViewHolder(LinearLayout v) {
				super(v);
				mLinearLayout = v;
			}

			public void setContent(ItemContent itemContent) {
				TextView title = (TextView) mLinearLayout.findViewById(R.id.db_item_title);
				TextView content = (TextView) mLinearLayout.findViewById(R.id.db_item_content);
				title.setText(itemContent.title);
				content.setText(itemContent.url + "\n" + itemContent.content);
			}
		}

		// Provide a suitable constructor (depends on the kind of dataset)
		public ListAdapter(ArrayList<ItemContent> myDataset) {
			mDataset = myDataset;
		}

		// Create new views (invoked by the layout manager)
		@Override
		public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
													   int viewType) {
			// create a new view
			LinearLayout v = (LinearLayout) getInflater()
					.inflate(R.layout.database_list_item, parent, false);
			// set the view's size, margins, paddings and layout parameters


			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			// - get element from your dataset at this position
			// - replace the contents of the view with that element
			holder.setContent(mDataset.get(position));

		}

		// Return the size of your dataset (invoked by the layout manager)
		@Override
		public int getItemCount() {
			return mDataset.size();
		}
	}

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

	private void getRecipeQueryResults(String queryString) throws JSONException {
		RecipeQuery query = new RecipeQuery();
		query.setName(queryString);
		JSONObject results = query.getJSONResults();
		if (results == null) return;
		JSONArray resultsArray = results.getJSONArray("results");
		if (resultsArray == null) return;
		ArrayList<ItemContent> contentArray = new ArrayList<ItemContent>();
		for (int i = 0; i < resultsArray.length(); i++) {
			JSONObject object = resultsArray.getJSONObject(i);
			ItemContent itemContent = new ItemContent();
			itemContent.title = object.getString("title");
			itemContent.url = object.getString("href");
			itemContent.content = object.getString("ingredients");
			contentArray.add(itemContent);
		}
		ListAdapter listAdapter = new ListAdapter(contentArray);
		xml().page1.setAdapter(listAdapter);
	}

	private void getNutrientQueryResults(String queryString) throws JSONException {
		NutrientQuery query = new NutrientQuery(NutrientQuery.QueryType.SEARCH);
		query.setSearchQuery(queryString);
		JSONObject results = query.getJSONResults();
		if (results == null) return;
		JSONArray resultsArray = results.getJSONObject("list").getJSONArray("item");
		if (resultsArray == null) return;
		ArrayList<ItemContent> contentArray = new ArrayList<ItemContent>();
		for (int i = 0; i < resultsArray.length(); i++) {
			JSONObject object = resultsArray.getJSONObject(i);
			ItemContent itemContent = new ItemContent();
			itemContent.title = object.getString("name");
			itemContent.url = object.getString("ndbno");
			itemContent.content = object.getString("name");
			contentArray.add(itemContent);
		}
		ListAdapter listAdapter = new ListAdapter(contentArray);
		xml().page2.setAdapter(listAdapter);

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
							try {
								getRecipeQueryResults(queryString);
							} catch (JSONException e) {
								e.printStackTrace();
							}
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
							try {
								getNutrientQueryResults(queryString);
							} catch (JSONException e) {
								e.printStackTrace();
							}
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

		xml().page1.setLayoutManager(new LinearLayoutManager(getActivity()));
		xml().page2.setLayoutManager(new LinearLayoutManager(getActivity()));

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}

}
