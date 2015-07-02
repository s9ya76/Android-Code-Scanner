package eu.livotov.zxscan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.FontAwesomeText;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 03/11/2014
 */
public class ScannerFragment extends Fragment implements ScannerView.ScannerViewEventListener {
	private FontAwesomeText mStopScannerBtn;
	private BootstrapButton mActionScannerBtn;
	private TextView mScannerMsg;
	protected ScannerView scanner;
	protected ScannerView.ScannerViewEventListener scannerViewEventListener;

	private Boolean ShowActionScanBtnOnInitial = false;
	private Boolean startOnResume = true;
	private String scannerMsg = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
		return rootView;
	}

	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mStopScannerBtn = (FontAwesomeText) view.findViewById(R.id.stop_scanner_btn);
		mActionScannerBtn = (BootstrapButton) view.findViewById(R.id.action_scanner_btn);
		mScannerMsg = (TextView) view.findViewById(R.id.scanner_msg);

		scanner = (ScannerView) view.findViewById(R.id.scanner);
		scanner.setScannerViewEventListener(this);
		scanner.startScanner();

		mActionScannerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mStopScannerBtn.setVisibility(View.VISIBLE);
				mActionScannerBtn.setVisibility(View.INVISIBLE);
				getScanner().startScanner();
			}
		});

		mStopScannerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mStopScannerBtn.setVisibility(View.INVISIBLE);
				mActionScannerBtn.setVisibility(View.VISIBLE);
				getScanner().stopScanner();
			}
		});
	}

	public void onResume() {
		super.onResume();
		// scanner.startScanner();
		if (startOnResume) {
			scanner.startScanner();
		} else {
			scanner.stopScanner();
		}

		if (ShowActionScanBtnOnInitial) {
			mActionScannerBtn.setVisibility(View.VISIBLE);
		}

		if (scannerMsg != null) {
			mScannerMsg.setText(scannerMsg);
		}
	}

	public void onPause() {
		super.onPause();
		scanner.stopScanner();
	}

	public BootstrapButton getStartButton() {
		return mActionScannerBtn;
	}

	public void showStartBtn(Boolean isShow) {
		if (isShow) {
			mActionScannerBtn.setVisibility(View.VISIBLE);
			mStopScannerBtn.setVisibility(View.INVISIBLE);
		} else {
			mActionScannerBtn.setVisibility(View.INVISIBLE);
			mStopScannerBtn.setVisibility(View.VISIBLE);
		}
	}

	public void setScannerMsg(String msg) {
		scannerMsg = msg;
	}

	public ScannerView.ScannerViewEventListener getScannerViewEventListener() {
		return scannerViewEventListener;
	}

	public void setScannerViewEventListener(final ScannerView.ScannerViewEventListener scannerViewEventListener) {
		this.scannerViewEventListener = scannerViewEventListener;
	}

	public void setShowActionScanBtnOnInitial(final Boolean isShow) {
		this.ShowActionScanBtnOnInitial = isShow;
	}

	public void setStartOnResume(final Boolean startOnResume) {
		this.startOnResume = startOnResume;
	}

	public ScannerView getScanner() {
		return scanner;
	}

	@Override
	public void onScannerReady() {

	}

	@Override
	public void onScannerFailure(int cameraError) {

	}

	public boolean onCodeScanned(final String data) {
		if (scannerViewEventListener != null) {
			scannerViewEventListener.onCodeScanned(data);
		}

		return true;
	}
}
