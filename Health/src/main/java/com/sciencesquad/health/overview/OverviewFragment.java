package com.sciencesquad.health.overview;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.Visibility;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.TextView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Coefficient;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.Dispatcher;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentOverviewBinding;
import com.sciencesquad.health.nutrition.NutritionModule;
import com.sciencesquad.health.run.RunModule;
import com.sciencesquad.health.sleep.SleepModule;
import com.sciencesquad.health.steps.StepsModule;
import com.sciencesquad.health.workout.WorkoutModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class OverviewFragment extends BaseFragment implements OnChartValueSelectedListener,
		OnChartGestureListener, Coefficient {
    public static final String TAG = OverviewFragment.class.getSimpleName();

	/**
	 * Instances of modules, organized in alphabetical order
	 */
	private ArrayList<Module> modules;

	/**
	 * Overview coefficient, overview colors and module coefficients sorted in alphabetical order
	 *
	 * TODO: Keep in mind the Harris-Benedict equation for basal metabolic rate
	 * (check out other possibilities too)
	 */
	private double overviewCoefficient;
	private int[] overviewColors;
	private double[] moduleCoefficients; // I figured an array made sense here

	/**
	 * Floating Action Button animations
	 */
    private Boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
	private Animation rotation;

	/**
	 * Stuff for pie graph; module-related information always in alphabetical order
	 */
    private float[] yData = {5, 10, 15, 20, 25}; // Health Coefficients go here
    private String[] xData = {"Nutrition", "Run & Cycle", "Sleep", "Steps", "Workout"};
	private ArrayList<Integer> pieColors;
	private int[] moduleColors;
	private PieDataSet pds;
	private float currentAngle;

    private CalendarView calendarView;
    private TextView dateDisplay;

	/**
	 * @see BaseFragment
	 */
    @Override
    protected BaseFragment.Configuration getConfiguration() {
        return new BaseFragment.Configuration(
                TAG, "Overview", R.drawable.ic_menu_overview,
                R.style.AppTheme_Overview, R.layout.fragment_overview
        );
    }

	/**
	 * Our generated binding class is different...
	 * @see BaseFragment
	 */
    @Override @SuppressWarnings("unchecked")
    protected FragmentOverviewBinding xml() {
        return super.xml();
    }

	/**
	 * Calculates overview coefficient by averaging coefficients from other modules
	 * @return calculated overview coefficient
	 */
	@Override
	public double calculateCoefficient() {
		double sum = 0;
		for (int i = 0; i < moduleCoefficients.length; i++)
			sum += moduleCoefficients[i];
		double average = sum / 5;
		return average;
	}

	/**
	 * Retrieves overview coefficient
	 * @return overviewCoefficient
	 */
	@Override
	public double getCoefficient() {

		return this.overviewCoefficient;
	}

	/**
	 * Sets overview coefficient
	 * TODO: Implement!
	 * @see Coefficient
	 */
	@Override
	public void setCoefficient(double coefficient) {
		this.overviewCoefficient = coefficient;
	}

	/**
	 * Get module instances and add to modules ArrayList in alphabetical order
	 * (not sure if I should return ArrayList modules or not, TBD)
	 *
	 * Need to determine if I should return ArrayList modules or not
	 */
	private void getModuleInstances() {
		modules = new ArrayList<>();
		NutritionModule nutritionModule = Module.of(NutritionModule.class);
		modules.add(nutritionModule);
		RunModule runModule = Module.of(RunModule.class);
		modules.add(runModule);
		SleepModule sleepModule = Module.of(SleepModule.class);
		modules.add(sleepModule);
		StepsModule stepsModule = Module.of(StepsModule.class);
		modules.add(stepsModule);
		WorkoutModule workoutModule = Module.of(WorkoutModule.class);
		modules.add(workoutModule);
	}

	/**
	 * Obtain module coefficients and populate moduleCoefficients in alphabetical order
	 *
	 * Need to determine if I should return double[] moduleCoefficients or not
	 *
	 * TODO: Fix line 178; I made Module implement Coefficient, I do not want to do this
	 */
	private void getModuleCoefficients() {
		if (moduleCoefficients == null)
			moduleCoefficients = new double[5];
		Iterator<Module> iterator = modules.iterator();
		for (int i = 0; i < modules.size() || iterator.hasNext(); i++) {
			Module m = iterator.next(); //
			moduleCoefficients[i] = m.getCoefficient();
		}
	}

	/**
	 * Obtains colors from modules
	 */
	private void getModuleColors() {
		pieColors = new ArrayList<>();	// for use in PieChart; sorted alphabetically
		moduleColors = new int[5]; 		// for use elsewhere; sorted alphabetically
		Context theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Nutrition);
		int nutritionColor = BaseFragment.getThemeColors(theme)[2];
		pieColors.add(nutritionColor);
		moduleColors[0] = nutritionColor;

		theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Run);
		int runColor = BaseFragment.getThemeColors(theme)[2];
		pieColors.add(runColor);
		moduleColors[1] = runColor;

		theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Sleep);
		int sleepColor = BaseFragment.getThemeColors(theme)[2];
		pieColors.add(sleepColor);
		moduleColors[2] = sleepColor;

		theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Steps);
		int stepsColor = BaseFragment.getThemeColors(theme)[2];
		pieColors.add(stepsColor);
		moduleColors[3] = stepsColor;

		theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Workout);
		int workoutColor = BaseFragment.getThemeColors(theme)[2 /* colorAccent */];
		pieColors.add(workoutColor);
		moduleColors[4] = workoutColor;
	}

    /**
     * Populates the PieChart with data
     */
    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
        }

        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < xData.length; i++) {
            xVals.add(xData[i]);
        }

        // create pie data set
        this.pds = new PieDataSet(yVals1, "Module Coefficients");
        this.pds.setSliceSpace(0); // messing with this
        this.pds.setSelectionShift(7f);
        this.pds.setColors(pieColors);

        PieData data = new PieData(xVals, this.pds);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        xml().overviewChart.setData(data);
        xml().overviewChart.highlightValues(null);
		xml().overviewChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend legend = xml().overviewChart.getLegend();
		legend.setEnabled(false);
        xml().overviewChart.invalidate();
    }

	/**
	 * To provide a Circular Reveal animation.
	 * @see BaseFragment
	 */
    @Override
    public void onSetupTransition() {
        this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
        this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
    }

	/**
	 * @see BaseFragment
	 */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		xml().setFragment(this);

		// Get modules and their coefficients
		getModuleInstances();
		getModuleCoefficients();

		// Get overview colors
		Context theme = new ContextThemeWrapper(BaseApp.app(), R.style.AppTheme_Overview);
		overviewColors = BaseFragment.getThemeColors(theme);

		// Get drawables and manipulate
		Drawable plus = ContextCompat.getDrawable(getActivity(), R.drawable.ic_plus);
		plus.setTint(overviewColors[2]);

		// Grab colors on first start up
		if (pieColors == null || pieColors.isEmpty())
			getModuleColors();

		// Setup the Toolbar
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		// Create tabs
        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        // No description
        xml().overviewChart.setDescription("");

        // Enable hole & configure
        xml().overviewChart.setDrawHoleEnabled(true);
        xml().overviewChart.setHoleColor(Color.TRANSPARENT);
        xml().overviewChart.setHoleRadius(40);
        xml().overviewChart.setTransparentCircleRadius(45);
		setCoefficient(calculateCoefficient());
        xml().overviewChart.setCenterText(Double.toString(getCoefficient()));
        xml().overviewChart.setCenterTextSize(35);
		xml().overviewChart.setCenterTextColor(Color.DKGRAY);

        // Enable touch & rotation
        xml().overviewChart.setTouchEnabled(true);
        xml().overviewChart.setRotationAngle(0);
		//currentAngle = 0;
		xml().overviewChart.setOnChartValueSelectedListener(this);
		xml().overviewChart.setOnChartGestureListener(this);

		// Populate chart with data
        addData();

        // Bind calendar view
        //calendarView = xml().calendarView;
        dateDisplay = xml().dateDisplay;
        dateDisplay.setText("Date: ");

        /*calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                dateDisplay.setText("Date: " + i2 + " / " + i1 + " / " + i);

				BaseApp.app().display("Selected Date:\n" + "Day = " + i2 + "\n" +
                        "Month = " + i1 + "\n" + "Year = " + i, false);
            }
        });*/

		// Initialize animations for fabs
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.rotate_backward);
		rotation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotation);

        // FABulous!!!
		xml().overviewFab.setImageDrawable(plus);
		xml().overviewFab.setBackgroundTintList(ColorStateList.valueOf(overviewColors[0]));
		xml().overviewFab2.setImageDrawable(plus);
		xml().overviewFab2.hide();
		xml().overviewFab3.setImageDrawable(plus);
		xml().overviewFab3.hide();
		xml().overviewFab4.setImageDrawable(plus);
		xml().overviewFab4.hide();

		xml().overviewFab.setOnClickListener(v -> {
            animateFab();
        });

		xml().overviewFab2.setOnClickListener(v -> {
			xml().overviewFab2.startAnimation(rotation);
		});

		xml().overviewFab3.setOnClickListener(v -> {
			xml().overviewFab3.startAnimation(rotation);
		});

		xml().overviewFab4.setOnClickListener(v -> {
			xml().overviewFab4.startAnimation(rotation);
		});
    }

    /**
     * Animations for when the overviewFab is pressed
     */
    public void animateFab() {
        if (!isFabOpen) {
			xml().overviewFab.startAnimation(rotate_forward);
			xml().overviewFab2.startAnimation(fab_open);
			xml().overviewFab3.startAnimation(fab_open);
			xml().overviewFab4.startAnimation(fab_open);
			xml().overviewFab2.show();
			xml().overviewFab3.show();
			xml().overviewFab4.show();
			xml().overviewFab2.setClickable(true);
			xml().overviewFab3.setClickable(true);
			xml().overviewFab4.setClickable(true);
            isFabOpen = true;
        } else {
			xml().overviewFab.startAnimation(rotate_backward);
			xml().overviewFab2.startAnimation(fab_close);
			xml().overviewFab3.startAnimation(fab_close);
			xml().overviewFab4.startAnimation(fab_close);
			xml().overviewFab2.hide();
			xml().overviewFab3.hide();
			xml().overviewFab4.hide();
			xml().overviewFab2.setClickable(false);
			xml().overviewFab3.setClickable(false);
			xml().overviewFab4.setClickable(false);
            isFabOpen = false;
        }
    }

	/**
	 *
	 */
	/*@Override
	public void onResume() {
		super.onResume();
		double currCo = getCoefficient();
		double upCo = calculateCoefficient();
		if (Double.compare(currCo, upCo) == 0) {
			
		} else {
			setCoefficient(upCo);
		}
	}*/

	/**
	 * Called when a value has been selected inside the chart.
	 *
	 * @param e The selected Entry.
	 * @param dataSetIndex The index in the datasets array of the data object
	 * the Entrys DataSet is in.
	 * @param h the corresponding highlight object that contains information
	 * about the highlighted position
	 */
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

		if (e == null)
			return;

		int ll = 0;
		long delay = 1000L;
		float a = xml().overviewChart.getRotationAngle();
		float c = 0;
		String text = "Overview Health Share Text";
		Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_overview);
		Easing.EasingOption eo = Easing.EasingOption.EaseInOutCirc;
		TimeUnit tu = TimeUnit.MILLISECONDS;

		float rotateBy = (float) (yData[e.getXIndex()] / overviewCoefficient * 180);
		float rc0 = (float) (yData[0] / overviewCoefficient * 180);
		float rc1 = (float) (yData[1] / overviewCoefficient * 180);
		float rc2 = (float) (yData[2] / overviewCoefficient * 180);
		float rc3 = (float) (yData[3] / overviewCoefficient * 180);
		float rc4 = (float) (yData[4] / overviewCoefficient * 180);
		rc2 = rc0 + rc1 + rc2;
		rc3 = rc1 + rc2 + rc3;
		rc4 = rc0 + rc1 + rc3 + rc4;
		float r = 270 - rotateBy;

		switch (e.getXIndex()) {
			case 0:
				c = 0;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_nutrition);
				drawable.setTint(moduleColors[0]);
				ll = R.layout.fragment_overview_nutrition;
				text = "Nutrition Share";
				break;
			case 1:
				c = rc1;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_run);
				drawable.setTint(moduleColors[1]);
				ll = R.layout.fragment_overview_run;
				text = "Run Share";
				break;
			case 2:
				c = rc2;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_sleep);
				drawable.setTint(moduleColors[2]);
				ll = R.layout.fragment_overview_sleep;
				text = "Sleep Share";
				break;
			case 3:
				c = rc3;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_steps);
				drawable.setTint(moduleColors[3]);
				ll = R.layout.fragment_overview_steps;
				text = "Steps Share";
				break;
			case 4:
				c = rc4;
				drawable = ContextCompat.getDrawable(getActivity(),
						R.drawable.ic_fitness_center_24dp);
				drawable.setTint(moduleColors[4]);
				ll = R.layout.fragment_overview_workout;
				text = "Workout Share";
				break;
		}
		final Drawable myDrawable = drawable;
		final int l = ll;
		rotateChart(eo, a, r, c);
		final String _t = text;
		Dispatcher.UI.run(() -> {
			showDialog(myDrawable, l, _t);
		}, delay, tu);
	}

	/**
	 * Called when nothing has been selected or a deselect has been made.
	 */
	@Override
	public void onNothingSelected() {
		float fangle = xml().overviewChart.getRotationAngle();
		float tangle = 0;
		xml().overviewChart.spin(1000, fangle, tangle, Easing.EasingOption.EaseInOutCirc);
	}

	/**
	 * Callbacks when the chart is longpressed.
	 *
	 * @param me
	 */
	@Override
	public void onChartLongPressed(MotionEvent me) {
		Drawable oval = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_overview);
		oval.setTint(overviewColors[2]);
		int l = R.layout.fragment_overview_number;
		showDialog(oval, l, "Overview Health Share Text");
	}

	/**
	 * Callbacks when the chart is double-tapped.
	 *
	 * @param me
	 */
	@Override
	public void onChartDoubleTapped(MotionEvent me) {
		// Do nothing, for now
	}

	/**
	 * Callbacks when the chart is single-tapped.
	 *
	 * @param me
	 */
	@Override
	public void onChartSingleTapped(MotionEvent me) {
		// Do nothing, for now
	}

	/**
	 * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
	 *
	 * @param me
	 * @param lastPerformedGesture
	 */
	@Override
	public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
		// Do nothing
	}

	/**
	 * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
	 *
	 * @param me
	 * @param lastPerformedGesture
	 */
	@Override
	public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
		// Do nothing
	}

	/**
	 * Callbacks then a fling gesture is made on the chart.
	 *
	 * @param me1
	 * @param me2
	 * @param velocityX
	 * @param velocityY
	 */
	@Override
	public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
		// Do nothing
	}

	/**
	 * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
	 *
	 * @param me
	 * @param scaleX scalefactor on the x-axis
	 * @param scaleY scalefactor on the y-axis
	 */
	@Override
	public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
		// Do nothing
	}

	/**
	 * Callbacks when the chart is moved / translated via drag gesture.
	 *
	 * @param me
	 * @param dX translation distance on the x-axis
	 * @param dY translation distance on the y-axis
	 */
	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
		// Do nothing
	}

	/**
	 *
	 * @param eo
	 * @param rotation
	 * @param correction
	 */
	public void rotateChart(Easing.EasingOption eo, float fromAngle, float rotation, float correction) {
		float toAngle = rotation - correction;
		xml().overviewChart.spin(1000, fromAngle, toAngle, eo);
	}

	/**
	 * Displays relevant dialog as determined in onValuePressed() and onLongPressed() methods
	 *
	 * @param d
	 * @param layout
	 */
	public void showDialog(Drawable d, int layout, final String shareText) {

		new MaterialStyledDialog(getActivity())
				.setIcon(d)
				.setCustomView(getInflater().inflate(layout, null))
				.withDialogAnimation(true, Duration.FAST)
				.setCancelable(false)
				.setPositive(getResources().getString(R.string.accept),
						(dialog, which) -> Log.d(TAG, "Accepted!"))
				.setNegative(getResources().getString(R.string.decline),
						(dialog, which) -> Log.d(TAG, "Declined!"))
				.setNeutral("Share", (dialog, which) -> {
					Intent i = new Intent(Intent.ACTION_SEND);
					i.putExtra(Intent.EXTRA_TEXT, shareText);
					i.setType("text/plain");
					startActivity(Intent.createChooser(i, "Share"));
				})
				.show();
	}
}
