/**
 * 
 */
package com.vrj.udacity.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 * @author vladimirjeune
 *
 */
public class ForecastFragment extends Fragment {

	public ForecastFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// This is the root of the hierarchy.  No need to get yourself.
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
		
		ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
				// The current context (this fragment's parent activity.)
				this.getActivity(),
				// ID of list item layout
				R.layout.list_item_forecast,
				// ID of the textview to populate
				R.id.list_item_forecast_textview,
				// Forecast data as a list
				weekforecast);
		
		// From the root of the Layout Hierarchy find the element you are looking for.
		ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
		listView.setAdapter(mForecastAdapter);  // Binding ArrayAdapter to ListView

        return rootView;
    }
	
	/**
	 * FETCHWEATHERTASK - AsyncTasks so .connect() does not step on the UIThread.
	 * You will pass in params[0] to URL and return modified JSON Strings
	 */
	public class FetchWeatherTask extends AsyncTask<URL, Void, String[]>{

		@Override
		protected String[] doInBackground(URL... params) {
			// These two need to be declared outside the try/catch
	        // so that they can be closed in the finally block.
	        HttpURLConnection urlConnection = null;
	        BufferedReader reader = null;

	        // Will contain the raw JSON response as a string.
	        String forecastJsonStr = null;

	        try {
	            // Construct the URL for the OpenWeatherMap query
	            // Possible parameters are available at OWM's forecast API page, at
	            // http://openweathermap.org/API#forecast
	            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

	            // Create the request to OpenWeatherMap, and open the connection
	            urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.setRequestMethod("GET");
	            urlConnection.connect();  // You cannot do this on main thread.  Causes android.os.NetworkOnMainThreadException

	            // Read the input stream into a String
	            InputStream inputStream = urlConnection.getInputStream();
	            StringBuffer buffer = new StringBuffer();
	            if (inputStream == null) {
	                // Nothing to do.
	                return null;
	            }
	            reader = new BufferedReader(new InputStreamReader(inputStream));

	            String line;
	            while ((line = reader.readLine()) != null) {
	                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
	                // But it does make debugging a *lot* easier if you print out the completed
	                // buffer for debugging.
	                buffer.append(line + "\n");
	            }

	            if (buffer.length() == 0) {
	                // Stream was empty.  No point in parsing.
	                return null;
	            }
	            forecastJsonStr = buffer.toString();
	        } catch (IOException e) {
	            Log.e("PlaceholderFragment", "Error ", e);
	            // If the code didn't successfully get the weather data, there's no point in attemping
	            // to parse it.
	            return null;
	        } finally{
	            if (urlConnection != null) {
	                urlConnection.disconnect();
	            }
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (final IOException e) {
	                    Log.e("PlaceholderFragment", "Error closing stream", e);
	                }
	            }
	        }
	        
			return null;
		}
		
	}
}
