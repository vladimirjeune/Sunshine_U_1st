package com.vrj.udacity.sunshine.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			String[] forecastArray = {
					"Today Ð Sunny Ð 83 / 66",
					"Tomorrow Ð Sunny Ð 73 / 65",
					"Sunday Ð Sunny Ð 63 / 56",
					"Monday Ð Cloudy Ð 76 / 46",
					"Tuesday Ð Partly Cloudy Ð 59 / 56",
					"Wednesday Ð Misty Ð 64 / 61",
					"Thursday Ð Clear Ð 80 / 69",
					"Friday Ð Foggy Ð 81 / 67",
					"Saturday Ð Partly Sunny Ð 78 / 56"
					
			};
			
			List<String> weekforecast = new ArrayList<String>(
					Arrays.asList(forecastArray));
			
			ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>(
					// The current context (this fragment's parent activity.)
					this.getActivity(),
					// ID of list item layout
					R.layout.list_item_forecast,
					// ID of the textview to populate
					R.id.list_item_forecast_textview,
					// Forecast data as a list
					weekforecast);

			return rootView;
		}
	}
}
