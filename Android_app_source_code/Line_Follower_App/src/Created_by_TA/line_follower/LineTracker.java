package Created_by_TA.line_follower;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;


import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;


public class LineTracker extends Activity implements SensorEventListener {
	private static final String TAG = "Line Follower by TA";

	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	
	public static final String MyPREFERENCES = "MyPrefereces" ;
   
    SharedPreferences sharedpreferences;
    
	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	private Preview mPreview;
    private DrawOnTop mDrawOnTop;
 // Orientation sensor and data
    SensorManager sensorMgr;
    Sensor orSensor;
    static Float azimuth=null;
    static Float pitch=null;
    static Float roll=null;
    

	
	private Handler myHandler;
	private Runnable myRunnable = new Runnable() {
	@Override
	public void run() {
	    // Code à éxécuter de façon périodique
		sendCommand(1,mDrawOnTop.right_speed);
		sendCommand(2,mDrawOnTop.left_speed);
	    myHandler.postDelayed(this,1);
	    }
	};
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}
		 

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		    mDrawOnTop = new DrawOnTop(this);
	        mPreview = new Preview(this, mDrawOnTop);
			 //Sending Thread
	        setContentView(mPreview);
	        addContentView(mDrawOnTop, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        myHandler = new Handler();
	        myHandler.postDelayed(myRunnable,1);
    	
	        // register orientation sensor events:
	        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
	        orSensor = null;
			for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
	            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
	                orSensor = sensor;
	            }
	        }
	 
	        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_FASTEST);        
    }
    
    @Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}
    
    @Override
	protected void onStop() {
    	sendCommand(3,0);
		super.onStop();
		
		// unregister with the orientation sensor
        sensorMgr.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// not interested in this event	
	}

	public void onSensorChanged(SensorEvent event) {
		azimuth=event.values[0];
		pitch=event.values[1];
		roll=event.values[2];
	}
    @Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}
    
    @Override
	public void onPause() {
		super.onPause();
		sendCommand(3,0);
		 if(myHandler != null)
		        myHandler.removeCallbacks(myRunnable);
		closeAccessory();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}
	
	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			
			Log.d(TAG, "accessory opened");
			
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	
	
	private void closeAccessory() {
		

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}
	
	public void sendCommand(int command, int speed) {
		if (speed > 255)
			speed = 252;
		byte[] buffer = new byte[2];
		buffer[0] = (byte)command;
		buffer[1] = (byte)speed;
		
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
               
	            
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);

			}
			
		}
		
	}
	
	
	}

	
