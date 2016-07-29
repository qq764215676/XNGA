package org.holoeverywhere.pickers;

import java.util.Calendar;

import org.holoeverywhere.widget.datetimepicker.time.RadialPickerLayout;
import org.holoeverywhere.widget.datetimepicker.time.TimePickerDialog;

import ys.oa.record.pages.OrderFragment1;
import ys.oa.record.pages.OrderFragment4;
import ys.oa.record.pages.OrderHouseFragment1;
import ys.oa.record.pages.OrderInquestFragment1;
import ys.oa.record.pages.OrderInquestFragment2;
import ys.oa.util.Constants;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;

public class PickersTimePickerFragment extends TimePickerDialog implements TimePickerDialog.OnTimeSetListener {

	private Fragment fragment;
	private int timeWhich;

	public PickersTimePickerFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public PickersTimePickerFragment(Fragment fragment, int timeWhich) {
		this.fragment = fragment;
		this.timeWhich = timeWhich;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		initialize(this, 23, 45, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		Constants.mDateAndTime += " " + hourOfDay + ":" + minute;
		// T.showShort(getSupportActivity(), Constants.mDateAndTime);
		if (fragment instanceof OrderFragment1) {
			((OrderFragment1) fragment).setAlarmTime();
		} else if (fragment instanceof OrderFragment4) {
			((OrderFragment4) fragment).setAlarmTime(timeWhich);
		} else if (fragment instanceof OrderHouseFragment1) {
			((OrderHouseFragment1) fragment).setInspectionTime();
		} else if (fragment instanceof OrderInquestFragment1) {
			((OrderInquestFragment1) fragment).setCaseTime();
		} else if (fragment instanceof OrderInquestFragment2) {
			((OrderInquestFragment2) fragment).setInquestTime(timeWhich);
		}
	}
}
