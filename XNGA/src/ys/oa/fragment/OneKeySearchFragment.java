package ys.oa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ys.nlga.activity.R;

public class OneKeySearchFragment extends Fragment {

	public static OneKeySearchFragment newInstance() {
		OneKeySearchFragment fragment = new OneKeySearchFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		 return inflater.inflate(R.layout.fragment_page, container, false);  
	}
	
	

}
