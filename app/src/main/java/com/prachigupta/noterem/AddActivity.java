package com.prachigupta.noterem;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    AlertDialog dialog;
    String type;
    List<CheckListItem> list;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RelativeLayout relative;
    EditText eTitle;
    EditText ecTitle;
    EditText eContent;
    ActionBar bar;
    ListView lvChecklistAdd;
    Button bAddItem;
    EditText eAddItem;
    TextView tvNext;
    TextView tvCancel;
    TextView tvOk;
    TextView tvDate;
    TextView tvTime;
    RadioGroup rgFrequency;
    ScrollView svAddMain;

    RadioButton rbOnce;
    RadioButton rbDaily;
    RadioButton rbMonthly;
    RadioButton rbYearly;

    NoteEntry entry;
    GeoFencing geo;
    CheckListAdapter adapter;

    int index;

    boolean isEdit;

    final Calendar calendar = Calendar.getInstance();
    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = -1;

        Intent intent = getIntent();
        type = intent.getStringExtra(Constant.INTENT_ADD);
        if (intent.getStringExtra(Constant.EDIT) != null &&
                intent.getStringExtra(Constant.EDIT).equals(Constant.EDIT)) {
            int id = intent.getIntExtra(Constant.ID, 0);
            entry = databaseHandler.getEntry(id);
            geo = databaseHandler.getEntryGeoByNoteId(entry.get_id());
            isEdit = true;
        } else {
            isEdit = false;
        }
        bar = getSupportActionBar();

        switch (type) {
            case Constant.TEXT:
                setContentView(R.layout.activity_add_text);
                if (bar != null) {
                    if (isEdit) {
                        bar.setTitle("Edit Notes");
                    } else {
                        bar.setTitle("Add Notes");
                    }
                }
                eTitle = (EditText) findViewById(R.id.eTitle);
                eContent = (EditText) findViewById(R.id.eContent);
                if (isEdit) {
                    eTitle.setText(entry.get_title());
                    eContent.setText(entry.get_content());
                }
                break;

            case Constant.CHECKLIST:
                setContentView(R.layout.activity_add_check_list);
                if (bar != null) {
                    if (isEdit) {
                        bar.setTitle("Edit Check List");
                    } else {
                        bar.setTitle("Add Check List");
                    }
                }
                lvChecklistAdd = (ListView) findViewById(R.id.lvChecklistAdd);
                bAddItem = (Button) findViewById(R.id.bAddChecklistItem);
                ecTitle = (EditText) findViewById(R.id.ecTitle);
                if (isEdit) {
                    ecTitle.setText(entry.get_title());
                    list = databaseHandler.databaseToListCheckList(entry.get_id());
                } else {

                    list = new ArrayList<>();
                }
                break;

            case Constant.REMINDER:
                setContentView(R.layout.activity_add_reminder);
                if (bar != null) {
                    if (isEdit) {
                        bar.setTitle("Edit Reminder");
                    } else {
                        bar.setTitle("Add Reminder");
                    }
                }
                eContent = (EditText) findViewById(R.id.eNotes);
                tvDate = (TextView) findViewById(R.id.tvDate);
                tvTime = (TextView) findViewById(R.id.tvTime);
                rgFrequency = (RadioGroup) findViewById(R.id.rgFrequency);
                if (isEdit) {
                    eContent.setText(entry.get_content());
                    tvDate.setText(entry.get_reminderDate());
                    tvTime.setText(entry.get_reminderTime());
                    rbOnce = (RadioButton) findViewById(R.id.rbOnce);
                    rbDaily = (RadioButton) findViewById(R.id.rbDaily);
                    rbMonthly = (RadioButton) findViewById(R.id.rbMonthly);
                    rbYearly = (RadioButton) findViewById(R.id.rbYearly);
                    switch (entry.get_frequency()) {
                        case Constant.ONCE:

                            rbOnce.setChecked(true);
                            break;
                        case Constant.DAILY:
                            rbDaily.setChecked(true);
                            break;
                        case Constant.MONTHLY:
                            rbMonthly.setChecked(true);
                            break;
                        case Constant.YEARLY:
                            rbYearly.setChecked(true);
                            break;
                    }

                } else {
                    tvDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                            (calendar.get(Calendar.MONTH) + 1) + "/" +
                            calendar.get(Calendar.YEAR));

                    tvTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                            calendar.get(Calendar.MINUTE));
                }
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        relative = (RelativeLayout) findViewById(R.id.rlAddMain);
        svAddMain = (ScrollView) findViewById(R.id.svAddMain);
        if (isEdit) {

            colorChange(entry.get_color());
        } else {
            colorChange();
        }
        if (type.equals(Constant.CHECKLIST)) {
            if (list.size() > 0) {

                adapter = new CheckListAdapter(this, list);
                lvChecklistAdd.setAdapter(adapter);
                Utils.getListViewSize(lvChecklistAdd);

                lvChecklistAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Dialog(AddActivity.this);
                        index = position;
                        Log.i("MainActivity", "Index of List Item clicked" + index + "");
                        eAddItem.setText(list.get(position).get_itemName());
                    }
                });
            }
        }
    }

    public void Dialog(Context context) {
        dialog = new AlertDialog.Builder(context)
                .setView(R.layout.add_item_custom_dialog)
                .create();
        dialog.show();

        tvNext = (TextView) dialog.findViewById(R.id.tvNext);
        tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        eAddItem = (EditText) dialog.findViewById(R.id.eAddItem);

    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }

    public void colorClick(View view) {

        switch (view.getId()) {

            case R.id.bRed:
                puttingValuesInSharedPreference(Constant.RED);
                break;
            case R.id.bBlue:
                puttingValuesInSharedPreference(Constant.BLUE);
                break;
            case R.id.bBrown:
                puttingValuesInSharedPreference(Constant.BROWN);
                break;
            case R.id.bGreen:
                puttingValuesInSharedPreference(Constant.GREEN);
                break;
            case R.id.bGrey:
                puttingValuesInSharedPreference(Constant.GRAY);
                break;
            case R.id.bIndigo:
                puttingValuesInSharedPreference(Constant.INDIGO);
                break;
            case R.id.bLime:
                puttingValuesInSharedPreference(Constant.LIME);
                break;
            case R.id.bOrange:
                puttingValuesInSharedPreference(Constant.ORANGE);
                break;
            case R.id.bPink:
                puttingValuesInSharedPreference(Constant.PINK);
                break;
            case R.id.bBlueGrey:
                puttingValuesInSharedPreference(Constant.BLUE_GREY);
                break;
            case R.id.bYellow:
                puttingValuesInSharedPreference(Constant.YELLOW);
                break;
            case R.id.bPurple:
                puttingValuesInSharedPreference(Constant.PURPLE);
                break;
        }

        if (isEdit) {

            colorChange(entry.get_color());
        } else {
            colorChange();
        }
        dismiss();
    }

    public void colorChange() {
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        String color = pref.getString(Constant.SHARED_PREF_NOTE_COLOR, Constant.PURPLE);
        ColorDrawable colorDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(Utils.colorDecide(color)));
            svAddMain.setBackgroundColor(getColor(Utils.colorDecide(color)));
            relative.setBackgroundColor(getColor(Utils.colorDecide(color)));
        }
        if (bar != null) {
            bar.setBackgroundDrawable(colorDrawable);
        }

    }

    public void colorChange(String color) {

        ColorDrawable colorDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(Utils.colorDecide(color)));
            svAddMain.setBackgroundColor(getColor(Utils.colorDecide(color)));
            relative.setBackgroundColor(getColor(Utils.colorDecide(color)));
        }
        if (bar != null) {
            bar.setBackgroundDrawable(colorDrawable);
        }

    }

    public void puttingValuesInSharedPreference(String color) {
        if (isEdit) {

            entry.set_color(color);
        } else {
            pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
            editor = pref.edit();
            editor.putString(Constant.SHARED_PREF_NOTE_COLOR, color);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iAddSave:
                save();
                break;
            case R.id.iAddColor:
                dialog = new AlertDialog.Builder(AddActivity.this)
                        .setView(R.layout.color_custom_dialog)
                        .create();
                dialog.show();
                break;
            case R.id.iGeoFencing:
                if (isEdit) {
                    SharedPreferences pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constant.SHARED_PREF_LATITUDE, geo.get_latitude());
                    editor.putString(Constant.SHARED_PREF_LONGITUDE, geo.get_longitude());
                    editor.apply();
                }
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {


        String titleText;
        String titleCheck;
        String content;
        NoteEntry newEntry = new NoteEntry();

        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        String color = pref.getString(Constant.SHARED_PREF_NOTE_COLOR, Constant.PURPLE);

        newEntry.set_date((calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.YEAR));
        if (!isEdit) {
            newEntry.set_color(color);
        }

        switch (type) {
            case Constant.TEXT:

                eTitle = (EditText) findViewById(R.id.eTitle);
                eContent = (EditText) findViewById(R.id.eContent);

                titleText = eTitle.getText().toString();
                content = eContent.getText().toString();

                if (titleText.length() > 0 || content.length() > 0) {

                    if (isEdit) {
                        entry.set_title(titleText);
                        entry.set_content(content);
                        databaseHandler.updateNoteEntry(entry);
                        saveGeoFencing(entry);
                        Toast.makeText(AddActivity.this, "Text updated", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        newEntry.set_type(Constant.TEXT);
                        newEntry.set_title(titleText);
                        newEntry.set_content(content);
                        newEntry.set_reminderDate(null);
                        newEntry.set_reminderTime(null);
                        newEntry.set_frequency(null);
                        newEntry.set_id(databaseHandler.addEntry(newEntry));
                        saveGeoFencing(newEntry);
                        Toast.makeText(AddActivity.this, "Text Added", Toast.LENGTH_LONG).show();
                        finish();
                    }


                } else {
                    Toast.makeText(AddActivity.this, "Title and Content both cannot be null",
                            Toast.LENGTH_LONG).show();
                }

                break;

            case Constant.CHECKLIST:
                setContentView(R.layout.activity_add_check_list);
                if (bar != null) {
                    bar.setTitle("Add Check List");
                }

                titleCheck = ecTitle.getText().toString();

                if (list.size() > 0) {

                    content = Constant.CHECKLIST;


                    if (isEdit) {
                        entry.set_title(titleCheck);
                        databaseHandler.updateNoteEntry(entry);
                        for (CheckListItem temp : list) {
                            temp.set_noteId(entry.get_id());
                            databaseHandler.updateCheckListItem(temp);

                        }
                        saveGeoFencing(entry);
                        Toast.makeText(AddActivity.this, "Check List updated",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        newEntry.set_type(Constant.CHECKLIST);
                        newEntry.set_title(titleCheck);
                        newEntry.set_content(content);
                        newEntry.set_reminderDate(null);
                        newEntry.set_reminderTime(null);
                        newEntry.set_frequency(null);

                        newEntry.set_id(databaseHandler.addEntry(newEntry));
                        for (CheckListItem temp : list) {
                            temp.set_noteId(newEntry.get_id());
                            databaseHandler.addCheckListItem(temp);


                        }
                        saveGeoFencing(newEntry);
                        Toast.makeText(AddActivity.this, "Check List Created",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else {
                    colorChange();
                    Toast.makeText(AddActivity.this, "Add Item in Check List",
                            Toast.LENGTH_LONG).show();
                }


                break;

            case Constant.REMINDER:
                setContentView(R.layout.activity_add_reminder);
                if (bar != null) {
                    bar.setTitle("Add Reminder");
                }

                content = eContent.getText().toString();
                String date = tvDate.getText().toString();
                String time = tvTime.getText().toString();

                RadioButton rb = (RadioButton) rgFrequency.findViewById(rgFrequency.getCheckedRadioButtonId());

                if (content.length() > 0) {


                    if (isEdit) {
                        entry.set_content(content);
                        entry.set_reminderDate(date);
                        entry.set_reminderTime(time);

                        switch (rb.getId()) {
                            case R.id.rbOnce:
                                entry.set_frequency(Constant.ONCE);
                                break;
                            case R.id.rbDaily:
                                entry.set_frequency(Constant.DAILY);
                                break;
                            case R.id.rbMonthly:
                                entry.set_frequency(Constant.MONTHLY);
                                break;
                            case R.id.rbYearly:
                                entry.set_frequency(Constant.YEARLY);
                                break;

                        }
                        databaseHandler.updateNoteEntry(entry);
                        saveGeoFencing(entry);
                        Toast.makeText(AddActivity.this, "Reminder updated",
                                Toast.LENGTH_LONG).show();
                        cancelReminder(entry);
                        remind(entry);
                        finish();
                    } else {

                        newEntry.set_type(Constant.REMINDER);
                        newEntry.set_title(null);
                        newEntry.set_content(content);
                        newEntry.set_reminderDate(date);
                        newEntry.set_reminderTime(time);

                        switch (rb.getId()) {
                            case R.id.rbOnce:
                                newEntry.set_frequency(Constant.ONCE);
                                break;
                            case R.id.rbDaily:
                                newEntry.set_frequency(Constant.DAILY);
                                break;
                            case R.id.rbMonthly:
                                newEntry.set_frequency(Constant.MONTHLY);
                                break;
                            case R.id.rbYearly:
                                newEntry.set_frequency(Constant.YEARLY);
                                break;

                        }
                        newEntry.set_id(databaseHandler.addEntry(newEntry));
                        saveGeoFencing(newEntry);
                        remind(newEntry);
                        Toast.makeText(AddActivity.this, "Reminder Scheduled",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }


                } else {
                    Toast.makeText(AddActivity.this, "Message cannot be null",
                            Toast.LENGTH_LONG).show();
                }

                break;
        }

        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(Constant.SHARED_PREF_LONGITUDE, null);
        editor.putString(Constant.SHARED_PREF_LATITUDE, null);
        editor.apply();
    }

    public void saveGeoFencing(NoteEntry entry) {
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
        String latitude = pref.getString(Constant.SHARED_PREF_LATITUDE, null);
        String longitude = pref.getString(Constant.SHARED_PREF_LONGITUDE, null);
        if (latitude != null && latitude.length() > 0 && longitude != null && longitude.length() > 0) {
            GeoFencing item = new GeoFencing(latitude, longitude, entry.get_id(), Constant.RUNNING);
            databaseHandler.addGeofencingItem(item);
            editor = pref.edit();
            editor.putString(Constant.SHARED_PREF_LATITUDE, null);
            editor.putString(Constant.SHARED_PREF_LONGITUDE, null);
            editor.apply();


        }
    }


    public void cancelReminder(NoteEntry entry) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entry.get_id(),
                new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (pendingIntent != null) {
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    public void remind(NoteEntry entry) {


        Calendar cal = Utils.getDate(entry.get_reminderDate());
        cal = Utils.getTime(entry.get_reminderTime(), cal);
        long repeating;

        Notification notification = getNotification(entry);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Constant.ID, entry.get_id());
        intent.putExtra(Constant.NOTIFICATION, notification);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entry.get_id(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
        switch (entry.get_frequency()) {
            case Constant.DAILY:
                manager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                break;
            case Constant.MONTHLY:
                repeating = Utils.monthly();
                manager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                        repeating, pendingIntent);
                break;
            case Constant.YEARLY:
                GregorianCalendar c = new GregorianCalendar();
                if (c.isLeapYear(c.get(Calendar.YEAR))) {
                    manager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 366, pendingIntent);
                } else {
                    manager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 365, pendingIntent);
                }
                break;
        }

    }

    private Notification getNotification(NoteEntry entry) {

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(entry.get_content());
        builder.setSmallIcon(R.drawable.ic_query_builder_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        }
        return null;
    }

    public void addItem(View view) {

        Dialog(AddActivity.this);
    }

    public void next(View view) {

        String value = eAddItem.getText().toString();
        if (value.length() > 0) {
            CheckListItem item;
            if (index >= 0) {

                item = list.get(index);
                item.set_itemName(value);
                list.remove(index);
                list.add(index, item);
                index = -1;
            } else {
                item = new CheckListItem(value, Constant.UNCHECKED);
                list.add(item);
            }
            dialog.dismiss();
            onResume();
            dialog = null;
            Dialog(AddActivity.this);
        } else {
            Toast.makeText(this, "Please Enter Text", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view) {
        dialog.dismiss();
        dialog = null;
    }

    public void ok(View view) {
        String value = eAddItem.getText().toString();
        if (value.length() >= 0) {
            CheckListItem item;
            if (index >= 0) {

                item = list.get(index);
                item.set_itemName(value);
                list.remove(index);
                list.add(index, item);
                index = -1;
            } else {
                item = new CheckListItem(value, Constant.UNCHECKED);
                list.add(item);
            }
            dialog.dismiss();
            onResume();
            dialog = null;
        } else {
            Toast.makeText(this, "Please Enter Text", Toast.LENGTH_LONG).show();
        }
    }

    public void openDate(View view) {

        int year;
        int month;
        int day;

        if (isEdit) {
            Calendar c = Utils.getDate(entry.get_reminderDate());
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year,
                month,
                day);

        datePickerDialog.show();

    }

    public void openTime(View view) {
        int hour;
        int min;
        if (isEdit) {
            Calendar c = Calendar.getInstance();
            Utils.getTime(entry.get_reminderTime(), c);
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);

        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
        }


        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour,
                min,
                false);
        timePickerDialog.show();

    }
}
