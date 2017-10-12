package com.prachigupta.noterem;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    AlertDialog dialog;
    ListView lvAddEntry;
    TextView tvAddNote;
    RelativeLayout rlMain;

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);

        if (!isServiceStarted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.INTERNET}
                            , 10);
                } else {
                    Toast.makeText(this, "Please provide permission for Location", Toast.LENGTH_LONG).show();
                }
                return;
            }
            Intent i = new Intent(this, MapService.class);

            Log.i("Main Activity", "Service Start");
            startService(i);
        }
        //this.deleteDatabase(Constant.DATABASE_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        colorChange();

        tvAddNote = (TextView) findViewById(R.id.tvAddNote);
        lvAddEntry = (ListView) findViewById(R.id.lvNoteEntry);

        final List<NoteEntry> noteList = databaseHandler.databaseToList();

        if (noteList.size() > 0) {

            tvAddNote.setVisibility(View.GONE);


            lvAddEntry.setVisibility(View.VISIBLE);

            NoteAdapter adapter = new NoteAdapter(this, noteList);
            lvAddEntry.setAdapter(adapter);

            final Context context = this;
            lvAddEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    NoteEntry entry = noteList.get(position);

                    Intent detailIntent = new Intent(context, DetailActivity.class);
                    detailIntent.putExtra(Constant.ID, entry.get_id());
                    startActivity(detailIntent);
                }
            });


        } else {

            lvAddEntry.setVisibility(View.GONE);
            tvAddNote.setVisibility(View.VISIBLE);
            tvAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add();
                }
            });
        }

    }

    public void colorChange() {

        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        String color = pref.getString(Constant.SHARED_PREF_NOTE_COLOR, Constant.PURPLE);
        ColorDrawable colorDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(Utils.colorDecide(color)));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Color feature not available in your device");
            AlertDialog alert = builder.create();
            alert.show();

        }

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(colorDrawable);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.imAdd:
                add();
                break;
            case R.id.imColor:
                dialog = new AlertDialog.Builder(this)
                        .setView(R.layout.color_custom_dialog)
                        .create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void add() {

        dialog = new AlertDialog.Builder(this)
                .setView(R.layout.add_custom_dialog)
                .create();
        dialog.show();


    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }

    public void addClick(View view) {

        dismiss();
        Intent intent = new Intent(this, AddActivity.class);
        switch (view.getId()) {
            case R.id.bText:
                intent.putExtra(Constant.INTENT_ADD, Constant.TEXT);
                break;
            case R.id.bCheckList:
                intent.putExtra(Constant.INTENT_ADD, Constant.CHECKLIST);
                break;
            case R.id.bReminder:
                intent.putExtra(Constant.INTENT_ADD, Constant.REMINDER);
                break;
        }

        startActivity(intent);

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

        colorChange();
        dismiss();
    }

    public void puttingValuesInSharedPreference(String color) {
        pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(Constant.SHARED_PREF_NOTE_COLOR, color);
        editor.apply();
    }

    public boolean isServiceStarted() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MapService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
