package edu.vt.ece4564.sidekick;


public class UserInfoHolder {

	/* This is a singleton class that is used to store the user's
	 * name and profile picture
	 * 
	 * 
	 */
	private String mUserName;
	private String mUserFirstName;
	private String mProfileId;
	private boolean mLoginStatus;
	
	//just getters and setters
	
	public void setLoginStatus(boolean b){
		//b = 0 for not logged in
		//b = 1 for logged in
		
		mLoginStatus = b;
	}
	
	public boolean getLoginStatus(boolean b){
		//1-logged in
		//0-not logged in
		return mLoginStatus;
	}
	
	public void setUserName(String name){
		mUserName = name;	
	}

	public void setUserFirstName(String name){
		mUserFirstName = name;
	}

	public void setProfileId(String s){
		mProfileId = s;
	}

	public String getName(){
		return mUserName; 
	}
	public String getFirstName(){
		return mUserFirstName; 
	}
	public String getProfileId(){ 
		return mProfileId; 
	}

	private static final UserInfoHolder holder = new UserInfoHolder();
	public static UserInfoHolder getInstance(){ 
		return holder; 
	}
}