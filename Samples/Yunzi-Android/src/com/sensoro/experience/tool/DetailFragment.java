package com.sensoro.experience.tool;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.Beacon.MovingState;
import com.sensoro.experience.tool.MainActivity.OnBeaconChangeListener;

import android.R.anim;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/*
 * Detail of the beacon.
 */
public class DetailFragment extends Fragment implements OnBeaconChangeListener, OnItemClickListener {

	private static final String TAG = DetailFragment.class.getSimpleName();
	Beacon beacon;

	ImageView imageView;
	TextView snTextView;
	TextView idTextView;
	TextView rssiTextView;
	TextView temperatureTextView;
	TextView lightTextView;
	TextView moveTextView;
	TextView moveCountTextView;
	TextView modelTextView;
	TextView firmwareTextView;
	TextView batteryTextView;

	GridView itemGridView;
	SimpleAdapter simpleAdapter;
	ArrayList<Map<String, Object>> items;
	MainActivity activity;

	public static final String IMG = "IMG";
	public static final String NAME = "NAME";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		return view;
	}

	private void initActionBar() {
		activity.actionBar.setHomeButtonEnabled(true);
		activity.actionBar.setDisplayHomeAsUpEnabled(true);
		activity.actionBar.setDisplayShowHomeEnabled(false);
		activity.actionBar.setDisplayUseLogoEnabled(false);
		activity.actionBar.setTitle(R.string.yunzi);

		activity.actionBar.setCustomView(null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		beacon = (Beacon) bundle.get(MainActivity.BEACON);

		initCtrl();
		initActionBar();
		initYunzi();
		initTTF();
		super.onActivityCreated(savedInstanceState);
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

	private void initYunzi() {
		if (beacon == null) {
			return;
		}
		String model = beacon.getHardwareModelName();
		if (model.equalsIgnoreCase(getString(R.string.a0))) {
			imageView.setImageResource(R.drawable.yunzi_a0);
		} else if (model.equalsIgnoreCase(getString(R.string.b0))) {
			imageView.setImageResource(R.drawable.yunzi_b0);
		}
		String id = String.format("ID:%04x-%04x", beacon.getMajor(), beacon.getMinor());
		idTextView.setText(id);
		snTextView.setText("SN:" + beacon.getSerialNumber());
	}

	private void updateView() {
		if (beacon == null) {
			return;
		}
		String tmpString = null;

		rssiTextView.setText(beacon.getRssi() + "");
		Integer temp = beacon.getTemperature();
		if (temp == null) {
			tmpString = getString(R.string.closed);
		} else {
			tmpString = temp + " " + getString(R.string.degree);
		}
		temperatureTextView.setText(tmpString);

		Double light = beacon.getLight();
		if (light == null) {
			tmpString = getString(R.string.closed);
		} else {
			tmpString = new DecimalFormat("#0.00").format(light) + " " + getString(R.string.lx);
		}
		lightTextView.setText(tmpString);

		MovingState state = beacon.getMovingState();
		if (state == MovingState.STILL) {
			tmpString = getString(R.string.still);
		} else if (state == MovingState.MOVING) {
			tmpString = getString(R.string.moving);
		} else if (state == MovingState.DISABLED) {
			tmpString = getString(R.string.closed);
		}
		moveTextView.setText(tmpString);

		if (state == MovingState.DISABLED) {
			tmpString = getString(R.string.closed);
		} else {
			tmpString = beacon.getAccelerometerCount() + "";
		}

		moveCountTextView.setText(tmpString);

		modelTextView.setText(beacon.getHardwareModelName());
		firmwareTextView.setText(beacon.getFirmwareVersion());
		batteryTextView.setText(beacon.getBatteryLevel() + getString(R.string.percent));
	}

	private void initTTF() {

	}

	private void initCtrl() {
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);

		imageView = (ImageView) activity.findViewById(R.id.fragment_detail_iv);
		idTextView = (TextView) activity.findViewById(R.id.fragment_detail_id);
		snTextView = (TextView) activity.findViewById(R.id.fragment_detail_sn);
		rssiTextView = (TextView) activity.findViewById(R.id.fragment_detail_rssi);
		temperatureTextView = (TextView) activity.findViewById(R.id.fragment_detail_temperature);
		lightTextView = (TextView) activity.findViewById(R.id.fragment_detail_light);
		moveTextView = (TextView) activity.findViewById(R.id.fragment_detail_move);
		moveCountTextView = (TextView) activity.findViewById(R.id.fragment_detail_move_count);
		modelTextView = (TextView) activity.findViewById(R.id.fragment_detail_model);
		firmwareTextView = (TextView) activity.findViewById(R.id.fragment_detail_firmware);
		batteryTextView = (TextView) activity.findViewById(R.id.fragment_detail_battery);

		itemGridView = (GridView) activity.findViewById(R.id.fragment_detail_item);
		itemGridView.setOnItemClickListener(this);
		initGridView();
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

	private void initGridView() {

		items = new ArrayList<Map<String, Object>>();

		String[] names = new String[] { getString(R.string.title_distance), getString(R.string.title_range), getString(R.string.title_temperature), getString(R.string.title_light), getString(R.string.title_move), getString(R.string.title_notification) };

		String[] icons = new String[] { getString(R.string.icon_fa_map_marker), getString(R.string.icon_fa_bullseye), getString(R.string.icon_fa_cloud), getString(R.string.icon_fa_lightbulb_o), getString(R.string.icon_fa_rocket), getString(R.string.icon_fa_rss) };
		HashMap<String, Object> map = null;
		int pos = 0;
		for (String icon : icons) {
			map = new HashMap<String, Object>();
			map.put(IMG, icon);
			map.put(NAME, names[pos]);
			items.add(map);
			pos++;
		}

		simpleAdapter = new SimpleAdapter(getActivity(), items, R.layout.fragment_detail_grid_item, new String[] { IMG, NAME }, new int[] { R.id.fragment_detail_grid_item_iv, R.id.fragment_detail_grid_item_tv_name });
		itemGridView.setAdapter(simpleAdapter);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			activity.distanceFragment = new DistanceFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.distanceFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.distanceFragment, MainActivity.TAG_FRAG_DISTANCE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 1) {
			activity.rangeFragment = new RangeFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.rangeFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.rangeFragment, MainActivity.TAG_FRAG_TEMPERATURE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 2) {
			activity.temperatureFragment = new TemperatureFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.temperatureFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.temperatureFragment, MainActivity.TAG_FRAG_TEMPERATURE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 3) {
			activity.lightFragment = new LightFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.lightFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.lightFragment, MainActivity.TAG_FRAG_LIGHT);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 4) {
			activity.moveFragment = new MoveFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.moveFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.moveFragment, MainActivity.TAG_FRAG_MOVE);
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (position == 5) {
			activity.notificationFragment = new NotificationFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(MainActivity.BEACON, beacon);
			activity.notificationFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.activity_main_container, activity.notificationFragment, MainActivity.TAG_FRAG_NOTIFICATION);
			transaction.addToBackStack(null);
			transaction.commit();
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
