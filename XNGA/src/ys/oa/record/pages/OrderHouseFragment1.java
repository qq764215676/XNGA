package ys.oa.record.pages;

/**
 * 房屋信息
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
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.material.widget.FloatingEditText;
import ys.nlga.activity.R;
import com.wizardpager.ui.PageFragmentCallbacks;

public class OrderHouseFragment1 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderHousePage1 mPage;
	private FloatingEditText mEtCommunity, mEtHouseAddress, mEtHouseOwner, mEtHouseOwnerPhone, mEtHouseCustodian,
			mEtHouseCustodianPhone, mEtInspectionTime, mEtInspectionMan;
	private static OrderHouseFragment1 fragment;

	public static OrderHouseFragment1 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderHouseFragment1();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderHouseFragment1() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderHousePage1) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_house_page_1, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtCommunity = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_community));
		mEtCommunity.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_1));

		mEtHouseAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_address));
		mEtHouseAddress.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_2));

		mEtHouseOwner = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_owner));
		mEtHouseOwner.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_3));

		mEtHouseOwnerPhone = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_owner_phone));
		mEtHouseOwnerPhone.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_4));

		mEtHouseCustodian = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_custodian));
		mEtHouseCustodian.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_5));

		mEtHouseCustodianPhone = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_house_custodian_phone));
		mEtHouseCustodianPhone.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_6));

		mEtInspectionTime = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inspection_time));
		mEtInspectionTime.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_7));
		mEtInspectionTime.setLongClickable(false);

		mEtInspectionMan = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inspection_man));
		mEtInspectionMan.setText(mPage.getData().getString(OrderHousePage1.HOUSE_INFO_8));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtInspectionTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment)).show(getFragmentManager());
			}
		});
		mEtInspectionTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHousePage1.HOUSE_INFO_7,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtCommunity.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_1, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_2, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseOwner.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_3, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseOwnerPhone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_4, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseCustodian.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_5, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtHouseCustodianPhone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_6, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtInspectionMan.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderHousePage1.HOUSE_INFO_8, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	public void setInspectionTime() {
		mEtInspectionTime.setText(Constants.mDateAndTime);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (mEtCommunity != null) {
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
