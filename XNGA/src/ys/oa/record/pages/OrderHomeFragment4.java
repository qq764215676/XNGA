package ys.oa.record.pages;

/**
 * 承租人信息
 */
import ys.oa.activity.RecordHomeActivity;
import android.app.ActionBar.LayoutParams;
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

public class OrderHomeFragment4 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderHomePage4 mPage;
	private FloatingEditText mEtTenantName, mEtTenantSex, mEtTenantId, mEtTenantHkAddress;

	private RecordHomeActivity mActivity;

	public static OrderHomeFragment4 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		OrderHomeFragment4 fragment = new OrderHomeFragment4();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderHomeFragment4() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = (RecordHomeActivity) getActivity();
		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderHomePage4) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home_page_4, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtTenantName = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_tenant_name));
		mEtTenantName.setText(mPage.getData().getString(OrderHomePage4.TENANT_NAME));

		mEtTenantSex = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_tenant_sex));
		mEtTenantSex.setText(mPage.getData().getString(OrderHomePage4.TENANT_SEX));

		mEtTenantId = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_tenant_id));
		mEtTenantId.setText(mPage.getData().getString(OrderHomePage4.TENANT_ID));

		mEtTenantHkAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_hk_address));
		mEtTenantHkAddress.setText(mPage.getData().getString(OrderHomePage4.TENANT_HK_ADDRESS));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtTenantName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage4.TENANT_NAME,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtTenantSex.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage4.TENANT_SEX,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtTenantId.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage4.TENANT_ID, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtTenantHkAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHomePage4.TENANT_HK_ADDRESS,
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
		if (mEtTenantHkAddress != null) {
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
