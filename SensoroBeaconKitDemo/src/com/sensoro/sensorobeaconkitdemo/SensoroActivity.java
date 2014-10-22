package com.sensoro.sensorobeaconkitdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.beacon.kit.BatterySaveInBackground;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.SensoroBeaconConnection;
import com.sensoro.beacon.kit.SensoroBeaconManager;

import java.util.ArrayList;

public class SensoroActivity extends Activity implements SensoroBeaconManager.BeaconManagerListener, SensoroBeaconConnection.BeaconConnectionCallback {
	private static final String TAG = SensoroActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BT = 1000;
	private Context context;
	private ListView listView;
	private SensoroAdapter sensoroAdapter;
	private ArrayList<Beacon> beaconArrayList;
	private BatterySaveInBackground batterySaveInBackground;
	private SensoroBeaconManager sensoroBeaconManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensoro);

		init();
	}

	@Override
	protected void onDestroy() {
		if (sensoroBeaconManager != null) {
			/**
			 * Stop SBK service.
			 */
			sensoroBeaconManager.stopService();
		}
		super.onDestroy();
	}

	private void init() {
		initView();
		checkBle();
	}

	private void checkBle() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		} else {
			BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
			if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				initSensoroSDK();
			}
		}
	}

	private void initView() {
		context = this;
		beaconArrayList = new ArrayList<Beacon>();
		sensoroAdapter = new SensoroAdapter(this);
		listView = (ListView) findViewById(R.id.lv_beacons);
		listView.setAdapter(sensoroAdapter);
	}

	private void initSensoroSDK() {
		/**
		 * If BatterySaveInBackground has been initialised, it will open the
		 * background saving power mode. If the app is switched to the
		 * background, SBK will use the background scanning period and the
		 * scanning interval .Otherwise,it us will use the foreground scanning
		 * period and the scanning interval.
		 */
		batterySaveInBackground = new BatterySaveInBackground(getApplication());
		/**
		 * Get a single instance of SensoroBeaconManager.
		 */
		sensoroBeaconManager = SensoroBeaconManager.getInstance(context);
		/**
		 * Set a listener of SensoroBeaconManager.BeaconManagerListener.
		 */
		sensoroBeaconManager.setBeaconManagerListener(this);
		/**
		 * Set the foreground scanning period. The default value is 1100 ms.
		 */
		sensoroBeaconManager.setForegroundScanPeriod(1000);
		/**
		 * Set the foreground scanning interval. The default value is 0 ms.
		 */
		sensoroBeaconManager.setForegroundBetweenScanPeriod(10);
		/**
		 * Set the background scanning period. The default value is 10000 ms.
		 */
		sensoroBeaconManager.setBackgroundScanPeriod(3000);
		/**
		 * Set the foreground scanning interval. The default value is 300000 ms.
		 */
		sensoroBeaconManager.setBackgroundBetweenScanPeriod(5000);
		/**
		 * Set the exist time of beacon. The default value is 8000 ms.
		 */
		sensoroBeaconManager.setBeaconExistTime(5000);
		/**
		 * Set the beacon updating period. The default value is 1000 ms.
		 */
		sensoroBeaconManager.setUpdateBeaconPeriod(1000);
		/**
		 * Start SBK service.
		 */
		try {
			sensoroBeaconManager.startService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * Stop SBK service.
		 */
		// sensoroBeaconManager.stopService();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				initSensoroSDK();
			} else if (resultCode == RESULT_CANCELED) {
				finish();
			}
		}
	}

	/**
	 * This callback method is called when a beacon appeares.
	 *
	 * @param beacon
	 */
	@Override
	public void onNewBeacon(final Beacon beacon) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!beaconArrayList.contains(beacon)) {
					beaconArrayList.add(beacon);
					sensoroAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 * This callback method is called when a beacon disappears.
	 *
	 * @param beacon
	 */
	@Override
	public void onGoneBeacon(final Beacon beacon) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Beacon deleteBeacon = null;
				for (Beacon inBeacon : beaconArrayList) {
					if (inBeacon.equals(beacon)) {
						deleteBeacon = beacon;
					}
				}
				if (deleteBeacon != null) {
					beaconArrayList.remove(deleteBeacon);
					sensoroAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	/**
	 * This callback method is called regularly.
	 *
	 * @param beacons
	 */
	@Override
	public void onUpdateBeacon(final ArrayList<Beacon> beacons) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (beaconArrayList != null && beaconArrayList.size() != 0) {
					for (Beacon beacon : beacons) {
						if (beaconArrayList.contains(beacon)) {
							beaconArrayList.set(beaconArrayList.indexOf(beacon), beacon);
						}
					}
					sensoroAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	public void onConnectedState(Beacon beacon, int newState, int status) {

	}

	@Override
	public void onSetNewPassword(Beacon beacon, int status) {

	}

	@Override
	public void onDisabledPassword(Beacon beacon, int status) {

	}

	@Override
	public void onCheckPassword(Beacon beacon, int status) {

	}

	@Override
	public void onSetBaseSetting(Beacon beacon, int status) {

	}

	@Override
	public void onSetSensorSetting(Beacon beacon, int status) {

	}

	@Override
	public void onSetMajoMinor(Beacon beacon, int status) {

	}

	@Override
	public void onSetProximityUUID(Beacon beacon, int status) {

	}

	@Override
	public void onResetToFactory(Beacon beacon, int status) {

	}

	@Override
	public void onResetAcceleratorCount(Beacon beacon, int status) {

	}

	@Override
	public void onUpdateSensorData(Beacon beacon, int status) {

	}

	@Override
	public void onTemperatureDataUpdate(Beacon beacon, int temperature) {

	}

	@Override
	public void onBrightnessLuxDataUpdate(Beacon beacon, double lux) {

	}

	@Override
	public void onAcceleratorMovingUpdate(Beacon beacon, int isMovingState) {

	}

	@Override
	public void onAcceleratorCountUpdate(Beacon beacon, int count) {

	}

	class SensoroAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater = null;
		private ViewHolder holder = null;

		SensoroAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return beaconArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return beaconArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.item_beacons, parent, false);
				holder = new ViewHolder();
				holder.uuidTextView = (TextView) convertView.findViewById(R.id.item_tv_uuid);
				holder.majorTextView = (TextView) convertView.findViewById(R.id.item_tv_major);
				holder.minorTextView = (TextView) convertView.findViewById(R.id.item_tv_minor);
				holder.rssiTextView = (TextView) convertView.findViewById(R.id.item_tv_rssi);
				holder.modleTextView = (TextView) convertView.findViewById(R.id.item_tv_modle);
				holder.stateImageView = (ImageView) convertView.findViewById(R.id.item_iv_state);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.uuidTextView.setText(beaconArrayList.get(position).getProximityUuid());
			holder.majorTextView.setText(String.format("%04X", beaconArrayList.get(position).getMajor()));
			holder.minorTextView.setText(String.format("%04X", beaconArrayList.get(position).getMinor()));
			holder.modleTextView.setText(beaconArrayList.get(position).getHardwareModelName());
			int rssi = beaconArrayList.get(position).getRssi();
			holder.rssiTextView.setText(String.valueOf(rssi));
			if (rssi < -80) {
				holder.stateImageView.setBackgroundResource(R.drawable.ic_yellow);
			} else {
				holder.stateImageView.setBackgroundResource(R.drawable.ic_green);
			}
			return convertView;
		}

		class ViewHolder {
			private TextView uuidTextView = null;
			private TextView majorTextView = null;
			private TextView minorTextView = null;
			private TextView rssiTextView = null;
			private TextView modleTextView = null;
			private ImageView stateImageView = null;
		}
	}
}
