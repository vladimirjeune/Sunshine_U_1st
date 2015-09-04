package com.vrj.udacity.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	private final String LOG_TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new ForecastFragment()).commit();
			
		}
		Log.i(LOG_TAG, "ONCREATE()");
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(LOG_TAG, "ONSTOP()");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(LOG_TAG, "ONDESTROY()");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(LOG_TAG, "ONPAUSE()");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(LOG_TAG, "ONRESUME()");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(LOG_TAG, "ONSTART()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// We do not need an Intent variable for anything so just call in function
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (id == R.id.action_map) {
			openPreferredLocationInMap();  
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * OPENPREFERREDLOCATIONINMAP - Created to give use option of seeing preferred location on map
	 */
	private void openPreferredLocationInMap(){
				
		String location = Utility.getPreferredLocation(this);
		
		// Using Scheme for geolocation data
		String geoScheme = "geo:0,0?";
		

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
		// Constructing a URI using the Scheme above and the location data from
		// SharedPreferences.  Then parsing it to correct format for safe
		// web transmission
		Uri geoLocation = Uri.parse(geoScheme)
				.buildUpon()
				.appendQueryParameter("q", location)
				.build();
		
		// Creating implicit intent to call some Map app
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(geoLocation);
		
		// Only go through with intent if their exists an app on the device
		// that can complete the action we are asking it to do.  Otherwise we
		// would crash.
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else {
			Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
		}
		
	}

}
