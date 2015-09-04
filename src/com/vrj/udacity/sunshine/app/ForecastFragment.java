/**
 * 
 */
package com.vrj.udacity.sunshine.app;

import java.util.ArrayList;

import com.vrj.udacity.sunshine.app.data.WeatherContract;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 * @author vladimirjeune
 *
 */
public class ForecastFragment extends Fragment {
	// Package name ensures keys are unique in case interacts with other apps.
	public final static String EXTRA_MESSAGE ="com.vrj.udacity.sunshine.app.MESSAGE";
	private ForecastAdapter mForecastAdapter = null;
	
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

		
		// Get data to populate ForecastFragment from DB
		String locationSetting = Utility.getPreferredLocation(getActivity());
		
		// We want sort order ASCending by date
		String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
		Uri weatherForLocationUri = WeatherContract.WeatherEntry
				.buildWeatherLocationWithStartDate(locationSetting
						, System.currentTimeMillis());
		
		Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri
				, null, null, null, sortOrder);
		
		// 0, no flags set
		// The CursorAdapter will take data from our cursor and populate the ListView
		// However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
		// up with an empty list the first time we run.
		mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

		// This is the root of the hierarchy.  No need to get yourself.
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

		// From the root of the Layout Hierarchy find the element you are looking for.
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(mForecastAdapter);  // Binding ArrayAdapter to ListView
		
        return rootView;
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
}
