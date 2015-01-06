package com.sensoro.experience.tool;

import java.util.ArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * The temperature of the beacon.
 */
public class TemperatureFragment extends Fragment implements OnBeaconChangeListener {

	TextView valueTextView;
	Beacon beacon;
	RelativeLayout relativeLayout;
	ImageView redView;
	ImageView yellowView;
	ImageView greenView;

	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_temperature, container, false);
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
		Integer temperature = beacon.getTemperature();
		if (temperature == null) {
			/*
			 * The temperature sensor is closed.
			 */
			valueTextView.setText(R.string.disable);
			return;
		}
		String format = String.format("%d", temperature);

		double redAlpha = (temperature - 20) / 15.0;
		double yellowAlpha = (temperature - 10) / 15.0;
		double greenAlpha = (25 - temperature) / 15.0;

		if (temperature > 35) {
			redView.setImageAlpha((int) redAlpha);
		}
		if (temperature > 25) {
			yellowAlpha = 1;
		}
		if (temperature > 25) {
			greenAlpha = 0;
		}

		if (temperature < 20) {
			redAlpha = 0;
		}
		if (temperature < 10) {
			yellowAlpha = 0;
		}
		if (temperature < 0) {
			greenAlpha = 1;
		}
		redView.setImageAlpha((int) (redAlpha * 100));
		yellowView.setImageAlpha((int) (yellowAlpha * 100));
		greenView.setImageAlpha((int) (greenAlpha * 100));

		valueTextView.setText(format + getString(R.string.degree));
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);

		valueTextView = (TextView) activity.findViewById(R.id.fragment_temperature_value);
		relativeLayout = (RelativeLayout) activity.findViewById(R.id.fragment_temperature_rl);

		redView = (ImageView) activity.findViewById(R.id.fragment_temperature_iv_red);
		yellowView = (ImageView) activity.findViewById(R.id.fragment_temperature_iv_yellow);
		greenView = (ImageView) activity.findViewById(R.id.fragment_temperature_iv_green);
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
			if (beacon.getProximityUUID().equals(this.beacon.getProximityUUID()) && beacon.getMajor() == this.beacon.getMajor() && beacon.getMinor() == this.beacon.getMinor()) {
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
