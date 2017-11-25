package Created_by_TA.line_follower;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class DrawOnTop extends View {
	//Display
	Bitmap mBitmap;
	Paint mPaint;
	int mImageWidth, mImageHeight;
	
	//Frame data
	byte[] mYUVData;
	int[] mRGBData;
	int[] mBWData;
	
	//FPS
	long lastFrame = System.currentTimeMillis();
	
	//PID
	float proportional;
	float integral;
	float derivative;
	float last_proportional;
	int Kp;
	int Kd;	
	int Ki;
	int test_usb_status;
	int test_sending_status;
	int Max_right_speed;
	int Max_left_speed;
	boolean black_background;
	//Motor Controls
	int right_speed=0;
	int left_speed=0;
	int max_speed = 255;
	
	//Line Tracking	
	int minPixels = 10; //Min pixels to identify as tape
	int threshold = 60;
	
	public static final String MyPREFERENCES = "MyPrefereces" ;
    public static final String test_premiere_execution  ="test"; 
    public static final String  old_derivativ="Key_old_derivativ"; 
	public static final String  old_proportional="Key_old_proportional"; 
	public static final String  old_integral="Key_old_integral";
	public static final String  old_right_speed="Key_old_right_speed"; 
	public static final String  old_left_speed="Key_old_left_speed"; 
	public static final String USB_STATUS  ="key_USB_STATUS";
	public static final String SENDING_STATUS  ="key_SENDING_STATUS";
	public static final String  black_background_test="Key_background";
	
    public DrawOnTop(Context context) {
        super(context);
        
       SharedPreferences sharedpreferences;
	   sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
       Kp=sharedpreferences.getInt(old_proportional,1);
       Kd=sharedpreferences.getInt(old_derivativ,2);
       Ki=sharedpreferences.getInt(old_integral,0);
       Max_right_speed=sharedpreferences.getInt(old_right_speed,255);
       Max_left_speed=sharedpreferences.getInt(old_left_speed,255);
       black_background=sharedpreferences.getBoolean(black_background_test,true);
       test_usb_status=sharedpreferences.getInt(USB_STATUS,0);
       test_sending_status=sharedpreferences.getInt(SENDING_STATUS,0);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(25);
        
        mBitmap = null;
        mYUVData = null;
        mRGBData = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null)
        {
        	int canvasWidth = canvas.getWidth();
        	int canvasHeight = canvas.getHeight();
        	        	
        	// Convert from YUV to Grayscale and threshold
        	grayscaleToBlackWhite(mRGBData, mYUVData, mImageWidth, mImageHeight);
        	
        	// ideally we only have one cluster in the tape line (which we assume to be our tape)
        	// calc centre of cluster and subtract from center of screen
        	// input to PID        	
        	int tapeCenter = findTape(mRGBData, mImageWidth, mImageHeight);
        	        	
        	//PID
    		proportional = tapeCenter - mImageHeight/2;
    		integral = integral + proportional;
    		derivative = proportional - last_proportional;
    		last_proportional = proportional;
    		int lineError = (int)(proportional * Kp + integral * Ki + derivative * Kd);
        	
    		// Restricting the error value between +/- max_speed.
    		if(Max_right_speed>Max_left_speed) max_speed=Max_left_speed;
    		else max_speed=Max_right_speed;
    		
    		if (lineError< -max_speed)
    		{
    			lineError = -max_speed;
    		}
    		if (lineError> max_speed)
    		{
    			lineError = max_speed;
    		}
        	
    		// If error_value is less than zero calculate right turn speed values
    		if (lineError< 0)
    		{
    		right_speed = Max_right_speed + lineError;
    		left_speed = Max_left_speed;
    		}
    		// If error_value is greater than zero calculate left turn values
    		else
    		{
    		right_speed = Max_right_speed;
    		left_speed = Max_left_speed - lineError;
    		}    		

        	long curFrame = System.currentTimeMillis();
        	float fps = 1000/(curFrame - lastFrame);
        	lastFrame = curFrame;
        	
        	// Draw bitmap
        	mBitmap.setPixels(mRGBData, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);       	
        	
        	Rect src = new Rect(0, 0, mImageWidth, mImageHeight);
        	Rect dst = new Rect(0, 0, canvasWidth, canvasHeight);
        	canvas.drawBitmap(mBitmap, src, dst, mPaint);
        	
        	canvas.drawLine(0, canvasHeight/2, canvasWidth, canvasHeight/2, mPaint);     	

        	canvas.rotate(-90, canvasWidth/2, canvasHeight/2);
        	mPaint.setTextSize(25);
        	mPaint.setColor(Color.BLUE);
        	canvas.drawText(mImageWidth + "x" + mImageHeight, 200, -140, mPaint);
        	canvas.drawText("fps: " + fps, 200, -110, mPaint);
        	
        	
            	mPaint.setTextSize(40);
            	mPaint.setStyle(Paint.Style.FILL);
            	mPaint.setColor(Color.MAGENTA);
            	canvas.drawText(String.valueOf(right_speed),2*canvasWidth/3,2*canvasHeight/3, mPaint);  
        		canvas.drawText(String.valueOf(left_speed),canvasWidth/4, 2*canvasHeight/3, mPaint);  
        		mPaint.setTextSize(30);
        		mPaint.setColor(Color.MAGENTA);
        		mPaint.setColor(Color.RED);
        		
        		
        		
        		mPaint.setColor(Color.MAGENTA);
        } // end if statement

        super.onDraw(canvas);
        
    } // end onDraw method

    public int findTape(int[] rgb, int width, int height) {
    	//line is located on the X axis (longer part of the screen)
    	int tapeStart = -1; 
		int tapeEnd = -1;
    	
		int tmpStart = -1;
		int tmpEnd = -1;
		int scanline = -1;
		
		//scan from top to bottom of screen
		for (int x = 0; x<width; x+=50) 
		{
	    	for (int pix = 0; pix < height; pix++)
	    	{
	    		int pixVal = (0xff & (int) rgb[x+pix*width]);
	    		boolean test;
	    		if (black_background) test=(pixVal != 0);
	    		else test=(pixVal == 0);
	    		if (test) {
	    			if (tmpStart == -1)
	    			{
	    				tmpStart = pix;    		
	    			}
	    			else
	    			{
	    				tmpEnd = pix;
	    				
	    				if ( tmpEnd-tmpStart > minPixels && (Math.abs((tmpEnd-tmpStart)/2 - height/2) <  Math.abs((tapeEnd-tapeStart)/2 - height/2)) )
	    				{
	    					tapeEnd = tmpEnd;
	    					tapeStart = tmpStart;
	    					scanline = x;
	    				}
	    			}
	    			
	    		} else {
	    			tmpStart = -1;
	    			tmpEnd = -1;
	    		}
	    	}
		}
    	
    	
    	if (tapeStart > -1 && tapeEnd > -1 && scanline >-1) {
	    	for (int pix = tapeStart; pix <= tapeEnd; pix++) {
				rgb[scanline+pix*width] = 0xff000000 | (0 << 16) | (255 << 8) | 0;
	    	}
    	}
    	return (tapeEnd+tapeStart)/2;
    }
    
    public void grayscaleToBlackWhite(int[] rgb, byte[] yuv420sp, int width, int height)
    {
    	final int frameSize = width * height;
    	
    	for (int pix = 0; pix < frameSize; pix++)
    	{
    		int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
    		if (pixVal < threshold) 
    			pixVal = 0;
    		else
    			pixVal = 255;
    		rgb[pix] = 0xff000000 | (pixVal << 16) | (pixVal << 8) | pixVal;
    	}
    }
} 
