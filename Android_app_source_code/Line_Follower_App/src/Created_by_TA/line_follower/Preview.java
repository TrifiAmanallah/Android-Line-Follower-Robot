package Created_by_TA.line_follower;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    DrawOnTop mDrawOnTop;
    boolean mFinished;
    boolean activer_torch;
    
    public static final String MyPREFERENCES = "MyPrefereces" ;
    public static final String test_torch="Key_torch";
    SharedPreferences sharedpreferences;
    
    Preview(Context context, DrawOnTop drawOnTop) {
        super(context);
 	   sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
 	   activer_torch=sharedpreferences.getBoolean(test_torch,true);
       mDrawOnTop = drawOnTop;
       mFinished = false;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    	
    	int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
    	
        try {
        	mCamera = Camera.open(0);
        	mCamera.setPreviewDisplay(holder);
           
           // Preview callback used whenever new viewfinder frame is available
           mCamera.setPreviewCallback(new PreviewCallback() {
        	  public void onPreviewFrame(byte[] data, Camera camera)
        	  {
        		  if ( (mDrawOnTop == null) || mFinished )
        			  return;
        		  
        		  if (mDrawOnTop.mBitmap == null)
        		  {
        			  // Initialize the draw-on-top companion
        			  Camera.Parameters params = camera.getParameters();
        			  mDrawOnTop.mImageWidth = params.getPreviewSize().width;
        			  mDrawOnTop.mImageHeight = params.getPreviewSize().height;
        			  mDrawOnTop.mBitmap = Bitmap.createBitmap(mDrawOnTop.mImageWidth, mDrawOnTop.mImageHeight, Bitmap.Config.RGB_565);
        			  mDrawOnTop.mRGBData = new int[mDrawOnTop.mImageWidth * mDrawOnTop.mImageHeight]; 
        			  mDrawOnTop.mYUVData = new byte[data.length];        			  
        		  }
        		  
        		  // Pass YUV data to draw-on-top companion
        		  System.arraycopy(data, 0, mDrawOnTop.mYUVData, 0, data.length);
    			  mDrawOnTop.invalidate();
        	  }
           });
        } 
        catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
        
		
		}   
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	mFinished = true;
    	mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(160, 120);
        parameters.setPreviewFrameRate(30);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        
        if(activer_torch){
        //Turn On FlashLight
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
                        return;
                                 }
          String flashMode = parameters.getFlashMode();
        if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
              parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
              mCamera.setParameters(parameters);
              } 
          }
        }
        else{
        	 List<String> flashModes = parameters.getSupportedFlashModes();
             if (flashModes == null) {
                             return;
                                      }
               String flashMode = parameters.getFlashMode();
        	if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                // Turn off the flash
                if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
                  parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                  mCamera.setParameters(parameters);
                }
        	}	
        }
        ///////////////////////
        
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
}
