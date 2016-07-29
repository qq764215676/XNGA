package ys.oa.record.pages;

/**
 * 接警信息
 */
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.pickers.PickersDatePickerFragment;

import ys.oa.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.material.widget.FloatingEditText;
import ys.nlga.activity.R;
import com.wizardpager.ui.PageFragmentCallbacks;

public class OrderFragment1 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderPage1 mPage;
	private FloatingEditText mEtAlarmTime, mEtAlarmPolicman, mEtAlarmUnit;
	private static OrderFragment1 fragment;

	public static OrderFragment1 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderFragment1();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderFragment1() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderPage1) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_1, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtAlarmTime = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_time));
		mEtAlarmTime.setText(mPage.getData().getString(OrderPage1.ALARM_TIME_DATA_KEY));
		mEtAlarmTime.setLongClickable(false);

		mEtAlarmPolicman = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_policeman));
		mEtAlarmPolicman.setText(mPage.getData().getString(OrderPage1.ALARM_POLICEMAN_DATA_KEY));

		mEtAlarmUnit = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_unit));
		mEtAlarmUnit.setText(mPage.getData().getString(OrderPage1.ALARM_UNIT_DATA_KEY));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtAlarmTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment)).show(getFragmentManager());
			}
		});
		mEtAlarmTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage1.ALARM_TIME_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmPolicman.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage1.ALARM_POLICEMAN_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmUnit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage1.ALARM_UNIT_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	public void setAlarmTime() {
		mEtAlarmTime.setText(Constants.mDateAndTime);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtAlarmPolicman != null) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!menuVisible) {
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks)) {
			throw new ClassCastException("Activity must implement PageFragmentCallbacks");
		}

		mCallbacks = (PageFragmentCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}
