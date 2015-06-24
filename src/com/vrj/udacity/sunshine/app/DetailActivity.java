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
					.add(R.id.container, new DetailFragment()).commit();
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
	public static class DetailFragment extends Fragment {
		public static final String LOG_TAG = DetailFragment.class.getSimpleName();
	    private ShareActionProvider mShareActionProvider;
		private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
		private String mForecastStr = "";
		
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		
		public DetailFragment() {
			setHasOptionsMenu(true);  // This Fragment has Options to add to the Options Menu
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_detail,
					container, false);
			
			// Get the intent that started this activity
			Intent intent = getActivity().getIntent();
			
			// Check for intent and extras
			if ( (intent != null) && (intent.hasExtra(Intent.EXTRA_TEXT))) {
				mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);  // You sent text as the extra
				((TextView) rootView.findViewById(R.id.detail_text))
					.setText(mForecastStr) ;  // find the textView in the fragment_detail and set it.
			}
			
			return rootView;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
		 */
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			
			// Inflate menu.  Adds items to action bar if it is present
			inflater.inflate(R.menu.detailfragment, menu);
			
			// Retrieve share action menu
			MenuItem menuItem = menu.findItem(R.id.action_share);

			// Get provider and hold on to set/change the share intent
			mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
			
			// Attach an Intent to the ShareActionProvider.  Can update at any time.  
			// Such as when the user selects a new piece of data they'd like to send.
			if (mShareActionProvider != null) {
				mShareActionProvider.setShareIntent(createShareIntent());
			} else {
				Log.d(LOG_TAG, "Share Action Provider is null?");
			}
			
		}
		

		/**
		 * CREATESHAREFORECASTINTENT - 
		 */
		private Intent createShareIntent() {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			
			// Says to not put the app we are sharing to on the call stack.  So if we 
			// click our apps icon later we are brought to our app and not the app
			// that we were sharing too!
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr 
					+ FORECAST_SHARE_HASHTAG);
			
			return shareIntent;
		}
		
	}
}
