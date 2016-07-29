package ys.oa.record.pages;

/**
 * 勘察内容
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

public class OrderInquestFragment3 extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private OrderInquestPage3 mPage;

	public static OrderInquestFragment3 create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		OrderInquestFragment3 fragment = new OrderInquestFragment3();
		fragment.setArguments(args);
		return fragment;
	}

	public OrderInquestFragment3() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (OrderInquestPage3) mCallbacks.onGetPage(mKey);
	}

	private FloatingEditText mEtSceneLoss, mEtSceneDispose, mEtCaseProperty, mEtCaseUnit, mEtCaseMethod,
			mEtCaseFeature, mEtCaseTool, mEtOtherInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inquest_page_3, container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

		mEtSceneLoss = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_scene_loss));
		mEtSceneLoss.setText(mPage.getData().getString(OrderInquestPage3.SCENE_LOSS));

		mEtSceneDispose = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_scene_dispose));
		mEtSceneDispose.setText(mPage.getData().getString(OrderInquestPage3.SCENE_DISPOSE));

		mEtCaseProperty = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_case_property));
		mEtCaseProperty.setText(mPage.getData().getString(OrderInquestPage3.CASE_PROPERTY));

		mEtCaseUnit = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_case_unit));
		mEtCaseUnit.setText(mPage.getData().getString(OrderInquestPage3.CASE_UNIT));

		mEtCaseMethod = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_case_method));
		mEtCaseMethod.setText(mPage.getData().getString(OrderInquestPage3.CASE_METHOD));

		mEtCaseFeature = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_case_feature));
		mEtCaseFeature.setText(mPage.getData().getString(OrderInquestPage3.CASE_FEATURE));

		mEtCaseTool = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_case_tool));
		mEtCaseTool.setText(mPage.getData().getString(OrderInquestPage3.CASE_TOOL));

		mEtOtherInfo = (FloatingEditText) ((TextView) rootView.findViewById(R.id.et_other_info));
		mEtOtherInfo.setText(mPage.getData().getString(OrderInquestPage3.OTHER_INFO));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEtSceneLoss.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderInquestPage3.SCENE_LOSS, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtSceneDispose.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.SCENE_DISPOSE,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtCaseProperty.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.CASE_PROPERTY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtCaseUnit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.CASE_UNIT, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtCaseMethod.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.CASE_METHOD,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtCaseFeature.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.CASE_FEATURE,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtCaseTool.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(OrderInquestPage3.CASE_TOOL, (editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mEtOtherInfo.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData()
						.putString(OrderInquestPage3.OTHER_INFO, (editable != null) ? editable.toString() : null);
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
		if (mEtSceneLoss != null) {
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
