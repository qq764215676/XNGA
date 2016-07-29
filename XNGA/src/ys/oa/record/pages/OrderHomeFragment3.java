package ys.oa.record.pages;

/**
 * 出租房信息
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

public class OrderHomeFragment3 extends Fragment {
	private static final String ARG_KEY = "key";
	private static final int DATE_BEGIN = 0;
	private static final int DATE_END = 1;

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderHomePage3 mPage;
	private FloatingEditText mEtHouseAddress, mEtBeginDate, mEtEndDate, mEtHouseUse, mEtHouseArea, mEtPeopleNum;

	private static OrderHomeFragment3 fragment;

	public static OrderHomeFragment3 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderHomeFragment3();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderHomeFragment3() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderHomePage3) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home_page_3, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtHouseAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_address));
		mEtHouseAddress.setText(mPage.getData().getString(OrderHomePage3.HOUSE_ADDRESS));

		mEtBeginDate = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_begin_date));
		mEtBeginDate.setText(mPage.getData().getString(OrderHomePage3.BEGIN_DATE));
		mEtBeginDate.setLongClickable(false);

		mEtEndDate = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_end_date));
		mEtEndDate.setText(mPage.getData().getString(OrderHomePage3.END_DATE));
		mEtEndDate.setLongClickable(false);

		mEtHouseUse = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_use));
		mEtHouseUse.setText(mPage.getData().getString(OrderHomePage3.HOUSE_USE));

		mEtHouseArea = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_area));
		mEtHouseArea.setText(mPage.getData().getString(OrderHomePage3.HOUSE_AREA));

		mEtPeopleNum = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_people_num));
		mEtPeopleNum.setText(mPage.getData().getString(OrderHomePage3.PEOPLE_NUM));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtHouseAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.HOUSE_ADDRESS,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtBeginDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderHomeFragment3.DATE_BEGIN))
						.show(getFragmentManager());
			}
		});
		mEtBeginDate.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.BEGIN_DATE,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderHomeFragment3.DATE_END))
						.show(getFragmentManager());
			}
		});
		mEtEndDate.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.END_DATE, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseUse.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.HOUSE_USE, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseArea.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.HOUSE_AREA, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtPeopleNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage3.PEOPLE_NUM, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	public void setHomeDate(int timeWhich) {
		switch (timeWhich) {
		case OrderHomeFragment3.DATE_BEGIN:
			mEtBeginDate.setText(Constants.mDateAndTime);
			break;
		case OrderHomeFragment3.DATE_END:
			mEtEndDate.setText(Constants.mDateAndTime);
			break;
		}
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtHouseAddress != null) {
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
