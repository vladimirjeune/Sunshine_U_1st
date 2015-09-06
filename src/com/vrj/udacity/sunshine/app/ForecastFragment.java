/**
 * 
 */
package com.vrj.udacity.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vrj.udacity.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 * @author vladimirjeune
 *
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// Package name ensures keys are unique in case interacts with other apps.
	public final static String EXTRA_MESSAGE ="com.vrj.udacity.sunshine.app.MESSAGE";
	private final static int FORECAST_LOADER_ID = 0;
	private ForecastAdapter mForecastAdapter = null;
	
	private static final String[] FORECAST_COLUMNS = {
		// In this case the id needs to be fully qualified with a table name, since
		// the content provider joins the location & weather tables in the background
		// (both have an _id column)
		// On the one hand, that's annoying.  On the other, you can search the weather table
		// using the location set by the user, which is only in the Location table.
		// So the convenience is worth it.
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
	
	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Override this so the we can use menu events in here.
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);		      // MUST BE CALLED HERE so we can handle menu events for Options Menu
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 0, no flags set
		// The CursorAdapter will take data from our cursor and populate the ListView
		// However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
		// up with an empty list the first time we run.
        // Create an empty adapter we will use to display the loaded data.
		mForecastAdapter = new ForecastAdapter(getActivity(), null, 0); 

		// This is the root of the hierarchy.  No need to get yourself.
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

		// From the root of the Layout Hierarchy find the element you are looking for.
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		
		// ListView will pass an URI need for the DetailView
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// CursorAdapter returns a cursor at the correct position for getItem(), or null
				// if it cannot reach that position
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				
				if (null != cursor) {
					String locationSetting = Utility.getPreferredLocation(getActivity());
					Intent intent = new Intent(getActivity(), DetailActivity.class)
						.setData(WeatherContract.WeatherEntry
								.buildWeatherLocationWithDate(locationSetting, COL_WEATHER_DATE));
					startActivity(intent);
				}
			}
		});
		
		listView.setAdapter(mForecastAdapter);  // Binding ArrayAdapter to ListView
		
        return rootView;
    }
	
	/**
	 * ONACTIVITYCREATED - Initialize Loader with LoaderManager in this callback.
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
		getLoaderManager().initLoader(0, null, this);
		super.onActivityCreated(savedInstanceState);  // From instructor correction
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		// Do not call super on this one
		inflater.inflate(R.menu.forecastfragment, menu );
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.action_refresh:
			updateWeather();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			
		}
		
	}

	/**
	 * UPDATEWEATHER - Updates the weather data with the weather from the location stored in the user's 
	 * 		stored preferences.
	 */
	private void updateWeather() {
		FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity()); 
		String location = Utility.getPreferredLocation(getActivity());
		weatherTask.execute(location); 
	}
	
	/**
	 * ONSTART - Occurs immediately after onCreate()
	 */
	@Override
	public void onStart() {
		super.onStart();
		updateWeather();
	}

	/**
	 * ONCREATELOADER - will call ContentProvider when executed by LoaderManager.
	 * 		Since CursorLoader is from AsyncTasksLoader, work on BG thread.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int anId, Bundle aBundle) {
		//		 	Get data to populate ForecastFragment from DB
		String locationSetting = Utility.getPreferredLocation(getActivity());
		
		// We want sort order ASCending by date
		String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
		Uri weatherForLocationUri = WeatherContract.WeatherEntry
				.buildWeatherLocationWithStartDate(locationSetting
						, System.currentTimeMillis());
		
		return new CursorLoader(
				getActivity(), 
				weatherForLocationUri, 
				ForecastFragment.FORECAST_COLUMNS, 
				null, 
				null, 
				sortOrder);  // ASC usually
	}

	/**
	 * ONLOADFINISHED - Called when Loader completes and data is ready. 
	 * 		Also should make any UI changes you want now that the data is ready.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	    // Swap the new cursor in.  (The framework will take care of closing the
	    // old cursor once we return.)
		mForecastAdapter.swapCursor(arg1);  // Switch to using this cursor		
	}

	/**
	 * ONLOADERRESET - Typically called when Loader destroyed.  Need to remove all references
	 * 		to Loader Data.
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	    // This is called when the last Cursor provided to onLoadFinished()
	    // above is about to be closed.  We need to make sure we are no
	    // longer using it.
		mForecastAdapter.swapCursor(null);  // Release previous cursor so can be properly handled
	}
}
