package eu.livotov.zxscan;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import eu.livotov.labs.android.camview.CAMView;
import eu.livotov.zxscan.decoder.BarcodeDecoder;
import eu.livotov.zxscan.decoder.zxing.ZXDecoder;
import eu.livotov.zxscan.util.SoundPlayer;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 03/11/2014
 */
public class ScannerView extends FrameLayout implements CAMView.CAMViewListener {
	private static final String TAG = ScannerView.class.getSimpleName();

	public CAMView camera;
	protected ImageView hud;
	protected ScannerViewEventListener scannerViewEventListener;
	protected BarcodeDecoder decoder;
	protected int scannerSoundAudioResource = R.raw.beep;
	protected boolean playSound = true;
	protected SoundPlayer soundPlayer;

	// decode barcode image time interval
	private long decodeTimeInterval = 3000;

	private long lastDecodeTime = SystemClock.elapsedRealtime();

	public ScannerView(final Context context) {
		super(context);
		initUI();
	}

	protected void initUI() {
		final View root = LayoutInflater.from(getContext()).inflate(getScannerLayoutResource(), this);
		camera = (CAMView) root.findViewById(R.id.zxscanlib_camera);
		hud = (ImageView) root.findViewById(R.id.cameraHud);
		camera.setCamViewListener(this);
		camera.setFocusable(true);
		decoder = new ZXDecoder();
		soundPlayer = new SoundPlayer(getContext());
	}

	protected int getScannerLayoutResource() {
		return R.layout.view_scanner;
	}

	public ScannerView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}

	public ScannerView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initUI();
	}

	@TargetApi(21)
	public ScannerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initUI();
	}

	public void startScanner() {
		try {
			camera.start();
		} catch (Exception e) {
			Log.e(TAG, "Exception: " + e);
		}
	}

	public void stopScanner() {
		try {
			camera.stop();
		} catch (Exception e) {
			Log.e(TAG, "Exception: " + e);
		}
	}

	public CAMView getCamera() {
		return camera;
	}

	public ScannerViewEventListener getScannerViewEventListener() {
		return scannerViewEventListener;
	}

	public void setScannerViewEventListener(final ScannerViewEventListener scannerViewEventListener) {
		this.scannerViewEventListener = scannerViewEventListener;
	}

	public void setScannerSoundAudioResource(final int scannerSoundAudioResource) {
		this.scannerSoundAudioResource = scannerSoundAudioResource;
	}

	public boolean isPlaySound() {
		return playSound;
	}

	public void setPlaySound(final boolean playSound) {
		this.playSound = playSound;
	}

	public void setHudImageResource(int res) {
		if (hud != null) {
			hud.setBackgroundResource(res);
			setHudVisible(res != 0);
		}
	}

	public void setHudVisible(boolean visible) {
		if (hud != null) {
			hud.setVisibility(visible ? VISIBLE : INVISIBLE);
		}
	}

	/**
	 * set decode barcode image time interval
	 * @param decodeTimeInterval
	 */
	public void setDecodeTimeInterval(long decodeTimeInterval) {
		this.decodeTimeInterval = decodeTimeInterval;
	}

	public void onCameraReady(Camera camera) {
		if (scannerViewEventListener != null) {
			scannerViewEventListener.onScannerReady();
		}
	}

	public void onCameraError(final int err, final Camera camera) {
		if (scannerViewEventListener != null) {
			scannerViewEventListener.onScannerFailure(err);
		}
	}

	public void onPreviewData(final byte[] bytes, final int i, final Camera.Size size) {

		if (SystemClock.elapsedRealtime() - lastDecodeTime > decodeTimeInterval) {
			lastDecodeTime = SystemClock.elapsedRealtime();

			if (scannerViewEventListener != null) {
				Log.d(TAG, "decode!");
				final String data = decoder.decode(bytes, size.width, size.height);
				if (!TextUtils.isEmpty(data)) {
					if (scannerViewEventListener.onCodeScanned(data)) {
						beep();
					}
				}
			}
		}
	}

	private void beep() {
		if (playSound && scannerSoundAudioResource != 0) {
			soundPlayer.playRawResource(scannerSoundAudioResource, false);
		}
	}

	public interface ScannerViewEventListener {
		void onScannerReady();

		void onScannerFailure(int cameraError);

		boolean onCodeScanned(final String data);
	}
}
