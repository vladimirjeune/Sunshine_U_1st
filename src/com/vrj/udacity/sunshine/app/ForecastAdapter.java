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
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
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
        
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastAdapterViewHolder favh = new ForecastAdapterViewHolder(view);  // Takes layout
        view.setTag(favh);  // Setting ViewHolder as tag so can access whenever needed
        
        return view;
    }

    /*
    	This is where we fill-in the views with the contents of the cursor.
   */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// We are setting the views in the ViewHolder to save rendering time
		ForecastAdapterViewHolder favh = (ForecastAdapterViewHolder) view
				.getTag();  // Getting our ViewHolder out, so we do not have 
							// to re-traverse the View Hierarchy.
				
	    // Read weather icon ID from cursor
	    int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
	    
	    // Need to determine weather to use the ART or ICON image
	    // Since Today, and OTHER DAYS use different images in this List
	    if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
	    	favh.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
	    } else {
	    	favh.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
	    }
	    	
	    // Read date from cursor
	    long dateInMS = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);	    
	    favh.dateView.setText(Utility.getFriendlyDayString(context, dateInMS));
	
	    // Read weather forecast from cursor
	    String forecastStr = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
	    favh.descriptionView.setText(forecastStr);
	
	    // Read user preference for metric or imperial temperature units
	    boolean isMetric = Utility.isMetric(context);
	
	    // Read high temperature from cursor
	    double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
	    favh.highTempView.setText(Utility.formatTemperature(context, high, isMetric));
	
	    // Read low temperature from cursor
	    double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
	    favh.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
	
	}
	
	/**
	 * Cache of the children views for a forecast list item.
	 * This way all ViewGroup traversals are minimized
	 */
	public static class ForecastAdapterViewHolder {
	    public final ImageView iconView;
	    public final TextView dateView;
	    public final TextView descriptionView;
	    public final TextView highTempView;
	    public final TextView lowTempView;

	    public ForecastAdapterViewHolder(View view) {
	        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
	        dateView = (TextView) view.findViewById(R.id.list_item_date_textView);
	        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textView);
	        highTempView = (TextView) view.findViewById(R.id.list_item_high_textView);
	        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
	    }
	}
}