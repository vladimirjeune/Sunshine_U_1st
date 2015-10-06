package com.vrj.udacity.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrj.udacity.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = DetailFragment.class.getSimpleName();
	private static final int DETAIL_LOADER_ID = 0;  // Loader ids MUST be unique per activity
    private ShareActionProvider mShareActionProvider;
	private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
	private String mForecastStr = "";
			
	private static final String[] DETAIL_COLUMNS = {
		WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
		WeatherContract.WeatherEntry.COLUMN_DATE,
		WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
		WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
		WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
		WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
		WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
		WeatherContract.WeatherEntry.COLUMN_DEGREES,
		WeatherContract.WeatherEntry.COLUMN_PRESSURE
	};

	// These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
	// must change.  Projections
	static final int COL_WEATHER_ID = 0;
	static final int COL_WEATHER_DATE = 1;
	static final int COL_WEATHER_DESC = 2;
	static final int COL_WEATHER_MAX_TEMP = 3;
	static final int COL_WEATHER_MIN_TEMP = 4;
	static final int COL_HUMIDITY = 5;
	static final int COL_WIND = 6;
	static final int COL_WIND_DEGREES = 7;
	static final int COL_PRESSURE = 8;
	
	
	
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
		if (mForecastStr != null) {  // Only do this if there is a forecast to share. No assumption about order
			mShareActionProvider.setShareIntent(createShareIntent());
		}
		
	}
	

	/**
	 * CREATESHAREFORECASTINTENT - Can share data with other applications like e-mail
	 */
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		
		// Says to not put the app we are sharing to on the call stack.  So if we 
		// click our apps icon later we are brought to our app and not the app
		// that we were sharing to!
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr 
				+ FORECAST_SHARE_HASHTAG);  // Data to share
		
		return shareIntent;
	}

	/**
	 * ONCREATELOADER - will call ContentProvider when called by LoaderManager.
	 * 		Since derived from AsyncTaskLoader, will do operations on background
	 * 		thread.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.v(LOG_TAG, "In onCreateLoader");
		Intent intent = getActivity().getIntent();
		
		if (null == intent) { 
			return null; 
		}
		
		return new CursorLoader(
				getActivity(), 
				intent.getData(),
				DETAIL_COLUMNS, 
				null, 
				null, 
				null);
	}

	/**
	 * ONLOADFINISHED - When operation completes this is called and holds the values we retrieved
	 * 		from the DB.  No need to requery after rotation.  initializer will notice data is done
	 * 		and use it from here.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		Log.v(LOG_TAG, "In onLoadFinished()");
		if (!cursor.moveToFirst()) {  // Setting Cursor to 1st position and checking for data
			Log.d(LOG_TAG, "Nothing came back from the cursor");
			return;
		}
		
		// Use our Utility Class to obtain correct functions for formatting
		boolean isMetric = Utility.isMetric(getActivity());
		
		// Gather all Views
		TextView detail_date_textView = (TextView) getView()
				.findViewById(R.id.detail_date_textView);
		TextView detail_high_textView = (TextView) getView()
				.findViewById(R.id.detail_high_textView);
		TextView detail_low_textView = (TextView) getView()
				.findViewById(R.id.detail_low_textView);
		TextView detail_humidity_textView = (TextView) getView()
				.findViewById(R.id.detail_humidity_textView);
		TextView detail_wind_textView = (TextView) getView()
				.findViewById(R.id.detail_wind_textView);
		TextView detail_pressure_textView = (TextView) getView()
				.findViewById(R.id.detail_pressure_textView);
		ImageView detail_icon_imageView = (ImageView) getView()
				.findViewById(R.id.detail_icon);
		TextView detail_forecast_textView = (TextView) getView()
				.findViewById(R.id.detail_forecast_textView);
		
		
		long dateInMS = cursor.getLong(COL_WEATHER_DATE);
		detail_date_textView.setText(Utility
				.getFriendlyDayString(getActivity(), dateInMS));
		
		detail_high_textView.setText(Utility.formatTemperature(getActivity() 
				, cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
		
		detail_low_textView.setText(Utility.formatTemperature(getActivity() 
				, cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric));
					
		detail_humidity_textView.setText(getActivity()
				.getString(R.string.format_humidity, cursor.getFloat(COL_HUMIDITY)));  // Utility?
		
		detail_wind_textView.setText(Utility.getFormattedWind(getActivity()
				, cursor.getFloat(COL_WIND)
				, cursor.getFloat(COL_WIND_DEGREES)));
		
		detail_pressure_textView.setText(getActivity()
				.getString(R.string.format_pressure, cursor.getFloat(COL_PRESSURE)));

		detail_icon_imageView.setImageResource(R.drawable.ic_launcher);

		detail_forecast_textView.setText(getActivity()
				.getString(R.string.format_forecast, cursor.getString(COL_WEATHER_DESC)));
		
		// If onCreateOptionMenu has already occurred, we need to update the shareIntent
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(createShareIntent());  // Since mForecastStr has changed
		}
		
	}

	/**
	 * ONLOADERRESET - called so you can release resources so they can be properly released by class.
	 * 		Nothing done in this case since we are not using Adapter.
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// Not using cursorAdapter so no resources to releaase
	}
	
}
