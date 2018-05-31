package com.sensoro.experience.tool;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.meg7.widget.CircleImageView;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.R.bool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * The light intensity of the beacon.
 */
public class LightFragment extends Fragment implements OnBeaconChangeListener {

	TextView valueTextView;
	CircleImageView circleImageView;
	Beacon beacon;

	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_light, container, false);
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
		Double light = beacon.getLight();
		String format = null;
		if (light == null) {
			/*
			 * The light sensor is closed.
			 */
			format = getString(R.string.disable);
			valueTextView.setText(format);
			return;
		}
		format = new DecimalFormat("#0.00").format(light);
		if (light < 10) {
			circleImageView.setImageResource(R.color.dark);
			valueTextView.setText(getString(R.string.dark) + " (" + format + ")");
		} else if (light >= 10 && light < 200) {
			circleImageView.setImageResource(R.color.normal);
			valueTextView.setText(getString(R.string.normal) + " (" + format + ")");
		} else if (light >= 200) {
			circleImageView.setImageResource(R.color.glare);
			valueTextView.setText(getString(R.string.glare) + " (" + format + ")");
		}

	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);

		circleImageView = (CircleImageView) activity.findViewById(R.id.fragment_light_civ);
		valueTextView = (TextView) activity.findViewById(R.id.fragment_light_value);
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
