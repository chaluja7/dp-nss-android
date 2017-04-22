package cvut.cz.dp.nss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cvut.cz.dp.nss.R;
import cvut.cz.dp.nss.SearchActivity;
import cvut.cz.dp.nss.rest.RestClient;
import cz.msebera.android.httpclient.Header;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class StopAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private SearchActivity mContext;
    private List<String> resultList = new ArrayList<>();

    public StopAutoCompleteAdapter(SearchActivity context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    //vyber stanice z aktualne vybraneho jizdniho radu
                    final Spinner spinner = (Spinner) mContext.findViewById(R.id.timetable_spinner);
                    List<String> stops = getStops(mContext.getTimeTableMap().get(spinner.getSelectedItem().toString()), constraint.toString());

                    filterResults.values = stops;
                    filterResults.count = stops.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
    }

    /**
     * dotazne stops pres rest
     * @param timeTableId id jizdniho radu
     * @param stopStartsWith pattern stanice
     * @return seznam stanic dle patternu
     */
    private List<String> getStops(String timeTableId, String stopStartsWith) {
        RequestParams params = new RequestParams("startsWith", stopStartsWith);
        final List<String> stops = new ArrayList<>();

        RestClient.getSync("x-" + timeTableId + "/stop", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for(int i = 0; i < timeline.length(); i++) {
                        stops.add((String) timeline.get(i));
                    }
                } catch (JSONException e) {
                    //nothing
                }
            }
        });

        return stops;
    }

}
