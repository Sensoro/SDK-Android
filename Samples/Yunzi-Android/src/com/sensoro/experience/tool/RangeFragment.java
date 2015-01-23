package com.sensoro.experience.tool;

import java.util.ArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.Beacon.Proximity;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/*
 * The range of the beacon.
 */
public class RangeFragment extends Fragment implements OnBeaconChangeListener {

	Beacon beacon;
	MainActivity activity;
	RelativeLayout immediateLayout;
	RelativeLayout nearLayout;
	RelativeLayout farLayout;
	TTFIcon userIcon;

	int[] immediatePostion;
	int[] nearPosition;
	int[] farPosition;
	int[] unknowPosition;
	TranslateAnimation animation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_range, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCtrl();
		setTitle();
		super.onActivityCreated(savedInstanceState);
	}

	private void setTitle() {
		activity.setTitle(R.string.back);
	}

	private void updateView(Beacon beacon) {
		if (beacon == null) {
			return;
		}
		changePos(beacon.getProximity());
	}

	private void changePos(Proximity proximity) {

		if (beacon == null) {
			return;
		}
		if (beacon.getProximity() == proximity) {
			return;
		}
		float fromX = userIcon.getX();
		float fromY = userIcon.getY();
		float toX = 0;
		float toY = 0;
		if (proximity == Proximity.PROXIMITY_IMMEDIATE) {
			toX = immediatePostion[0];
			toY = immediatePostion[1];
		} else if (proximity == Proximity.PROXIMITY_NEAR) {
			toX = nearPosition[0];
			toY = nearPosition[1];
		} else if (proximity == Proximity.PROXIMITY_FAR) {
			toX = farPosition[0];
			toY = farPosition[1];
		}
		ObjectAnimator animator = ObjectAnimator.ofFloat(userIcon, "translationY", fromY, toY);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		animator.setDuration(500);
		ObjectAnimator.setFrameDelay(200);

		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {
	}
		});
		animator.start();

	}

	private void initUserPos() {
		if (beacon == null) {
			userIcon.setX(unknowPosition[0]);
			userIcon.setY(unknowPosition[1]);
			// userIcon.setX(immediatePostion[0]);
			// userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_IMMEDIATE) {
			userIcon.setX(immediatePostion[0]);
			userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_NEAR) {
			userIcon.setX(nearPosition[0]);
			userIcon.setY(nearPosition[1]);
			// userIcon.setX(immediatePostion[0]);
			// userIcon.setY(immediatePostion[1]);
		} else if (beacon.getProximity() == Proximity.PROXIMITY_FAR) {
			userIcon.setX(farPosition[0]);
			userIcon.setY(farPosition[1]);
			// userIcon.setX(immediatePostion[0]);
			// userIcon.setY(immediatePostion[1]);
		}
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);

		immediateLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_immediate);
		nearLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_near);
		farLayout = (RelativeLayout) activity.findViewById(R.id.fragment_range_far);
		userIcon = (TTFIcon) activity.findViewById(R.id.fragment_range_iv);

		initCircle();
	}

	private void initCircle() {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int wid = metrics.widthPixels;
		int height = metrics.heightPixels;

		int length = (height > wid) ? wid : height;

		int radius = length / 8;
		int curLength = radius * 2;
		int curRadius = radius;
		LayoutParams params = new LayoutParams(curLength, curLength);
		immediateLayout.setLayoutParams(params);
		immediateLayout.setX(wid / 2 - curRadius);
		immediateLayout.setY(height / 2 - curRadius);

		curLength = 4 * radius;
		curRadius = 2 * radius;

		params = new LayoutParams(curLength, curLength);
		nearLayout.setLayoutParams(params);
		nearLayout.setX(wid / 2 - curRadius);
		nearLayout.setY(height / 2 - curRadius);

		curLength = 6 * radius;
		curRadius = 3 * radius;
		params = new LayoutParams(curLength, curLength);
		farLayout.setLayoutParams(params);
		farLayout.setX(wid / 2 - curRadius);
		farLayout.setY(height / 2 - curRadius);

		int userRaduis = radius / 5 * 2;
		params = new LayoutParams(userRaduis * 2, userRaduis * 2);
		userIcon.setLayoutParams(params);

		initPosition(wid, height);

		initUserPos();

		// immediateLayout.setVisibility(View.GONE);
		// nearLayout.setVisibility(View.GONE);
		// farLayout.setVisibility(View.GONE);

	}

	private void initPosition(int wid, int height) {

		int length = (height > wid) ? wid : height;

		int radius = length / 8;
		int userRaduis = radius / 5 * 2;

		immediatePostion = new int[2];
		immediatePostion[0] = wid / 2 - userRaduis;
		immediatePostion[1] = height / 2 - userRaduis;

		nearPosition = new int[2];
		nearPosition[0] = wid / 2 - userRaduis;
		nearPosition[1] = (int) (height / 2 - userRaduis + 1.5 * radius);

		farPosition = new int[2];
		farPosition[0] = wid / 2 - userRaduis;
		farPosition[1] = (int) (height / 2 - userRaduis + 2.5 * radius);

		unknowPosition = new int[2];
		unknowPosition[0] = wid / 2 - userRaduis;
		unknowPosition[1] = (int) (height / 2 - userRaduis + 4 * radius);
	}

	@Override
	public void onResume() {
		updateView(beacon);
		registerBeaconChangeListener();
		super.onResume();
	}

	@Override
	public void onStop() {
		unregisterBeaconChangeListener();
		super.onStop();
	}

	/*
	 * Register beacon change listener.
	 */
	private void registerBeaconChangeListener() {
		activity.registerBeaconChangerListener(this);
	}

	/*
	 * Register beacon change listener.
	 */
	private void unregisterBeaconChangeListener() {
		activity.unregisterBeaconChangerListener(this);
	}

	@Override
	public void onBeaconChange(ArrayList<Beacon> beacons) {
		for (Beacon beacon : beacons) {
			if (beacon.getSerialNumber() != null && beacon.getSerialNumber().equals(this.beacon.getSerialNumber())) {
				updateView(beacon);
				this.beacon = beacon;
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			activity.onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
