package edu.vt.ece4564.sidekick;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera = null;
	private Context mContext;
	CameraListener mCameraListener;

	public CameraSurface(Context context, CameraListener cl) {
		super(context);
		mContext = context;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mCameraListener = cl;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// Fix camera rotation
		int rotation = 0;
		switch (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
		case Surface.ROTATION_0:
			rotation = 90;
			break;
		case Surface.ROTATION_90:
			rotation = 0;
			break;
		case Surface.ROTATION_180:
			rotation = 270;
			break;
		case Surface.ROTATION_270:
			rotation = 180;
			break;
		default:
			Log.e("Rotation", "Invalid case");
			break;
		}

		// Start display
		mCamera.setDisplayOrientation(rotation);
		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null) {
	        try {
	        	// Try to start camera
		    	mCamera = Camera.open();
		    	mCamera.setPreviewDisplay(mHolder);
		    	
	        } catch (IOException e) {
				e.printStackTrace();
				mCamera.release();
				mCamera = null;
			} catch (Exception e) {
	        	e.printStackTrace();
	        	mCamera = null;
	        }
	    }

		// Camera wont open
		if (mCamera == null) {
			Toast.makeText(mContext, "Cannot start camera. App closed", Toast.LENGTH_LONG).show();
			System.exit(0); // Kill activity (I know this is bad but app will crash without)
		}

		mCameraListener.setCameraViewAngle((float)mCamera.getParameters().getHorizontalViewAngle());
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Stop camera
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

}
