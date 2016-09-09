package ys.oa.fragment;

import java.io.IOException;

import ys.nlga.activity.R;
import ys.oa.activity.MainActivity;
import ys.oa.activity.PatrolRegActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

public class ScanCodeFragment extends Fragment implements Callback
{

	public static final String SCAN_RESULT_ACTION = "com.zxing.fragment.ACTION_SCAN_RESULT";

	private static ScanCodeFragment scanCodeFragment;
	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button cancelScanButton;
	private View view;
	private Context context;
	private MainActivity mainActivity;
	private LayoutInflater mInflater;

	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;

	private TextView tvTitle;
	private int type = -1;

	public boolean isNeedCapture()
	{
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture)
	{
		this.isNeedCapture = isNeedCapture;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getCropWidth()
	{
		return cropWidth;
	}

	public void setCropWidth(int cropWidth)
	{
		this.cropWidth = cropWidth;
	}

	public int getCropHeight()
	{
		return cropHeight;
	}

	public void setCropHeight(int cropHeight)
	{
		this.cropHeight = cropHeight;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getActivity();
		mInflater = LayoutInflater.from(context);
	}

	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		view = mInflater.inflate(R.layout.activity_qr_scan, null);
		CameraManager.init(getActivity());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(getActivity());
		mContainer = (RelativeLayout) view.findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) view.findViewById(R.id.capture_crop_layout);
		// tvTitle = (TextView) view.findViewById(R.id.tv_qr_scan_title);

		// tvTitle.setText("扫一扫");
		ImageView mQrLineView = (ImageView) view.findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		return view;
	}

	public static ScanCodeFragment getInstance()
	{
		if (scanCodeFragment == null)
		{
			scanCodeFragment = new ScanCodeFragment();
		}
		return scanCodeFragment;
	}

	boolean flag = true;

	protected void light()
	{
		if (flag == true)
		{
			flag = false;

			CameraManager.get().openLight();
		}
		else
		{
			flag = true;

			CameraManager.get().offLight();
		}

	}

	public void onResume()
	{
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(String result)
	{
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getActivity(), PatrolRegActivity.class);
		intent.putExtra("result", result);
		startActivity(intent);

		handler.sendEmptyMessage(R.id.restart_preview);
	}

	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// �����Ƿ���Ҫ��ͼ
			setNeedCapture(true);

		}
		catch (IOException ioe)
		{
			return;
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (handler == null)
		{
			handler = new CaptureActivityHandler(scanCodeFragment);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;

	}

	public CaptureActivityHandler getHandler()
	{
		return handler;
	}

	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate()
	{
		if (playBeep && mediaPlayer != null)
		{
			mediaPlayer.start();
		}
		if (vibrate)
		{
			Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener()
	{
		public void onCompletion(MediaPlayer mediaPlayer)
		{
			mediaPlayer.seekTo(0);
		}
	};

}