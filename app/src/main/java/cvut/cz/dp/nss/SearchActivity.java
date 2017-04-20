package cvut.cz.dp.nss;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

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
        dateView.setText(getDateString(new GregorianCalendar()));

        //policko aktualne vybraneho casu
        final TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(getTimeString(new GregorianCalendar()));

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
        dialogDateView.findViewById(R.id.date_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //a po vyberu data se nasetuje do naseho zobrazovaciho policka
                DatePicker datePicker = (DatePicker) dialogDateView.findViewById(R.id.date_picker);
                dateView.setText(getDateString(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
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
                timeView.setText(getTimeString(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                alertTimeDialog.dismiss();
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
        Intent intent = new Intent(this, SearchResultActivity.class);
        EditText editText = (EditText) findViewById(R.id.stopFrom);
        String message = editText.getText().toString();
        intent.putExtra("stopFrom", message);
        startActivity(intent);
    }

    private String getDateString(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
    }

    private String getDateString(int year, int month, int day) {
        return day + "." + month + "." + year;
    }

    private String getTimeString(Calendar calendar) {
        return getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    private String getTimeString(int hour, int minute) {
        return hour + ":" + (minute < 10 ? "0" + minute : minute);
    }

}
