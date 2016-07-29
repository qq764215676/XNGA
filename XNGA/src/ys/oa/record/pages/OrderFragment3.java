package ys.oa.record.pages;

/**
 * 报警信息
 */
import ys.oa.activity.RecordActivity;
import android.app.ActionBar.LayoutParams;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.material.widget.FloatingEditText;
import ys.nlga.activity.R;
import com.wizardpager.ui.PageFragmentCallbacks;

public class OrderFragment3 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderPage3 mPage;
	private FloatingEditText mEtAlarmType, mEtAlarmHasMaterial, mEtAlarmAddress, mEtAlarmInfo;

	private RecordActivity mActivity;

	public static OrderFragment3 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		OrderFragment3 fragment = new OrderFragment3();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderFragment3() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = (RecordActivity) getActivity();
		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderPage3) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_3, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtAlarmType = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_type));
		mEtAlarmType.setText(mPage.getData().getString(OrderPage3.ALARM_TYPE_DATA_KEY));

		mEtAlarmHasMaterial = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_has_material));
		mEtAlarmHasMaterial.setText(mPage.getData().getString(OrderPage3.ALARM_HAS_MATERIAL_DATA_KEY));

		mEtAlarmAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_address));
		mEtAlarmAddress.setText(mPage.getData().getString(OrderPage3.ALARM_ADDRESS_DATA_KEY));

		mEtAlarmInfo = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_alarm_info));
		mEtAlarmInfo.setText(mPage.getData().getString(OrderPage3.ALARM_INFO_DATA_KEY));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtAlarmType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initDropDown(mEtAlarmType, getResources().getTextArray(R.array.alarmSort));
			}
		});
		mEtAlarmType.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage3.ALARM_TYPE_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmHasMaterial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initDropDown(mEtAlarmHasMaterial, getResources().getTextArray(R.array.materialSort));
			}
		});
		mEtAlarmHasMaterial.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage3.ALARM_HAS_MATERIAL_DATA_KEY,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage3.ALARM_ADDRESS_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtAlarmInfo.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderPage3.ALARM_INFO_DATA_KEY,
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
		if (mEtAlarmInfo != null) {
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

	private PopupWindow popupWindow;

	public void initDropDown(final EditText editText, final CharSequence[] items) {
		View v = mActivity.getLayoutInflater().inflate(R.layout.sort_list, null);
		popupWindow = new PopupWindow(v, editText.getWidth() + 25, LayoutParams.WRAP_CONTENT, true);
		ListView listSorts = (ListView) v.findViewById(R.id.listSorts);
		listSorts.setAdapter(new ArrayAdapter<CharSequence>(mActivity, R.layout.sort_item, R.id.tv_sort, items));
		listSorts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				editText.setText(((TextView) v.findViewById(R.id.tv_sort)).getText().toString());
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
		popupWindow.showAsDropDown(editText, -13, -47);
	}
}
