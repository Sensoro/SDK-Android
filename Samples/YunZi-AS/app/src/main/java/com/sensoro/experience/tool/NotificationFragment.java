package com.sensoro.experience.tool;

import com.sensoro.beacon.kit.Beacon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/*
 * Control the notification of beacon.
 */
public class NotificationFragment extends Fragment implements OnCheckedChangeListener {

	SwitchButton switchButton;
	Beacon beacon;

	MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		return inflater.inflate(R.layout.fragment_notification, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCtrl();
		setTitle();
		super.onActivityCreated(savedInstanceState);
	}

	private void initCtrl() {
		beacon = (Beacon) getArguments().get(MainActivity.BEACON);
		activity = (MainActivity) getActivity();
		setHasOptionsMenu(true);

		switchButton = (SwitchButton) activity.findViewById(R.id.fragment_notification_sb);
	}

	private void setTitle() {
		activity.setTitle(R.string.back);
	}

	@Override
	public void onResume() {
		initState(beacon);
		super.onResume();
	}

	private void initState(Beacon beacon) {
		if (beacon == null) {
			switchButton.setChecked(false);
		}
		String key = activity.getKey(beacon);
		boolean state = activity.sharedPreferences.getBoolean(key, false);
		switchButton.setChecked(state);
		switchButton.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (beacon == null) {
			return;
		}
		String key = activity.getKey(beacon);
		if (isChecked) {
			boolean isExist = activity.sharedPreferences.contains(key);
			if (isExist) {
				return;
			}
			Editor editor = activity.sharedPreferences.edit();
			editor.putBoolean(key, true);
			editor.commit();
		} else {
			Editor editor = activity.sharedPreferences.edit();
			editor.remove(key);
			editor.commit();
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
