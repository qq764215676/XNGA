package ys.oa.record.pages;

/**
 * 报警人信息
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.material.widget.FloatingEditText;
import ys.nlga.activity.R;
import com.wizardpager.ui.PageFragmentCallbacks;

public class OrderFragment2 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderPage2 mPage;
	private FloatingEditText mEtAlarmManName, mEtAlarmManPhone, mEtAlarmManAddress;

	public static OrderFragment2 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		OrderFragment2 fragment = new OrderFragment2();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderFragment2() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderPage2) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_2, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtAlarmManName = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_man_name));
		mEtAlarmManName.setText(mPage.getData().getString(OrderPage2.ALARM_MAN_NAME_DATA_KEY));

		mEtAlarmManPhone = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_man_phone));
		mEtAlarmManPhone.setText(mPage.getData().getString(OrderPage2.ALARM_MAN_PHONE_DATA_KEY));

		mEtAlarmManAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_man_address));
		mEtAlarmManAddress.setText(mPage.getData().getString(OrderPage2.ALARM_MAN_ADDRESS_DATA_KEY));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtAlarmManName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage2.ALARM_MAN_NAME_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmManAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage2.ALARM_MAN_ADDRESS_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmManPhone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage2.ALARM_MAN_PHONE_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtAlarmManName != null) {
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
