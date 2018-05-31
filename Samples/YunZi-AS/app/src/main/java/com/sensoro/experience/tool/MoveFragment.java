package com.sensoro.experience.tool;

import java.util.ArrayList;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.Beacon.MovingState;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

/*
 * The moving state of the beacon.
 */
public class MoveFragment extends Fragment implements OnBeaconChangeListener {

	TextView valueTextView;
	TextView addTextView;
	int moveNum;
	ImageView earthView;
	Beacon beacon;
	AnimationDrawable animationDrawable;
	Animation addAnimation;
	MainActivity activity;
	float earthRoatY;
	View oneself;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		oneself = inflater.inflate(R.layout.fragment_move, container, false);
		return oneself;
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

	private void updateView() {
		if (beacon == null) {
			return;
		}
		if (beacon.getMovingState() == MovingState.DISABLED) {
			/*
			 * Accelerometer is closed.
			 */
			valueTextView.setText(getString(R.string.disable));
			return;
		}
		if (beacon.getMovingState() == MovingState.STILL) {
			animationDrawable.stop();
			valueTextView.setText(moveNum + "");
			if (beacon.getAccelerometerCount() > moveNum) {
				moveNum = beacon.getAccelerometerCount();
				creatAnim();
				addTextView.startAnimation(addAnimation);
				return;
			}
		} else if (beacon.getMovingState() == MovingState.MOVING) {
			/*
			 * The beacon is moving
			 */
			animationDrawable.start();
		}

	}

	private void creatAnim() {
		addAnimation = new TranslateAnimation(0, 0, 0, -40);
		addAnimation.setDuration(700);
		addAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				addTextView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				addTextView.setVisibility(View.GONE);
			}
		});
	}

	private void initCtrl() {
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);
		valueTextView = (TextView) oneself.findViewById(R.id.fragment_move_value);
		addTextView = (TextView) oneself.findViewById(R.id.fragment_move_add);
		earthView = (ImageView) oneself.findViewById(R.id.fragment_move_earth);
		animationDrawable = (AnimationDrawable) earthView.getBackground();
		
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		MovingState state = beacon.getMovingState();
		if (state == MovingState.DISABLED) {
			/*
			 * accelerometer is closed.
			 */
			valueTextView.setText(getString(R.string.disable));
		} else {
			moveNum = beacon.getAccelerometerCount();
		valueTextView.setText(moveNum + "");
	}

	}

	@Override
	public void onResume() {
		updateView();
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
				this.beacon = beacon;
				updateView();
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
