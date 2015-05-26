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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 * @author vladimirjeune
 *
 */
public class ForecastFragment extends Fragment {

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
		
		mForecastAdapter = new ArrayAdapter<String>(
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
		
		// Setting setItemClickListener
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String words = mForecastAdapter.getItem(position);
				
				Log.d(getTag(), words);
				Toast.makeText(getActivity().getApplicationContext(), words, Toast.LENGTH_SHORT).show();
				
				
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
				new FetchWeatherTask().execute("94043");  // String passed into doInBackground()
				return true;
			default:
				return super.onOptionsItemSelected(item);
			
		}
		
	}
	
	/**
	 * FETCHWEATHERTASK - AsyncTasks so .connect() does not step on the UIThread.
	 * You will pass in params[0] to URL and return modified JSON Strings
	 */
	public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

		// Using this makes sure that if you update the name of the class
		// it will throw Exception unless you update it here.  No unintended mismatches.
		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
		
		@Override
		protected String[] doInBackground(String... params) {
			
			// If there is no zipcode there is nothing to look up. Verify params size
			if (params.length < 1) {
				return null;
			}
			
			// These two need to be declared outside the try/catch
	        // so that they can be closed in the finally block.
	        HttpURLConnection urlConnection = null;
	        BufferedReader reader = null;

	        // Will contain the raw JSON response as a string.
	        String forecastJsonStr = null;
	        String[] forecastWeekArr = null;

	        try {
	            // Construct the URL for the OpenWeatherMap query
	            // Possible parameters are available at OWM's forecast API page, at
	            // http://openweathermap.org/API#forecast
	            final String AUTHORITY  = "api.openweathermap.org";
	            final String SCHEME     = "http";
	            final String PATH       = "data/2.5/forecast/daily";
	            final String QKEY_ZIP   = "q";
	            final String QKEY_MODE  = "mode";
	            final String QKEY_UNITS = "units";
	            final String QKEY_CNT   = "cnt";
	            
	            String value_mode   = "json";
	            String value_units  = "metric";
	            String value_cnt    = "7";
	            
	            Uri.Builder uriBuilder = new Uri.Builder();
	            
	            uriBuilder.scheme(SCHEME)
	            	.authority(AUTHORITY)
	            	.path(PATH)
	                .appendQueryParameter(QKEY_ZIP, params[0])
	                .appendQueryParameter(QKEY_MODE, value_mode)
	                .appendQueryParameter(QKEY_UNITS, value_units)
	                .appendQueryParameter(QKEY_CNT, value_cnt);
	            uriBuilder.build();

	            URL url = new URL(uriBuilder.toString());  // Create URL from built URI

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
	            forecastWeekArr = getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(value_cnt));
	            
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "Error ", e);
	            // If the code didn't successfully get the weather data, there's no point in attempting
	            // to parse it.
	            return null;
	        } catch ( NumberFormatException nfe) {
				Log.e(LOG_TAG, "Error", nfe);
				return null;
			} catch (JSONException je) {
				Log.e(LOG_TAG, "Error", je);
				return null;
			} finally{
	            if (urlConnection != null) {
	                urlConnection.disconnect();
	            }
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (final IOException e) {
	                    Log.e(LOG_TAG, "Error closing stream", e);
	                }
	            }
	        }
	        
			return forecastWeekArr;
		}
		
		
        /* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String[] result) {
			if ( result != null ) {
				// Clear out fake results
				mForecastAdapter.clear();
				
				// Add new stuff from parameter.
				// Min API 11 needed. :: // mForecastAdapter.addAll(result);
				for ( String text : result ) {
					mForecastAdapter.add(text);
				}
			}
		}


		/* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }
 
        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
 
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }
 
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {
 
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
 
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
 
            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.
 
            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.
 
            Time dayTime = new Time();
            dayTime.setToNow();
 
            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
 
            // now we work exclusively in UTC
            dayTime = new Time();
 
            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;
 
                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);
 
                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);
 
                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
 
                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);
 
                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }
 
            return resultStrs;
 
        }
		
	}
}
