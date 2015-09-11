package com.vrj.udacity.sunshine.app;

import com.vrj.udacity.sunshine.app.data.WeatherContract;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

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
	public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
		public static final String LOG_TAG = DetailFragment.class.getSimpleName();
		private static final int DETAIL_LOADER_ID = 10;  // Loader ids MUST be unique
	    private ShareActionProvider mShareActionProvider;
		private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
		private String mForecastStr = "";
		private TextView tv = null;
		
		private static final String[] DETAIL_COLUMNS = {
			WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
			WeatherContract.WeatherEntry.COLUMN_DATE,
			WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
			WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
			WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
			WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
			WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
			WeatherContract.LocationEntry.COLUMN_COORD_LAT,
			WeatherContract.LocationEntry.COLUMN_COORD_LONG
		};

		// These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
		// must change.  Projections
		static final int COL_WEATHER_ID = 0;
		static final int COL_WEATHER_DATE = 1;
		static final int COL_WEATHER_DESC = 2;
		static final int COL_WEATHER_MAX_TEMP = 3;
		static final int COL_WEATHER_MIN_TEMP = 4;
		static final int COL_LOCATION_SETTING = 5;
		static final int COL_WEATHER_CONDITION_ID = 6;
		static final int COL_COORD_LAT = 7;
		static final int COL_COORD_LONG = 8;
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// Initializing Loader w/ LoaderManager
			getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
			super.onActivityCreated(savedInstanceState);
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
			
			// Check for intent
			if ( intent != null) {
				mForecastStr = intent.getDataString();  // You set Data using setData(), for now data is URI
			}
			
			if (null != mForecastStr) {
//				((TextView) rootView.findViewById(R.id.detail_text))
//				.setText(mForecastStr) ;  // find the textView in the fragment_detail and set it.

				// Need to set tv in onLoadFinished()
				tv = ((TextView) rootView.findViewById(R.id.detail_text));
				tv.setText(mForecastStr);
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
			// that we were sharing to!
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr 
					+ FORECAST_SHARE_HASHTAG);
			
			return shareIntent;
		}


		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			Intent intent = getActivity().getIntent();
			Uri uri = null;
			
			String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
			
			if (intent != null) {  // if we could find it
				uri = Uri.parse(intent.getDataString());
			}
			
			return new CursorLoader(
					getActivity(), 
					uri,
					DETAIL_COLUMNS, 
					null, 
					null, 
					sortOrder);
		}


		@Override
		public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
			boolean isMetric = Utility.isMetric(getActivity());
			
			if (null != cursor) {
				if (!cursor.moveToFirst()) {  // Setting Cursor to 1st position and checking for data
					Log.d(LOG_TAG, "Nothing came back from the cursor");
				}
				String highLowStr = Utility.formatTemperature(
						cursor.getDouble(DetailFragment.COL_WEATHER_MAX_TEMP) 
						, isMetric) 
						+ "/" +
						Utility.formatTemperature(
								cursor.getDouble(DetailFragment.COL_WEATHER_MIN_TEMP)
								, isMetric);
				
				mForecastStr = Utility.formatDate(cursor.getLong(DetailFragment.COL_WEATHER_DATE)) + 
						" - " + cursor.getString(DetailFragment.COL_WEATHER_DESC) +
						" - " + highLowStr;
				
				tv.setText(mForecastStr);
			}
			
		}


		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// Not using cursorAdapter so no resources to releaase
			
		}
		
	}
}
