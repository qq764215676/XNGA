package ys.oa.record.pages;

/**
 * 整改意见
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

public class OrderHouseFragment3 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderHousePage3 mPage;
	private FloatingEditText mEtOpinion, mEtOwnerOpinion;
	private static OrderHouseFragment3 fragment;

	public static OrderHouseFragment3 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		fragment = new OrderHouseFragment3();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderHouseFragment3() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderHousePage3) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_house_page_3, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtOpinion = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_opinion));
		mEtOpinion.setText(mPage.getData().getString(OrderHousePage3.HOUSE_OPINION_1));

		mEtOwnerOpinion = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_owner_opinion));
		mEtOwnerOpinion.setText(mPage.getData().getString(OrderHousePage3.HOUSE_OPINION_2));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtOpinion.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHousePage3.HOUSE_OPINION_1,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtOwnerOpinion.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderHousePage3.HOUSE_OPINION_2,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (mEtOpinion != null) {
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
