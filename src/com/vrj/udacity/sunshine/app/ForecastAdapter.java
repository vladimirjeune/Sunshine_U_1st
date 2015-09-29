/**
 * 
 */
package com.vrj.udacity.sunshine.app;

/**
 * @author vladimirjeune
 *
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrj.udacity.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
	
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
	
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /**
     * Remember that these views are reused as needed.
     * Choose the right viewType so we can have the correct layout for the
     *  data.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // Determine layoutId from viewType
        
        switch (viewType) {
        	case VIEW_TYPE_TODAY: {
        		layoutId = R.layout.list_item_forecast_today; 
        		break;
        	}
        	case VIEW_TYPE_FUTURE_DAY: {
        		layoutId = R.layout.list_item_forecast;
        		break;
        	}
        }
        
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    /*
    	This is where we fill-in the views with the contents of the cursor.
   */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	    // our view is pretty simple here --- just a text view
	    // we'll keep the UI functional with a simple (and slow!) binding.
	
	    // Read weather icon ID from cursor
	    int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
	    // Use placeholder image for now
	    ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
	    iconView.setImageResource(R.drawable.ic_launcher);
	
	    // Read date from cursor
	    long dateInMS = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
	    TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textView);
	    dateView.setText(Utility.getFriendlyDayString(context, dateInMS));
	
	    // Read weather forecast from cursor
	    String forecastStr = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
	    TextView forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textView);
	    forecastView.setText(forecastStr);
	
	    // Read user preference for metric or imperial temperature units
	    boolean isMetric = Utility.isMetric(context);
	
	    // Read high temperature from cursor
	    double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
	    TextView highView = (TextView) view.findViewById(R.id.list_item_high_textView);
	    highView.setText(Utility.formatTemperature(high, isMetric));
	
	    // Read low temperature from cursor
	    double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
	    TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
	    lowView.setText(Utility.formatTemperature(low, isMetric));
	
	}
}