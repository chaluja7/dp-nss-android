package cvut.cz.dp.nss;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import cvut.cz.dp.nss.autocomplete.DelayAutoCompleteTextView;
import cvut.cz.dp.nss.autocomplete.StopAutoCompleteAdapter;
import cvut.cz.dp.nss.search.SearchParam;
import cvut.cz.dp.nss.util.DateTimeUtil;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class SearchActivity extends AppCompatActivity {

    private static final int THRESHOLD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //nastavime locale
        Locale locale = new Locale("cs_CZ");
        Locale.setDefault(locale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //policko aktualne vybraneho datumu
        final TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(DateTimeUtil.getDateString(new GregorianCalendar()));

        //policko aktualne vybraneho casu
        final TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(DateTimeUtil.getTimeString(new GregorianCalendar()));

        //vytvoreni dateTimePicker dialogu
        final View dialogDateView = View.inflate(this, R.layout.date_picker, null);
        final View dialogTimeView = View.inflate(this, R.layout.time_picker, null);
        final AlertDialog alertDateDialog = new AlertDialog.Builder(this).create();
        final AlertDialog alertTimeDialog = new AlertDialog.Builder(this).create();
        alertDateDialog.setView(dialogDateView);
        alertTimeDialog.setView(dialogTimeView);

        //po kliknuti na datum se otevre dialog pro vyber data
        this.findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDateDialog.show();
            }
        });

        //po kliknuti na cas se otevre dialog vyberu casu (24 hodinovy)
        this.findViewById(R.id.time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertTimeDialog.show();
            }
        });

        //udalost po vybrani data z dialogu
        final DatePicker datePicker = (DatePicker) dialogDateView.findViewById(R.id.date_picker);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePicker.getCalendarView().setFirstDayOfWeek(Calendar.MONDAY);
        } else {
            datePicker.setFirstDayOfWeek(Calendar.MONDAY);
        }
        dialogDateView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //a po vyberu data se nasetuje do naseho zobrazovaciho policka
                dateView.setText(DateTimeUtil.getDateString(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
                alertDateDialog.dismiss();
            }
        });

        //udalost po vyberu casu z dialogu
        final TimePicker timePicker = (TimePicker) dialogTimeView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dialogTimeView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //a po vyberu casu se nasetuje do naseho zobrazovaciho policka
                timeView.setText(DateTimeUtil.getTimeString(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                alertTimeDialog.dismiss();
            }
        });


        //naseptavac stanice z
        final DelayAutoCompleteTextView stopFromSearch = (DelayAutoCompleteTextView) findViewById(R.id.stopFrom);
        stopFromSearch.setThreshold(THRESHOLD);
        stopFromSearch.setAdapter(new StopAutoCompleteAdapter(this));
        stopFromSearch.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.stopFromLoadingIndicator));
        stopFromSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String stop = (String) adapterView.getItemAtPosition(position);
                stopFromSearch.setText(stop);
            }
        });

        //naseptavac stanice do
        final DelayAutoCompleteTextView stopToSearch = (DelayAutoCompleteTextView) findViewById(R.id.stopTo);
        stopToSearch.setThreshold(THRESHOLD);
        stopToSearch.setAdapter(new StopAutoCompleteAdapter(this));
        stopToSearch.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.stopToLoadingIndicator));
        stopToSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String stop = (String) adapterView.getItemAtPosition(position);
                stopToSearch.setText(stop);
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void submitForm(View view) {
        DelayAutoCompleteTextView stopFrom = (DelayAutoCompleteTextView) findViewById(R.id.stopFrom);
        DelayAutoCompleteTextView stopTo = (DelayAutoCompleteTextView) findViewById(R.id.stopTo);

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchParam.TIME_TABLE.getValue(), "pid");
        intent.putExtra(SearchParam.STOP_FROM.getValue(), stopFrom.getText().toString());
        intent.putExtra(SearchParam.STOP_TO.getValue(), stopTo.getText().toString());
        intent.putExtra(SearchParam.STOP_THROUGH.getValue(), "");
        intent.putExtra(SearchParam.DATE.getValue(), "16.3.2017 15:00");
        intent.putExtra(SearchParam.MAX_TRANSFERS.getValue(), 3);
        intent.putExtra(SearchParam.WITH_WHEELCHAIR.getValue(), false);
        intent.putExtra(SearchParam.BY_ARRIVAL.getValue(), false);

        startActivity(intent);
    }

    /**
     * prohodi stanice odkud a kam
     * @param view view
     */
    public void swapStops(View view) {
        DelayAutoCompleteTextView stopFrom = (DelayAutoCompleteTextView) findViewById(R.id.stopFrom);
        DelayAutoCompleteTextView stopTo = (DelayAutoCompleteTextView) findViewById(R.id.stopTo);
        String stopFromName = stopFrom.getText().toString();

        //a nechci pri prohozeni spustit filtering
        stopFrom.setText(stopTo.getText(), false);
        stopTo.setText(stopFromName, false);
    }

}
