package edu.vt.ece4564.sidekick;

import vt.edu.ece4564.sidekick.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class LoginView extends Activity implements OnClickListener{		

	/* LoginActivity is the starting activity for the app
	 * - allows users to login using their facebook information
	 * 	or continue without logging in
	 */

	@SuppressWarnings("unused")
	private final String APP_ID = "395143483950913";						//the app id assigned by facebook
	
	//private ProfilePictureView mProfilePic;								//stores the user's facebook profile picture
	private Button mContinueButton;											//button to progress to the camera view

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//Getting the UI elements
		mContinueButton = (Button) findViewById(R.id.loginGoToFinderButton);
		mContinueButton.setOnClickListener(this);								//Listen for the user to click the continue button

		if(Session.getActiveSession() != null){									//if there is an active session, get the user's facebook info.
			Session.openActiveSession(this, true, new Session.StatusCallback(){	//no need to login again.
				@Override
				public void call(Session session, SessionState state,Exception exception) {
					if (session.isOpened()) {

						// make request to the /me API
						Request.newMeRequest(session, new Request.GraphUserCallback() {

							@Override
							public void onCompleted(GraphUser user, Response response) {
								if(user != null){

									UserInfoHolder.getInstance().setUserName(user.getName());				//store the user info in the UserInfoHolder
									UserInfoHolder.getInstance().setUserFirstName(user.getFirstName());								
									UserInfoHolder.getInstance().setProfileId(user.getId());
									UserInfoHolder.getInstance().setLoginStatus(true);
									
									mContinueButton.setText("Continue");									//Change text of button since the user has logged in

									//let the user know they've signed in
									Toast.makeText(getApplicationContext(), "Welcome back, " + UserInfoHolder.getInstance().getFirstName(), Toast.LENGTH_SHORT).show();
									Intent i = new Intent(getApplicationContext(), CameraView.class);
									startActivity(i);
								}
							}
						}).executeAsync();
					}
					UserInfoHolder.getInstance().setUserName(null);										//if the  session is closed, erase all
					UserInfoHolder.getInstance().setUserFirstName(null);								//of the user's stored info
					UserInfoHolder.getInstance().setProfileId(null);									//and mark the loginstatus as not logged in
					UserInfoHolder.getInstance().setLoginStatus(false);
					
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {						//the results from facebook when a session changes
		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

		Session.openActiveSession(this, true, new Session.StatusCallback(){								//no need to login again.
			@Override
			public void call(Session session, SessionState state,Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user, Response response) {
							if(user != null){

								//the user signed in, store their info.
								UserInfoHolder.getInstance().setUserName(user.getName());
								UserInfoHolder.getInstance().setUserFirstName(user.getFirstName());								
								UserInfoHolder.getInstance().setProfileId(user.getId());
								UserInfoHolder.getInstance().setLoginStatus(true);
								mContinueButton.setText("Continue");

								//let the user know they've signed in
								Toast.makeText(getApplicationContext(), "Welcome back, " + UserInfoHolder.getInstance().getFirstName(), Toast.LENGTH_SHORT).show();
								
								//Continue to CameraView
								Intent i = new Intent(getApplicationContext(), CameraView.class);
								startActivity(i);
							}
						}
					}).executeAsync();
				}
				//no session, delete the user's info
				UserInfoHolder.getInstance().setUserName(null);
				UserInfoHolder.getInstance().setUserFirstName(null);
				UserInfoHolder.getInstance().setProfileId(null);
				UserInfoHolder.getInstance().setLoginStatus(false);
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.loginGoToFinderButton:			//the continue button
		{
			Intent i = new Intent(getApplicationContext(), CameraView.class);	//go to camera view
			startActivity(i);
		}
		}//end switch
	}
}




