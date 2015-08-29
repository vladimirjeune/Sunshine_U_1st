/**
 * 
 */
package com.vrj.udacity.sunshine.app;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
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
	private ArrayAdapter<String> mForecastAdapter = null;
	
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

		// This is the root of the hierarchy.  No need to get yourself.
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		mForecastAdapter = new ArrayAdapter<String>(
				// The current context (this fragment's parent activity.)
				this.getActivity(),
				// ID of list item layout
				R.layout.list_item_forecast,
				// ID of the textview to populate
				R.id.list_item_forecast_textview,
				// Forecast data as a list
				new ArrayList<String>());
		
		// From the root of the Layout Hierarchy find the element you are looking for.
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(mForecastAdapter);  // Binding ArrayAdapter to ListView
		
		// Setting setItemClickListener to show detail of item clicked.
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Got the string of the clicked item from mForecastAdapter
				String forecast = mForecastAdapter.getItem(position);
				
				// Used getActivity() as the context, and used the correct words.  
				// Gets Activity this Fragment is associated with.
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				
				// Msg to send with activity as Key:Value pair (string,forecast)
				intent.putExtra(Intent.EXTRA_TEXT, forecast);
				
				// Have associated MainActivity start the DetailActivity with the forecast string as extra
				startActivity(intent);
			}
		});

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
		// MUST use PREFERENCEMANAGER.getDEFAULTSharedPreferences, will get DEFAULT preference file for this Context
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		// Using user's DefaultSharedPrefs location for this context that you created, instead of hardcoded number.
		String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
		new FetchWeatherTask(getActivity(), mForecastAdapter).execute(location);  // String passed into doInBackground()
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
