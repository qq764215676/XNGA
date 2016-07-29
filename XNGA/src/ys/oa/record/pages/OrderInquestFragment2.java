package ys.oa.record.pages;

/**
 * 勘验信息
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

public class OrderInquestFragment2 extends Fragment {
	private static final String ARG_KEY = "key";
	private static final int TIME_START = 0;
	private static final int TIME_END = 1;

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderInquestPage2 mPage;

	private static OrderInquestFragment2 fragment;

	public static OrderInquestFragment2 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderInquestFragment2();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderInquestFragment2() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderInquestPage2) mCallbacks.onGetPage(mKey);
	}

	private FloatingEditText mEtInquestTime, mEtInquestEndTime, mEtInquestAddress, mEtInquestLeader, mEtInquestMember;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inquest_page_2, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtInquestTime = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inquest_time));
		mEtInquestTime.setText(mPage.getData().getString(OrderInquestPage2.INQUEST_TIME));
		mEtInquestTime.setLongClickable(false);

		mEtInquestEndTime = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inquest_end_time));
		mEtInquestEndTime.setText(mPage.getData().getString(OrderInquestPage2.INQUEST_END_TIME));
		mEtInquestEndTime.setLongClickable(false);

		mEtInquestAddress = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inquest_address));
		mEtInquestAddress.setText(mPage.getData().getString(OrderInquestPage2.INQUEST_ADDRESS));

		mEtInquestLeader = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inquest_leader));
		mEtInquestLeader.setText(mPage.getData().getString(OrderInquestPage2.INQUEST_LEADER));

		mEtInquestMember = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_inquest_member));
		mEtInquestMember.setText(mPage.getData().getString(OrderInquestPage2.INQUEST_MEMBER));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtInquestTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderInquestFragment2.TIME_START))
						.show(getFragmentManager());
			}
		});
		mEtInquestTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage2.INQUEST_TIME,
						(editable != null) ? editable.toString() : getResources().getString(R.string.review_none));
				mPage.notifyDataChanged();
			}
		});

		mEtInquestEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 日期选择
				((DialogFragment) new PickersDatePickerFragment(fragment, OrderInquestFragment2.TIME_END))
						.show(getFragmentManager());
			}
		});
		mEtInquestEndTime.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage2.INQUEST_END_TIME,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtInquestAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage2.INQUEST_ADDRESS,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtInquestLeader.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage2.INQUEST_LEADER,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtInquestMember.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage2.INQUEST_MEMBER,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});
	}

	public void setInquestTime(int timeWhich) {
		switch (timeWhich) {
		case OrderInquestFragment2.TIME_START:
			mEtInquestTime.setText(Constants.mDateAndTime);
			break;
		case OrderInquestFragment2.TIME_END:
			mEtInquestEndTime.setText(Constants.mDateAndTime);
			break;
		}
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEtInquestTime != null) {
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
