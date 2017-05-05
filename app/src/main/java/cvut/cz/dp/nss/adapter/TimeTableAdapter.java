package cvut.cz.dp.nss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cvut.cz.dp.nss.R;
import cvut.cz.dp.nss.rest.RestClient;
import cz.msebera.android.httpclient.Header;

/**
 * Adapter pro selectbox s jizdnimy rady
 *
 * @author jakubchalupa
 * @since 21.04.17
 */
public class TimeTableAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> resultList;
    private Map<String, String> resultMap;

    public TimeTableAdapter(Context context) {
        mContext = context;
        resultMap = getTimeTables();
        resultList = new ArrayList<>(resultMap.keySet());
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

    public Map<String, String> getItems() {
        return resultMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.spinner_one_item)).setText(getItem(position));

        return convertView;
    }


    /**
     * dotahne jizdni rady pres rest
     * @return seznam jizdnich radu (name -> id)
     */
    private Map<String, String> getTimeTables() {
        final Map<String, String> stops = new LinkedHashMap<>();

        RestClient.getSync("timeTable", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for(int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        stops.put(jsonObject.getString("name"), jsonObject.getString("id"));
                    }
                } catch (JSONException e) {
                    //nothing
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

        return stops;
    }

}
