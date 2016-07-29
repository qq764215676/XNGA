package ys.oa.record.pages;

/**
 * 出警信息
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

public class OrderFragment4 extends Fragment {
	private static final String ARG_KEY = "key";
	private static final int TIME_DEPARTURE = 0;
	private static final int TIME_REACH = 1;

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderPage4 mPage;
	private FloatingEditText mEtAlarmHandler, mEtAlarmTimeDeparture, mEtAlarmTimeReach, mEtAlarmHandleInfo;

	private static OrderFragment4 fragment;

	public static OrderFragment4 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderFragment4();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderFragment4() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderPage4) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_4, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtAlarmHandler = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_handler));
		mEtAlarmHandler.setText(mPage.getData().getString(OrderPage4.ALARM_HANDLER_DATA_KEY));

		mEtAlarmTimeDeparture = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_time_departure));
		mEtAlarmTimeDeparture.setText(mPage.getData().getString(OrderPage4.ALARM_TIME_DEPARTURE_DATA_KEY));
		mEtAlarmTimeDeparture.setLongClickable(false);

		mEtAlarmTimeReach = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_time_reach));
		mEtAlarmTimeReach.setText(mPage.getData().getString(OrderPage4.ALARM_TIME_REACH_DATA_KEY));
		mEtAlarmTimeReach.setLongClickable(false);

		mEtAlarmHandleInfo = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_handle_info));
		mEtAlarmHandleInfo.setText(mPage.getData().getString(OrderPage4.ALARM_HANDLE_INFO_DATA_KEY));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtAlarmHandler.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage4.ALARM_HANDLER_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmTimeDeparture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderFragment4.TIME_DEPARTURE))
						.show(getFragmentManager());
			}
		});
		mEtAlarmTimeDeparture.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage4.ALARM_TIME_DEPARTURE_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmTimeReach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderFragment4.TIME_REACH))
						.show(getFragmentManager());
			}
		});
		mEtAlarmTimeReach.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage4.ALARM_TIME_REACH_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmHandleInfo.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage4.ALARM_HANDLE_INFO_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	public void setAlarmTime(int timeWhich) {
		switch (timeWhich) {
		case OrderFragment4.TIME_DEPARTURE:
			mEtAlarmTimeDeparture.setText(Constants.mDateAndTime);
			break;
		case OrderFragment4.TIME_REACH:
			mEtAlarmTimeReach.setText(Constants.mDateAndTime);
			break;
		}
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtAlarmHandler != null) {
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
