package org.holoeverywhere.pickers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;

import ys.oa.record.pages.OrderHomeFragment1;
import ys.oa.record.pages.OrderHomeFragment3;
import ys.oa.util.Constants;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PickersDatePickerFragment extends DatePickerDialog implements DatePickerDialog.OnDateSetListener {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private DialogFragment dialogFragment;
	private Fragment fragment;
	private int dateWhich;

	public PickersDatePickerFragment(Fragment fragment) {
		this.fragment = fragment;
		dialogFragment = (DialogFragment) new PickersTimePickerFragment(fragment);
	}

	public PickersDatePickerFragment(Fragment fragment, int timeWhich) {
		this.fragment = fragment;
		dateWhich = timeWhich;
		dialogFragment = (DialogFragment) new PickersTimePickerFragment(fragment, timeWhich);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar cal = Calendar.getInstance();
		initialize(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		Constants.mDateAndTime = DATE_FORMAT.format(calendar.getTime());
		if (fragment instanceof OrderHomeFragment1) {
			((OrderHomeFragment1) fragment).setRegTime();
		} else if (fragment instanceof OrderHomeFragment3) {
			((OrderHomeFragment3) fragment).setHomeDate(dateWhich);
		} else {
			dialogFragment.show(getFragmentManager());
		}
	}
}
