package edu.vt.ece4564.sidekick;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.location.Location;
import android.view.View;


public class CameraOverlayView extends View {

		private float mDirection = 0;
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private boolean firstDraw;
		private float mQuarterCameraViewAngle = 0, mDeclination = 0;;
		private ArrayList<Place> mPlaces;
		private ArrayList<Integer> mColors;
		private Location mLocation;
		private Random mRand = new Random();
		
		
		public CameraOverlayView(Context context, Location loc, ArrayList<Place> places) {
			super(context);
			
			mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			mPaint.setStrokeWidth(2);
			mPaint.setTextSize(70);
			updatePlaces(places);
			mLocation = loc;
			
			firstDraw = true;
		}
		
		public void setLocation(Location loc) {
			mLocation = loc;
		}
		
		public void setPlaces(ArrayList<Place> places) {
			updatePlaces(places);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		}

		@Override
		protected void onDraw(Canvas canvas) {

			if(!firstDraw) {

				int width = getMeasuredWidth()/2;
				int height = getMeasuredHeight()/5;
				
				//Draw heading on screen
				/*mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Style.FILL);
				canvas.drawText(String.valueOf(mDirection), width, height*2+5, mPaint);*/
				
				// Draw names that correlates to heading
				int xVal = 0;
				float bearing;
				// For all people
				for(int i=0; i<mPlaces.size(); i++) {
					//Get bearing to person
					bearing = mLocation.bearingTo(mPlaces.get(i).getLocation());

					// Remap (-180,180) to (0,360)
					if(bearing < 0)
						bearing += 360;

					// Correct a problem when drawing near the 360/0 degree heading
					if(inInProblemArea(bearing)) {
						if(bearing > 180 && mDirection < 180)
							bearing -= 360;
						if(bearing < 180 && mDirection > 180)
							bearing += 360;
					}

					// Calculate X coordinate to draw to
					float pixPerDeg = width / mQuarterCameraViewAngle;
					xVal = width + (int) ((bearing - mDirection) * pixPerDeg);
					
					// Draw name with random fill and white stroke
					mPaint.setColor(mColors.get(i).intValue());
					mPaint.setStyle(Style.FILL);
					canvas.drawText(mPlaces.get(i).getName(), xVal, height, mPaint);
					mPaint.setColor(Color.WHITE);
					mPaint.setStyle(Style.STROKE);
					canvas.drawText(mPlaces.get(i).getName(), xVal, height, mPaint);
				}
			}

		}
		
		public void updateDirection(float dir) {
			firstDraw = false;
			
			mDirection = dir;

			// Account for difference between magnetic/true north
			mDirection += mDeclination;
			
			// Remap (-180,180) to (0,360)
			if(mDirection < 0)
				mDirection += 360;
			
			invalidate();
		}
		
		public void updateLocation(Location l, float declination) {
			mLocation = l;
			mDeclination = declination;
			invalidate();
		}

		public void setCameraViewAngle(float angle) {
			mQuarterCameraViewAngle = angle/4;
		}
		
		// Determines if a bearing is near the 360/0 degree boundary
		private boolean inInProblemArea(float bearing) {
			float bMin, bMax;
			
			bMin = mDirection-mQuarterCameraViewAngle;
			bMax = mDirection+mQuarterCameraViewAngle;
			
			if(bMin < 0)
				bMin += 360;
			
			if(bMax > 360)
				bMax -= 360;
			
			if(bMin > bMax) {
				if(0 <= bearing && bearing < bMax)
					return true;
				
				if(bMin < bearing && bearing <= 360)
					return true;
			}
			
			return false;
		}

		public void updatePlaces(ArrayList<Place> places) {
			// Load new people group
			mPlaces = places;
			
			// Generate draw colors randomly
			mColors = new ArrayList<Integer>();
			for(int i=0; i<places.size(); i++) {
				mColors.add(Color.argb(255, mRand.nextInt(256), mRand.nextInt(256), mRand.nextInt(256)));
			}
			
			invalidate();
		}
		
		
}
