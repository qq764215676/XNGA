package com.byaku.gallery;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable;
import com.diegocarloslima.byakugallery.lib.TouchImageView;
import com.polites.android.GestureImageView;
import ys.nlga.activity.R;

public class TouchImageActivity extends SwipeBackActivity {
	private String imgUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_touch_image);

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);

		imgUrl = getIntent().getStringExtra("imgUrl");

		try {
			final InputStream testIs = new FileInputStream(imgUrl);
			boolean isGesture = false;
			try {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(testIs, null, opts);
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				if (opts.outWidth <= dm.widthPixels || opts.outHeight <= dm.heightPixels) {
					isGesture = true;
				}
			} catch (OutOfMemoryError e) {
				isGesture = false;
			}

			final InputStream is = new FileInputStream(imgUrl);
			if (isGesture) {
				final GestureImageView imageGesture = (GestureImageView) findViewById(R.id.touch_item_image_small);
				imageGesture.setImageBitmap(BitmapFactory.decodeStream(is));
				imageGesture.setVisibility(View.VISIBLE);
			} else {
				final TouchImageView imageTouch = (TouchImageView) findViewById(R.id.touch_item_image);
				imageTouch.setVisibility(View.VISIBLE);
				final ProgressBar progress = (ProgressBar) findViewById(R.id.gallery_view_pager_sample_item_progress);
				TileBitmapDrawable.attachTileBitmapDrawable(imageTouch, is, null,
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
		} catch (FileNotFoundException e) {
			onBackPressed();
		}
	}
}
