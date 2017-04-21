package cvut.cz.dp.nss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cvut.cz.dp.nss.rest.RestClient;
import cvut.cz.dp.nss.search.SearchResultWrapper;
import cvut.cz.dp.nss.search.SearchStopTimeWrapper;
import cz.msebera.android.httpclient.Header;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.searchLoadingIndicator);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        List<SearchResultWrapper> results = search(intent.getStringExtra("timeTable"), intent.getStringExtra("stopFrom"),
                intent.getStringExtra("stopTo"), intent.getStringExtra("stopThrough"),
                intent.getStringExtra("date"), intent.getIntExtra("maxTransfers", 3), intent.getBooleanExtra("withWheelChair", false),
                progressBar);


//        // Capture the layout's TextView and set the string as its text
//        TextView textView = (TextView) findViewById(R.id.textView);
//        String s = results.size() + "";
//        textView.setText(s);
    }

    private List<SearchResultWrapper> search(String timeTableId, String stopFrom, String stopTo, String stopThrough,
                                             String date, int maxTransfers, boolean withWheelChair, final ProgressBar progressBar) {
        if(StringUtils.isBlank(timeTableId) || StringUtils.isBlank(stopFrom) || StringUtils.isBlank(stopTo) || StringUtils.isBlank(date)) {
            return new ArrayList<>();
        }

        RequestParams params = new RequestParams();
        params.add("stopFromName", stopFrom);
        params.add("stopToName", stopTo);
        if(!StringUtils.isBlank(stopThrough)) params.add("stopThroughName", stopThrough);
        params.add("departure", date);
        params.add("maxTransfers", maxTransfers + "");
        if(withWheelChair) params.add("withWheelChair", "true");

        final List<SearchResultWrapper> results = new ArrayList<>();

        final SearchResultActivity self = this;

        RestClient.getAsync("x-" + timeTableId + "/search", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {

                    ObjectMapper mapper = new ObjectMapper();
                    for(int i = 0; i < response.length(); i++) {
                        results.add(mapper.readValue(response.getJSONObject(i).toString(), SearchResultWrapper.class));
                    }
                    progressBar.setVisibility(View.GONE);

                    TableLayout table = (TableLayout) findViewById(R.id.result_table);

                    int count = 0;
                    for(SearchResultWrapper result : results) {

                        for(int i = 0; i < result.getStopTimes().size(); i++) {

                            TableRow tableRow = new TableRow(self);
                            tableRow.setMinimumHeight(50);
                            tableRow.setPadding(20, 10, 20, 0);

                            SearchStopTimeWrapper stopTime = result.getStopTimes().get(i);


                            //cas
                            TextView textView = new TextView(self);
                            textView.setPadding(0, 0, 20, 0);
                            textView.setTextSize(22);
                            if(i % 2 == 0) {
                                textView.setText(stopTime.getDeparture());
                            } else {
                                textView.setText(stopTime.getArrival());
                            }
                            tableRow.addView(textView);

                            //stanice
                            TextView textViewStop = new TextView(self);
                            textViewStop.setPadding(0, 0, 30, 0);
                            textViewStop.setTextSize(22);
                            textViewStop.setText(stopTime.getStop().getName());
                            tableRow.addView(textViewStop);

                            TextView textViewRoute = new TextView(self);
                            textViewRoute.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            if(i % 2 == 0) {
                                textViewRoute.setTextSize(16);
                                textViewRoute.setText("METRO");
                            } else {
                                textViewRoute.setTextSize(16);
                                textViewRoute.setText(stopTime.getTrip().getRoute().getShortName());
                            }
                            tableRow.addView(textViewRoute);


                            table.addView(tableRow);
                        }

                        if(++count < results.size()) {
                            TableRow tableRow = new TableRow(self);
                            tableRow.setMinimumHeight(80);
                            table.addView(tableRow);
                        }
                    }

                } catch (Exception e) {
                    //nothing
                    int i = 0;
                }

                int i = 0;
            }

//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                super.onSuccess(statusCode, headers, responseString);
//            }

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

        return results;
    }

}
