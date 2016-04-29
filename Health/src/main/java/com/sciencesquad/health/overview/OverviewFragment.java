package com.sciencesquad.health.overview;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.Visibility;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.TextView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.Dispatcher;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentOverviewBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OverviewFragment extends BaseFragment implements OnChartValueSelectedListener,
		OnChartGestureListener {
    public static final String TAG = OverviewFragment.class.getSimpleName();
	private double overviewCoefficient;
	private double nutritionCoefficient;
	private double runCoefficient;
	private double sleepCoefficient;
	private double stepsCoefficient;
	private double workoutCoefficient;

    private FloatingActionButton fab;
    private FloatingActionButton fab2; // dummy
    private FloatingActionButton fab3; // dummy
    private FloatingActionButton fab4; // dummy
    private Boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
	private Animation rotation;

    PieChart mPieChart;
    private float[] yData = {5, 10, 15, 20, 25};
    private String[] xData = {"Nutrition", "Run & Cycle", "Sleep", "Steps", "Workout"};
    private Integer[] pieColor = {Color.GREEN, Color.MAGENTA, Color.YELLOW,
            Color.RED, Color.BLUE};
	private float currentAngle;

    CalendarView calendarView;
    TextView dateDisplay;

    @Override
    protected BaseFragment.Configuration getConfiguration() {
        String overviewTag = OverviewModule.TAG; // instantiates the Module...
        return new BaseFragment.Configuration(
                TAG, "Overview", R.drawable.ic_menu_overview,
                R.style.AppTheme_Overview, R.layout.fragment_overview
        );
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentOverviewBinding xml() {
        return super.xml();
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
        PieDataSet pds = new PieDataSet(yVals1, "Module Coefficients");
        pds.setSliceSpace(3f);
        pds.setSelectionShift(7f);

        // taste the rainbow
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < pieColor.length; i++) {
            colors.add(pieColor[i]);
        }

        pds.setColors(colors);

        PieData data = new PieData(xVals, pds);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.DKGRAY);

        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        Legend legend = mPieChart.getLegend();
		legend.setEnabled(false);
        mPieChart.invalidate();
    }

    @Override
    public void onSetupTransition() {
        this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
        this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //xml().setModule(Module.of(OverviewModule.class));

		Drawable plus = ContextCompat.getDrawable(getActivity(), R.drawable.ic_plus);
		plus.setTint(Color.DKGRAY);

		// Setup the Toolbar
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		// Create tabs
        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        // This binds the pie chart
        mPieChart = xml().overviewChart;
        mPieChart.setDescription("Daily Overview"); 		// This is probably not needed
        mPieChart.setDescriptionColor(R.color.amber_50); 	// because it is not entirely visible

        // Enable hole & configure
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.TRANSPARENT);
        mPieChart.setHoleRadius(40);
        mPieChart.setTransparentCircleRadius(45);
		overviewCoefficient = 75;
        mPieChart.setCenterText(Double.toString(overviewCoefficient));
        mPieChart.setCenterTextSize(35);
		mPieChart.setCenterTextColor(Color.DKGRAY);

        // Enable touch & rotation
        mPieChart.setTouchEnabled(true);
        mPieChart.setRotationAngle(0);
		currentAngle = 0;
		mPieChart.setOnChartValueSelectedListener(this);
		mPieChart.setOnChartGestureListener(this);

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
        fab = xml().overviewFab;
		xml().overviewFab.setImageDrawable(plus);
        fab2 = xml().overviewFab2;
        fab2.hide();
        fab3 = xml().overviewFab3;
        fab3.hide();
        fab4 = xml().overviewFab4;
        fab4.hide();

        fab.setOnClickListener(v -> {
            animateFab();
        });

		fab2.setOnClickListener(v -> {
			fab2.startAnimation(rotation);
		});

		fab3.setOnClickListener(v -> {
			fab3.startAnimation(rotation);
		});

		fab4.setOnClickListener(v -> {
			fab4.startAnimation(rotation);
		});

    }

    /**
     * Animations for when the overviewFab is pressed
     */
    public void animateFab() {
        if (!isFabOpen) {
            fab.startAnimation(rotate_forward);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab2.show();
            fab3.show();
            fab4.show();
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        } else {
            fab.startAnimation(rotate_backward);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab2.hide();
            fab3.hide();
            fab4.hide();
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
        }
    }

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
		float a = mPieChart.getRotationAngle();
		float c = 0;
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
				drawable.setTint(Color.GREEN);
				ll = R.layout.fragment_overview_nutrition;
				break;
			case 1:
				c = rc1;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_run);
				drawable.setTint(Color.MAGENTA);
				ll = R.layout.fragment_overview_run;
				break;
			case 2:
				c = rc2;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_sleep);
				drawable.setTint(Color.YELLOW);
				ll = R.layout.fragment_overview_sleep;
				break;
			case 3:
				c = rc3;
				drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_steps);
				drawable.setTint(Color.RED);
				ll = R.layout.fragment_overview_steps;
				break;
			case 4:
				c = rc4;
				drawable = ContextCompat.getDrawable(getActivity(),
						R.drawable.ic_fitness_center_24dp);
				drawable.setTint(Color.BLUE);
				ll = R.layout.fragment_overview_workout;
				break;
		}
		final Drawable myDrawable = drawable;
		final int l = ll;
		rotateChart(eo, a, r, c);
		Dispatcher.UI.run(() -> {
			showDialog(myDrawable, l);
		}, delay, tu);
	}

	/**
	 * Called when nothing has been selected or a deselect has been made.
	 */
	@Override
	public void onNothingSelected() {
		float fangle = mPieChart.getRotationAngle();
		float tangle = 0;
		mPieChart.spin(1000, fangle, tangle, Easing.EasingOption.EaseInOutCirc);
	}

	/**
	 * Callbacks when the chart is longpressed.
	 *
	 * @param me
	 */
	@Override
	public void onChartLongPressed(MotionEvent me) {
		Drawable oval = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_overview);
		oval.setTint(Color.WHITE);
		int l = R.layout.fragment_overview_number;
		showDialog(oval, l);
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
		mPieChart.spin(1000, fromAngle, toAngle, eo);
	}

	/**
	 * Displays relevant dialog as determined in onValuePressed() and onLongPressed() methods
	 *
	 * @param d
	 * @param layout
	 */
	public void showDialog(Drawable d, int layout) {

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
					i.putExtra(Intent.EXTRA_TEXT, "Your progress in Health365 is 0.75!");
					i.setType("text/plain");
					startActivity(Intent.createChooser(i, "Share"));
				})
				.show();
	}
}
