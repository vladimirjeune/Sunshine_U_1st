package com.vrj.udacity.sunshine.app;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
			ArrayList<String> weatherForecastList = new ArrayList<String>();
			weatherForecastList.add("Today � Sunny � 83 / 66");
			weatherForecastList.add("Tomorrow � Sunny � 73 / 65");
			weatherForecastList.add("Sunday � Sunny � 63 / 56");
			weatherForecastList.add("Monday � Cloudy � 76 / 46");
			weatherForecastList.add("Tuesday � Partly Cloudy � 59 / 56");
			weatherForecastList.add("Wednesday � Misty � 64 / 61");
			weatherForecastList.add("Thursday � Clear � 80 / 69");
			weatherForecastList.add("Friday � Foggy � 81 / 67");
			weatherForecastList.add("Saturday � Partly Sunny � 78 / 56");
			
			return rootView;
		}
	}
}
