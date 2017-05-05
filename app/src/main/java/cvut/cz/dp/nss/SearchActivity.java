package cvut.cz.dp.nss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import cvut.cz.dp.nss.adapter.StopAutoCompleteAdapter;
import cvut.cz.dp.nss.adapter.TimeTableAdapter;
import cvut.cz.dp.nss.search.SearchParam;
import cvut.cz.dp.nss.util.DateTimeUtil;
import cvut.cz.dp.nss.view.DelayAutoCompleteTextView;

/**
 * Aktivita (obrazovka) vyhledavani spoju
 *
 * @author jakubchalupa
 * @since 21.04.17
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * min pocet znaku pro zapoceti vyhledavani stanic v naseptavaci
     */
    private static final int THRESHOLD = 3;

    /**
     * name -> id
     */
    private Map<String, String> timeTableMap;

    /**
     * dialog s nastavenim aplikace
     */
    private View dialogOptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //nastavime locale
        Locale locale = new Locale("cs_CZ");
        Locale.setDefault(locale);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //v hlavnim vlakne bude pozadavek na jizdni rady, opravdu to tak chceme, bez toho nema cenu
        //cokoliv dalsiho vykreslovat
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDateDialog.show();
            }
        });

        //po kliknuti na cas se otevre dialog vyberu casu (24 hodinovy)
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertTimeDialog.show();
            }
        });

        //udalost po vybrani data z dialogu
        final DatePicker datePicker = (DatePicker) dialogDateView.findViewById(R.id.date_picker);
        dialogDateView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //a po vyberu data se nasetuje do naseho zobrazovaciho policka
                dateView.setText(DateTimeUtil.getDateString(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
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

        //dialog s nastavenim
        dialogOptionView = View.inflate(this, R.layout.options_window, null);
        final AlertDialog alertOptionDialog = new AlertDialog.Builder(this).create();
        alertOptionDialog.setView(dialogOptionView);

        //po kliknuti na ikonku nastaveni se otevre dialog nastaveni
        this.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertOptionDialog.show();
            }
        });

        //vyber z moznych poctu prestupu
        Spinner maxTransfersSpinner = (Spinner) dialogOptionView.findViewById(R.id.maxTransfers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.maxTransfers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxTransfersSpinner.setAdapter(adapter);
        maxTransfersSpinner.setSelection(3);

        //selectbox pro vyber jizdniho radu
        final Spinner spinner = (Spinner) findViewById(R.id.timetable_spinner);
        final TimeTableAdapter timeTableAdapter = new TimeTableAdapter(this);
        spinner.setAdapter(timeTableAdapter);
        timeTableMap = timeTableAdapter.getItems();
        if(timeTableMap.isEmpty()) {
            //nepodarilo se nacist jizdni rady, takze zobrazim chybu a schovam formular
            findViewById(R.id.search_form).setVisibility(View.GONE);
            findViewById(R.id.serverErrorMessage).setVisibility(View.VISIBLE);
            return;
        }

        //naseptavac stanice z
        final DelayAutoCompleteTextView stopFromSearch = (DelayAutoCompleteTextView) findViewById(R.id.stopFrom);
        stopFromSearch.setThreshold(THRESHOLD);
        stopFromSearch.setAdapter(new StopAutoCompleteAdapter(this));
        stopFromSearch.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.stopFromLoadingIndicator));
        stopFromSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String stop = (String) adapterView.getItemAtPosition(position);
                stopFromSearch.setText(stop, false);
                hideKeyboard();
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
                stopToSearch.setText(stop, false);
                hideKeyboard();
            }
        });

        //naseptavac stanice pres
        final DelayAutoCompleteTextView stopThroughSearch = (DelayAutoCompleteTextView) findViewById(R.id.stopThrough);
        stopThroughSearch.setThreshold(THRESHOLD);
        stopThroughSearch.setAdapter(new StopAutoCompleteAdapter(this));
        stopThroughSearch.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.stopThroughLoadingIndicator));
        stopThroughSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String stop = (String) adapterView.getItemAtPosition(position);
                stopThroughSearch.setText(stop, false);
                hideKeyboard();
            }
        });

    }

    /**
     * odesle vyhledavaci formular a prejde na vysledky vyhledavani
     * @param view view
     */
    public void submitForm(View view) {
        //sesbiram data z jednotlivych komponent
        final DelayAutoCompleteTextView stopFrom = (DelayAutoCompleteTextView) findViewById(R.id.stopFrom);
        final DelayAutoCompleteTextView stopTo = (DelayAutoCompleteTextView) findViewById(R.id.stopTo);
        final DelayAutoCompleteTextView stopThrough = (DelayAutoCompleteTextView) findViewById(R.id.stopThrough);
        final Spinner spinner = (Spinner) findViewById(R.id.timetable_spinner);
        final RadioGroup radioButtons = (RadioGroup) findViewById(R.id.radioButtons);
        final Spinner maxTransfersSpinner = (Spinner) dialogOptionView.findViewById(R.id.maxTransfers_spinner);
        final CheckBox wheelChair = (CheckBox) dialogOptionView.findViewById(R.id.wheelChairCheck);
        final TextView dateView = (TextView) findViewById(R.id.date);
        final TextView timeView = (TextView) findViewById(R.id.time);

        //0 = odjezd, 1 = prijezd
        final int checkedIndex = radioButtons.indexOfChild(radioButtons.findViewById(radioButtons.getCheckedRadioButtonId()));

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchParam.TIME_TABLE.getValue(), timeTableMap.get(spinner.getSelectedItem().toString()));
        intent.putExtra(SearchParam.STOP_FROM.getValue(), stopFrom.getText().toString());
        intent.putExtra(SearchParam.STOP_TO.getValue(), stopTo.getText().toString());
        intent.putExtra(SearchParam.STOP_THROUGH.getValue(), stopThrough.getText().toString());
        intent.putExtra(SearchParam.DATE.getValue(), dateView.getText().toString() + " " + timeView.getText().toString());
        intent.putExtra(SearchParam.MAX_TRANSFERS.getValue(), Integer.parseInt(maxTransfersSpinner.getSelectedItem().toString()));
        intent.putExtra(SearchParam.WITH_WHEELCHAIR.getValue(), wheelChair.isChecked());
        intent.putExtra(SearchParam.BY_ARRIVAL.getValue(), checkedIndex == 1);

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

    /**
     * prida/odebere stanci pres
     * @param view view
     */
    public void toogleThroughStop(View view) {
        DelayAutoCompleteTextView stopThrough = (DelayAutoCompleteTextView) findViewById(R.id.stopThrough);
        ImageButton imageButton = (ImageButton) findViewById(R.id.button3);

        int currentVisibility = stopThrough.getVisibility();
        if(View.VISIBLE == currentVisibility) {
            stopThrough.setVisibility(View.GONE);
            stopThrough.setText("", false);
            imageButton.setBackgroundResource(R.drawable.ic_control_point_black_48dp);
        } else {
            stopThrough.setVisibility(View.VISIBLE);
            imageButton.setBackgroundResource(R.drawable.ic_highlight_off_black_48dp);
        }
    }

    public Map<String, String> getTimeTableMap() {
        return timeTableMap;
    }

    /**
     * schova sw klavesnici z obrazovky teto aktivity
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
