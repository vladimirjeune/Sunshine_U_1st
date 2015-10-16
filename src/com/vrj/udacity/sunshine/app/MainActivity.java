package com.vrj.udacity.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	private final String LOG_TAG = MainActivity.class.getSimpleName();
	private static final String DETAILFRAGMENT_TAG = "DFTAG";
	private String mLocation = "";
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocation = Utility.getPreferredLocation(this);  // Set to loc in SharedPrefs
		setContentView(R.layout.activity_main);

		if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            
            // Note how in the two pane case we check if the saved instance state is null. 
            // Why? Well if we rotate the phone, the system saves the fragment state in 
            // the saved state bundle and is smart enough to restore this state.
            // Therefore, if the saved state bundle is not null, the system already has the fragment it needs and you shouldnŐt go adding another one.
            if (savedInstanceState == null) {
            	getSupportFragmentManager().beginTransaction()
            	.replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
            	.commit();
            }
		} else {
			mTwoPane = false;
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
		super.onResume();
		Log.i(LOG_TAG, "ONRESUME()");
		
		String storedLocation = Utility.getPreferredLocation(this);
		
		// update the location in our second pane using the fragment manager
		if ((storedLocation != null) && (!storedLocation.equals(mLocation))) {  // If there is mismatch, correct it
			ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_forecast);  
			
			if (null != ff) {
				ff.onLocationChange();  // Get new data and place it in DB
			}
			
			mLocation = storedLocation;  // Repair mismatch here as well
		}
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
