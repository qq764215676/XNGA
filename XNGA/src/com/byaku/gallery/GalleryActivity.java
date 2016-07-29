package com.byaku.gallery;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.diegocarloslima.byakugallery.lib.GalleryViewPager;
import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable;
import com.diegocarloslima.byakugallery.lib.TouchImageView;
import ys.nlga.activity.R;

public class GalleryActivity extends SwipeBackActivity {

	/*
	 * String imgUrl = Util.saveImageView(mIvBanner, "" +
	 * System.currentTimeMillis()); String[] imgUrls = { imgUrl, imgUrl, imgUrl,
	 * imgUrl, imgUrl }; if (imgUrl != null) { startActivity(new
	 * Intent(NewsDetailActivity.this, GalleryActivity.class).putExtra("imgUrl",
	 * imgUrl)); startActivity(new Intent(NewsDetailActivity.this,
	 * GalleryActivity.class).putExtra("imgUrls", imgUrls)); }
	 */
	private String imgUrl;
	private String[] imgUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_activity);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);

		imgUrl = getIntent().getStringExtra("imgUrl");
		imgUrls = getIntent().getStringArrayExtra("imgUrls");
		// imgUrl = Util.getSDPath() + "/x.jpg";

		final GalleryViewPager gallery = (GalleryViewPager) findViewById(R.id.gallery_view_pager_sample_gallery);
		gallery.setAdapter(new GalleryAdapter());
		gallery.setOffscreenPageLimit(1);
	}

	// @Override
	// public void onBackPressed() {
	// scrollToFinishActivity();
	// }

	private final class GalleryAdapter extends FragmentStatePagerAdapter {

		private int[] images = { R.raw.android2 };

		GalleryAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public int getCount() {
			if (imgUrl != null) {
				return 1;
			} else if (imgUrls != null) {
				return imgUrls.length;
			} else {
				return this.images.length;
			}
		}

		@Override
		public Fragment getItem(int position) {
			if (imgUrl != null) {
				return GalleryFragment.getInstance(imgUrl);
			} else if (imgUrls != null) {
				return GalleryFragment.getInstance(imgUrls[position]);
			} else {
				return GalleryFragment.getInstance(this.images[position]);
			}
		}
	}

	public static final class GalleryFragment extends Fragment {

		public static GalleryFragment getInstance(int imageId) {
			final GalleryFragment instance = new GalleryFragment();
			final Bundle params = new Bundle();
			params.putInt("imageId", imageId);
			instance.setArguments(params);
			return instance;
		}

		public static GalleryFragment getInstance(String imgUrl) {
			final GalleryFragment instance = new GalleryFragment();
			final Bundle params = new Bundle();
			params.putString("imgUrl", imgUrl);
			instance.setArguments(params);
			return instance;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			final View v = inflater.inflate(R.layout.gallery_item, null);
			final TouchImageView image = (TouchImageView) v.findViewById(R.id.gallery_view_pager_sample_item_image);
			final ProgressBar progress = (ProgressBar) v.findViewById(R.id.gallery_view_pager_sample_item_progress);
			
			if (getArguments().getString("imgUrl") != null) {
				try {
					final String imgUrl = getArguments().getString("imgUrl");
					final InputStream fis = new FileInputStream(imgUrl);
					TileBitmapDrawable.attachTileBitmapDrawable(image, fis, null,
							new TileBitmapDrawable.OnInitializeListener() {

								@Override
								public void onStartInitialization() {
									progress.setVisibility(View.VISIBLE);
								}

								@Override
								public void onEndInitialization() {
									progress.setVisibility(View.GONE);
								}

								@Override
								public void onError(Exception ex) {

								}
							});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				final int imageId = getArguments().getInt("imageId");
				final InputStream is = getResources().openRawResource(imageId);
				TileBitmapDrawable.attachTileBitmapDrawable(image, is, null,
						new TileBitmapDrawable.OnInitializeListener() {

							@Override
							public void onStartInitialization() {
								progress.setVisibility(View.VISIBLE);
							}

							@Override
							public void onEndInitialization() {
								progress.setVisibility(View.GONE);
							}

							@Override
							public void onError(Exception ex) {

							}
						});
			}
			return v;
		}

	}
}
