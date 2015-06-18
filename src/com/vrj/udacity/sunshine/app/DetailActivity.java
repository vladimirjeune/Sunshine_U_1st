package com.vrj.udacity.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
			
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			// We are not expecting an extra so just call like this
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public static final String TAG = "PlaceholderFragment";
	    private ShareActionProvider mShareActionProvider;
		private final String HASHTAG = "#SunshineApp";
		private String forecast = "";
		
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);  // This Fragment has Options to add to the Options Menu
		}

		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			// Get the intent that started this activity
			Intent intent = getActivity().getIntent();
			View rootView = inflater.inflate(R.layout.fragment_detail,
					container, false);
			
			// Check for intent and extras
			if ( (intent != null) && (intent.hasExtra(Intent.EXTRA_TEXT))) {
				forecast = intent.getStringExtra(Intent.EXTRA_TEXT);  // You sent text as the extra
				((TextView) rootView.findViewById(R.id.detail_text)).setText(forecast) ;  // find the textView in the fragment_detail and set it.
			}
			
			setShareIntent(setTextIntentToShare(forecast + " " + HASHTAG));
			
			return rootView;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
		 */
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			
			Log.i(TAG, "onCreateOptionsMenu enter");			
			// Inflate menu resource file
			inflater.inflate(R.menu.detail, menu);
			
			MenuItem item = menu.findItem(R.id.action_share);

			mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//			mShareActionProvider.setShareIntent(getDefaultIntent());  // Removed on purpose
			super.onCreateOptionsMenu(menu, inflater);
			Log.i(TAG, "onCreateOptionsMenu exit");			
			
		}
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onPrepareOptionsMenu(android.view.Menu)
		 * Gets called when Menu is to be presented.  onCreateOptions happens only once
		 */
		@Override
		public void onPrepareOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			
			Log.i(TAG, "onPrepareOptionsMenu enter");			
			// Called every time because each DetailActivity has different data to SEND
			setShareIntent(setTextIntentToShare(forecast + " " + HASHTAG));
			Log.i(TAG, "onPrepareOptionsMenu exit");	
			
			super.onPrepareOptionsMenu(menu);
		}


		/**
		 * SETTEXTINTENTTOSHARE - will return an Intent set with the text that we want to share
		 * @param String - text - text to be sent through the Intent 
		 */
		private Intent setTextIntentToShare(String text) {
			
			// Type of Action
			Intent intent = new Intent(Intent.ACTION_SEND);
			
			// Text to send [KEY:VALUE]
			intent.putExtra(Intent.EXTRA_TEXT, text);
			
			// Set MimeType
			intent.setType("text/plain");
			
			return intent;
		}
		
		/**
		 * SETSHAREINTENT - Call this to update Share Intent
		 * @param shareIntent
		 */
		private void setShareIntent(Intent shareIntent) {
			if (mShareActionProvider != null) {
				mShareActionProvider.setShareIntent(shareIntent);
			}
		}
		
		/**
		 * GETDEFAULTINTENT - will return an intent with a default value to initialize provider.  
		 * As soon as actual intentions are known mShareActionProvider.setShareIntent() should be called.
		 * @return
		 */
		private Intent getDefaultIntent() {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			return intent;
		}
		
	}
}
