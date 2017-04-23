package cvut.cz.dp.nss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cvut.cz.dp.nss.rest.RestClient;
import cvut.cz.dp.nss.search.SearchParam;
import cvut.cz.dp.nss.search.SearchResultWrapper;
import cvut.cz.dp.nss.search.SearchStopTimeWrapper;
import cvut.cz.dp.nss.util.DateTimeUtil;
import cz.msebera.android.httpclient.Header;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class SearchResultActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private Button prevButton;

    private Button nextButton;

    private TextView errorMessage;

    private LinearLayout table;

    private List<SearchResultWrapper> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        progressBar = (ProgressBar) findViewById(R.id.searchLoadingIndicator);
        progressBar.setVisibility(View.VISIBLE);

        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        table = (LinearLayout) findViewById(R.id.result_table);

        final Intent intent = getIntent();
        searchResults = search(intent.getStringExtra(SearchParam.TIME_TABLE.getValue()),
                intent.getStringExtra(SearchParam.STOP_FROM.getValue()),
                intent.getStringExtra(SearchParam.STOP_TO.getValue()), intent.getStringExtra(SearchParam.STOP_THROUGH.getValue()),
                intent.getStringExtra(SearchParam.DATE.getValue()), intent.getIntExtra(SearchParam.MAX_TRANSFERS.getValue(), 3),
                intent.getBooleanExtra(SearchParam.WITH_WHEELCHAIR.getValue(), false),
                intent.getBooleanExtra(SearchParam.BY_ARRIVAL.getValue(), false));
    }

    /**
     * provede rest hledani spoju a po nalezeni je zobrazi na obrazovce
     *
     * @param timeTableId id jizdniho radu
     * @param stopFrom stanice z
     * @param stopTo stanice do
     * @param stopThrough stanice pres
     * @param date datum a cas
     * @param maxTransfers max pocet prestupu
     * @param withWheelChair pouze bezbarierove?
     * @param byArrival hledam dle casu prijezdu?
     * @return nalezene vysledky
     */
    private List<SearchResultWrapper> search(String timeTableId, String stopFrom, String stopTo, String stopThrough,
                                             String date, int maxTransfers, boolean withWheelChair,
                                             boolean byArrival) {

        if(StringUtils.isBlank(timeTableId) || StringUtils.isBlank(stopFrom) || StringUtils.isBlank(stopTo) || StringUtils.isBlank(date)) {
            progressBar.setVisibility(View.GONE);
            return new ArrayList<>();
        }

        //priprava parametru pro http request
        RequestParams params = new RequestParams();
        params.add(SearchParam.STOP_FROM.getValue(), stopFrom);
        params.add(SearchParam.STOP_TO.getValue(), stopTo);
        if(!StringUtils.isBlank(stopThrough)) params.add(SearchParam.STOP_THROUGH.getValue(), stopThrough);
        params.add(SearchParam.DATE.getValue(), date);
        params.add(SearchParam.MAX_TRANSFERS.getValue(), maxTransfers + "");
        if(withWheelChair) params.add(SearchParam.WITH_WHEELCHAIR.getValue(), "true");
        if(byArrival) params.add(SearchParam.BY_ARRIVAL.getValue(), "true");

        final List<SearchResultWrapper> results = new ArrayList<>();
        final SearchResultActivity self = this;

        RestClient.getAsync("x-" + timeTableId + "/search", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    //ziskam vsechny vysledky hledani
                    ObjectMapper mapper = new ObjectMapper();
                    for(int i = 0; i < response.length(); i++) {
                        results.add(mapper.readValue(response.getJSONObject(i).toString(), SearchResultWrapper.class));
                    }
                    //vypnu tocici se kolecko
                    progressBar.setVisibility(View.GONE);

                    //najdu si layout, kam vykresluju vysledky
                    int count = 0;
                    for(SearchResultWrapper result : results) {

                        //pro kazdy nalezeny spoj pridavam novy prvek na obrazovku
                        LayoutInflater inflater = (LayoutInflater) self.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View convertView = inflater.inflate(R.layout.result_search_item, table, false);

                        for(int i = 0; i < result.getStopTimes().size(); i++) {
                            SearchStopTimeWrapper stopTime = result.getStopTimes().get(i);

                            if(i % 2 == 0) {
                                //prvni zastaveni spoje (vyjezd)
                                inflater = (LayoutInflater) self.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                convertView = inflater.inflate(R.layout.result_search_item, table, false);

                                ((TextView) convertView.findViewById(R.id.time1)).setText(DateTimeUtil.getTimeWithoutSeconds(stopTime.getDeparture()));
                                ((TextView) convertView.findViewById(R.id.stop1)).setText(stopTime.getStop().getName());
                                final Integer imageResource = getImageResource(stopTime.getTrip().getRoute().getTypeCode());
                                if(imageResource != null) {
                                    ((ImageView) convertView.findViewById(R.id.routeImage)).setImageResource(imageResource);
                                }
                                ((TextView) convertView.findViewById(R.id.routeName)).setText(stopTime.getTrip().getRoute().getShortName());
                            } else {
                                //druhe zastaveni spoje (prijezd)
                                ((TextView) convertView.findViewById(R.id.time2)).setText(DateTimeUtil.getTimeWithoutSeconds(stopTime.getArrival()));
                                ((TextView) convertView.findViewById(R.id.stop2)).setText(stopTime.getStop().getName());
                                table.addView(convertView);
                            }

                        }

                        //informace o cele ceste
                        TextView infoView = (TextView) convertView.findViewById(R.id.info);
                        final int minutes = (int) Math.ceil(result.getTravelTime() / 60.0);
                        infoView.setText("Celkový čas jízdy: " + minutes + DateTimeUtil.getMinutesLabel(minutes));
                        infoView.setVisibility(View.VISIBLE);

                        //oddelovac nalezenych vysledku
                        if(++count < results.size()) {
                            convertView.findViewById(R.id.delim).setVisibility(View.VISIBLE);
                        }
                    }

                    if(!results.isEmpty()) {
                        //vse probehlo v poradku, takze zobrazim tlacitka na predchozi a pristi spoje
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                    } else {
                        //nenalezeny zadne vysledky
                        findViewById(R.id.noResultsMessage).setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    errorMessage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        return results;
    }

    /**
     * najde dalsi spoje misto jiz nalezenych
     */
    public void findNext(View view) {
        //schovam tlacitka prev a next
        prevButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        //pokud momentalne nemam zadne vysledky tak nemuzu hledat ani predchozi
        if(searchResults.isEmpty()) {
            return;
        }

        //zobrazim indikator nacitani
        progressBar.setVisibility(View.VISIBLE);
        //smazu vsechny jiz nalezene vysledky
        table.removeAllViews();

        //upravim datum hledani tak, aby byl o minutu vyssi, nez vyjezd naposledy nalezeneho vysledku
        final Intent intent = getIntent();
        SearchResultWrapper searchResultWrapper = searchResults.get(searchResults.size() - 1);

        //ziskam datum a cas vyjezdu posledniho nalezeneho vysledku
        String departureDate = searchResultWrapper.getDepartureDate();
        String departureTime = searchResultWrapper.getStopTimes().get(0).getDeparture();
        //pokud ma departureTime vteriny tak se jich zbavim
        String[] split = departureTime.split(":");
        if(split.length > 2) {
            departureTime = split[0] + ":" + split[1];
        }

        //vezmu departure, prevedu na datum a pridam minutu
        String departure = departureDate + " " + departureTime;
        DateTime dateTime = DateTimeUtil.JODA_DATE_TIME_FORMATTER.parseDateTime(departure);
        dateTime = dateTime.plusMinutes(1);

        //a potom prevedu zpatky na string coz bude parametr pro nove vyhledavani
        departure = dateTime.toString(DateTimeUtil.JODA_DATE_TIME_FORMATTER);

        //a provedu vyhledavani dle casu odjezdu
        searchResults = search(intent.getStringExtra(SearchParam.TIME_TABLE.getValue()),
                intent.getStringExtra(SearchParam.STOP_FROM.getValue()),
                intent.getStringExtra(SearchParam.STOP_TO.getValue()),
                intent.getStringExtra(SearchParam.STOP_THROUGH.getValue()),
                departure,
                intent.getIntExtra(SearchParam.MAX_TRANSFERS.getValue(), 3),
                intent.getBooleanExtra(SearchParam.WITH_WHEELCHAIR.getValue(), false), false);
    }

    /**
     * @param routeType kod typu spoje
     * @return obrazek k tomuto typu spoje. null, pokud neexistuje
     */
    private static Integer getImageResource(Integer routeType) {
        if(routeType == null) return null;

        switch(routeType) {
            case 0:
                return R.drawable.tram_p;
            case 1:
                return R.drawable.metro_p;
            case 2:
                return R.drawable.train_p;
            case 3:
                return R.drawable.bus_p;
            default:
                return null;
        }
    }

}
