package com.sciencesquad.health.prescriptions;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sciencesquad.health.R;

import java.util.HashMap;

/**
 * Created by andrew on 4/28/16.
 */
public class BottomSheet extends BottomSheetDialog {

	LinearLayout mLinearLayout;

	private final int TEXT_SIZE = 20;

	HashMap<String, Integer> views;
	HashMap<String, Runnable> listeners;

	public BottomSheet(Context context) {
		super(context);

		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		views = new HashMap<>();
		listeners = new HashMap<>();
	}

	public View getViewByTag(String tag) {
		return mLinearLayout.findViewById(views.get(tag));
	}

	public BottomSheet addTextInput(String tag, String hint, String value) {
		int viewID = View.generateViewId();

		EditText editText = new EditText(getContext());
		editText.setId(viewID);
		editText.setHint(hint);
		editText.setText(value);
		mLinearLayout.addView(editText);

		views.put(tag, viewID);

		return this;

	}

	public BottomSheet addTextInput(String tag, String hint) {
		return addTextInput(tag, hint, "");
	}

	private void handleActionPress(String tag) {
		if (listeners.containsKey(tag)) {
			listeners.get(tag).run();
		}
	}

	public BottomSheet setOnAction(String tag, Runnable listener) {
		listeners.put(tag, listener);
		return this;
	}

	public BottomSheet addAction(String tag, String text, int icon) {
		int viewID = View.generateViewId();

		Context ctx = getContext();

		LinearLayout action = new LinearLayout(ctx);
		action.setOrientation(LinearLayout.HORIZONTAL);
		action.setPadding(20, 20, 20, 20);
		action.setId(viewID);

		TextView actionTextView = new TextView(getContext());
		actionTextView.setText(text);
		actionTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE);

		ImageView imageView = new ImageView(ctx);
		Resources r = ctx.getResources();

		if (icon != 0) {
			Drawable iconDrawable = r.getDrawable(icon, ctx.getTheme());
			imageView.setImageDrawable(iconDrawable);
			imageView.setColorFilter(actionTextView.getCurrentTextColor());
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}

		float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(lp);
		action.addView(imageView);
		action.addView(actionTextView);

		action.setOnClickListener((v) -> {
			handleActionPress(tag);
			dismiss();
		});

		mLinearLayout.addView(action);

		views.put(tag, viewID);

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(mLinearLayout);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}
}
