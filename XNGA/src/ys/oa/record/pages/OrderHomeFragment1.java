package ys.oa.record.pages;

/**
 * 民警信息
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

public class OrderHomeFragment1 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderHomePage1 mPage;
	private FloatingEditText mEtRegTime, mEtRegPolicman;
	private static OrderHomeFragment1 fragment;

	public static OrderHomeFragment1 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderHomeFragment1();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderHomeFragment1() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderHomePage1) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home_page_1, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtRegTime = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_reg_time));
		mEtRegTime.setText(mPage.getData().getString(OrderHomePage1.REG_TIME));
		mEtRegTime.setLongClickable(false);

		mEtRegPolicman = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_reg_policeman));
		mEtRegPolicman.setText(mPage.getData().getString(OrderHomePage1.REG_POLICEMAN));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtRegTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment)).show(getFragmentManager());
			}
		});
		mEtRegTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage1.REG_TIME,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtRegPolicman.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage1.REG_POLICEMAN,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});
	}

	public void setRegTime() {
		mEtRegTime.setText(Constants.mDateAndTime);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtRegPolicman != null) {
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
