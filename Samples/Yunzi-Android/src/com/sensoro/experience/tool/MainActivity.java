package com.sensoro.experience.tool;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	RelativeLayout containLayout;
	BeaconsFragment beaconsFragment;
	DetailFragment detailFragment;
	DistanceFragment distanceFragment;
	RangeFragment rangeFragment;
	TemperatureFragment temperatureFragment;
	LightFragment lightFragment;
	MoveFragment moveFragment;
	NotificationFragment notificationFragment;

	ActionBar actionBar;
	LayoutInflater inflater;
	RelativeLayout actionBarMainLayout;
	RelativeLayout actionBarLayout;
	TTFIcon freshIcon;
	TTFIcon infoIcon;
	TextView actionBarTitle;

	NotificationManager notificationManager;
	public static final int NOTIFICATION_ID = 0;
	SharedPreferences sharedPreferences;

	FragmentManager fragmentManager;
	/*
	 * Beacon Manager lister,use it to listen the appearence, disappearence and
	 * updating of the beacons.
	 */
	BeaconManagerListener beaconManagerListener;
	MyApp app;
	/*
	 * Sensoro Manager
	 */
	SensoroManager sensoroManager;
	/*
	 * store beacons in onUpdateBeacon
	 */
	CopyOnWriteArrayList<Beacon> beacons;
	String beaconFilter;
	String matchFormat;
	Handler handler = new Handler();
	Runnable runnable;

	public static final String TAG_FRAG_BEACONS = "TAG_FRAG_BEACONS";
	public static final String TAG_FRAG_DETAIL = "TAG_FRAG_DETAIL";
	public static final String TAG_FRAG_DISTANCE = "TAG_FRAG_DISTANCE";
	public static final String TAG_FRAG_RANGE = "TAG_FRAG_RANGE";
	public static final String TAG_FRAG_LIGHT = "TAG_FRAG_LIGHT";
	public static final String TAG_FRAG_TEMPERATURE = "TAG_FRAG_TEMPERATURE";
	public static final String TAG_FRAG_MOVE = "TAG_FRAG_MOVE";
	public static final String TAG_FRAG_NOTIFICATION = "TAG_FRAG_NOTIFICATION";

	public static final String BEACON = "beacon";

	BluetoothManager bluetoothManager;
	BluetoothAdapter bluetoothAdapter;
	ArrayList<OnBeaconChangeListener> beaconListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCtrl();
		initActionBar();
		showFragment(0);
		initSensoroListener();
		initRunnable();
		initBroadcast();

	}

	@Override
	protected void onResume() {
		boolean isBTEnable = isBlueEnable();
		if (isBTEnable) {
			startSensoroService();
		}
		handler.post(runnable);
		super.onResume();
	}

	private void showFragment(int fragmentID) {
		beaconsFragment = new BeaconsFragment();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.activity_main_container, beaconsFragment, TAG_FRAG_BEACONS);
		transaction.commit();
		setTitle(R.string.yunzi);
	}

	private void initCtrl() {
		containLayout = (RelativeLayout) findViewById(R.id.activity_main_container);
		fragmentManager = getSupportFragmentManager();
		inflater = getLayoutInflater();
		app = (MyApp) getApplication();
		matchFormat = "%s-%04x-%04x";
		sensoroManager = app.sensoroManager;
		beacons = new CopyOnWriteArrayList<Beacon>();
		beaconListeners = new ArrayList<OnBeaconChangeListener>();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		sharedPreferences = getPreferences(Activity.MODE_PRIVATE);

	}

	private void initActionBar() {
		actionBar = getActionBar();

		if (actionBarMainLayout == null) {
			actionBarMainLayout = (RelativeLayout) inflater.inflate(R.layout.title_main, null);
			freshIcon = (TTFIcon) actionBarMainLayout.findViewById(R.id.actionbar_main_fresh);
			freshIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (fragmentManager.findFragmentByTag(MainActivity.TAG_FRAG_BEACONS) != null) {
						beacons.clear();
						updateGridView();
					}
				}
			});
			infoIcon = (TTFIcon) actionBarMainLayout.findViewById(R.id.actionbar_main_info);
		}

		if (actionBarLayout == null) {
			actionBarLayout = (RelativeLayout) inflater.inflate(R.layout.title_main, null);
			actionBarTitle = (TextView) findViewById(R.id.actionbar_title);
		}
	}

	private void initBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
					if (state == BluetoothAdapter.STATE_ON) {
						startSensoroService();
					}
				}
			}
		}, filter);
	}

	private boolean isBlueEnable() {
		bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		boolean status = bluetoothAdapter.isEnabled();
		if (!status) {
			Builder builder = new Builder(this);
			builder.setNegativeButton(R.string.yes, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(intent);
				}
			}).setPositiveButton(R.string.no, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setTitle(R.string.ask_bt_open);
			builder.show();
		}

		return status;
	}

	private void initRunnable() {
		runnable = new Runnable() {

			@Override
			public void run() {
				updateGridView();
				handler.postDelayed(this, 2000);
			}
		};
	}

	/*
	 * update the grid
	 */
	private void updateGridView() {
		if (beaconsFragment == null) {
			return;
		}
		if (!beaconsFragment.isVisible()) {
			return;
		}
		beaconsFragment.notifyFresh();
	}

	private void initSensoroListener() {
		beaconManagerListener = new BeaconManagerListener() {

			@Override
			public void onUpdateBeacon(final ArrayList<Beacon> arg0) {
				/*
				 * beacons has bean scaned in this scanning period.
				 */
				if (beaconsFragment == null) {
					beaconsFragment = (BeaconsFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_BEACONS);
				}
				if (beaconsFragment == null) {
					return;
				}
				/*
				 * beaconsFragment.isVisible()
				 */
				if (beaconsFragment.isVisible()) {
					/*
					 * Add the update beacons into the grid.
					 */
					for (Beacon beacon : arg0) {
						if (beacons.contains(beacon)) {
							continue;
						}
						/*
						 * filter
						 */

						if (TextUtils.isEmpty(beaconFilter)) {
							beacons.add(beacon);
						} else {
							String matckString = String.format(matchFormat, beacon.getSerialNumber(), beacon.getMajor(), beacon.getMinor());
							if (matckString.contains(beaconFilter)) {
						beacons.add(beacon);
					}
				}

					}
				}
				runOnUiThread(new Runnable() {
					public void run() {
						for (OnBeaconChangeListener listener : beaconListeners) {
							if (listener == null) {
								continue;
							}
							listener.onBeaconChange(arg0);
						}
					}
				});

			}

			@Override
			public void onNewBeacon(Beacon arg0) {
				/*
				 * A new beacon appears.
				 */
				String key = getKey(arg0);
				boolean state = sharedPreferences.getBoolean(key, false);
				if (state) {
					/*
					 * show notification
					 */

					showNotification(arg0, true);
				}

			}

			@Override
			public void onGoneBeacon(Beacon arg0) {
				/*
				 * A beacon disappears.
				 */
				String key = getKey(arg0);
				boolean state = sharedPreferences.getBoolean(key, false);
				if (state) {
					/*
					 * show notification
					 */

					showNotification(arg0, false);
				}
			}
		};
	}

	public String getKey(Beacon beacon) {
		if (beacon == null) {
			return null;
		}
		String key = beacon.getProximityUUID() + beacon.getMajor() + beacon.getMinor() + beacon.getSerialNumber();

		return key;

	}

	private void showNotification(final Beacon beacon, final boolean isIn) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Notification.Builder builder = new Notification.Builder(getApplicationContext());
				String context = null;
				if (isIn) {
					context = String.format("IN:%s", beacon.getSerialNumber());
				} else {
					context = String.format("OUT:%s", beacon.getSerialNumber());
				}
				builder.setTicker(context);
				builder.setContentText(context);
				builder.setWhen(System.currentTimeMillis());
				builder.setAutoCancel(true);
				builder.setContentTitle(getString(R.string.app_name));
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setDefaults(Notification.DEFAULT_SOUND);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pendingIntent);

				Notification notification = builder.build();

				notificationManager.notify(NOTIFICATION_ID, notification);
				// NotificationManager nm = (NotificationManager)
				// getSystemService(Context.NOTIFICATION_SERVICE);
				// Notification n = new Notification(R.drawable.ic_launcher,
				// "Hello,there!", System.currentTimeMillis());
				// n.flags = Notification.FLAG_AUTO_CANCEL;
				// // n.flags = Notification.FLAG_NO_CLEAR; // ������֪ͨ����ɾ��
				// Intent i = new Intent(getApplicationContext(),
				// MainActivity.class);
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				// Intent.FLAG_ACTIVITY_NEW_TASK);
				// // PendingIntent
				// PendingIntent contentIntent =
				// PendingIntent.getActivity(getApplicationContext(),
				// R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
				//
				// n.setLatestEventInfo(getApplicationContext(), "Hello,there!",
				// "Hello,there,I'm john.", null);
				// nm.notify(R.string.app_name, n);
				// nm.cancel(R.string.app_name); // ȡ��֪ͨ

			}
		});

	}

	/*
	 * Start sensoro service.
	 */
	private void startSensoroService() {
		// set a tBeaconManagerListener.
		sensoroManager.setBeaconManagerListener(beaconManagerListener);
		try {
			sensoroManager.startService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(runnable);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			
//		}
		// Fragment fragment =
		// fragmentManager.findFragmentByTag(TAG_FRAG_BEACONS);
//		if (fragment != null && fragment.isVisible()) {
//			// exit the app
//			System.exit(0);
		// return false;
		// }
//		// fragment = fragmentManager.findFragmentByTag(TAG_FRAG_DETAIL);
//		// if (fragment != null) {
//		// // back to beacons fragment
		// //
		// fragmentManager.beginTransaction().replace(R.id.activity_main_container,
//		// beaconsFragment).commit();
//		// return false;
//		// }
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * Beacon Change Listener.Use it to notificate updating of beacons.
	 */
	public interface OnBeaconChangeListener {
		public void onBeaconChange(ArrayList<Beacon> beacons);
	}

	/*
	 * Register beacon change listener.
	 */
	public void registerBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
		if (beaconListeners == null) {
			return;
		}
		beaconListeners.add(onBeaconChangeListener);
	}

	/*
	 * Unregister beacon change listener.
	 */
	public void unregisterBeaconChangerListener(OnBeaconChangeListener onBeaconChangeListener) {
		if (beaconListeners == null) {
			return;
		}
		beaconListeners.remove(onBeaconChangeListener);
	}
}
