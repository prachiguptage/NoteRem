package com.prachigupta.noterem;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActionBar bar;

    EditText eTitle;
    EditText eContent;
    ScrollView svAddMain;
    Button bAddItem;
    ListView lvCheckListAdd;
    Button bDate;
    Button bTime;
    RadioButton rbOnce;
    RadioButton rbDaily;
    RadioButton rbMonthly;
    RadioButton rbYearly;
    RelativeLayout relative;
    FrameLayout frame;
    TextView tvTitle;
    TextView tvContent;
    TextView tvDate;
    TextView tvTime;

    NoteEntry entry;
    GeoFencing geo;

    DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);

    int id;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        id = extras.getInt(Constant.ID, 0);


    }

    @Override
    protected void onResume() {
        super.onResume();

        entry = databaseHandler.getEntry(id);
        geo = databaseHandler.getEntryGeoByNoteId(entry.get_id());
        setView(entry.get_type());
        String color = entry.get_color();

        svAddMain = (ScrollView) findViewById(R.id.svAddMain);
        relative = (RelativeLayout) findViewById(R.id.rlAddMain);

        ColorDrawable colorDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(Utils.colorDecide(color)));
            svAddMain.setBackgroundColor(getColor(Utils.colorDecide(color)));
            relative.setBackgroundColor(getColor(Utils.colorDecide(color)));
        }

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(colorDrawable);

        }

        switch (entry.get_type()) {
            case Constant.TEXT:

                bar.setTitle("Notes");
                eTitle = (EditText) findViewById(R.id.eTitle);
                eContent = (EditText) findViewById(R.id.eContent);
                tvTitle = (TextView) findViewById(R.id.tvTitle);
                tvContent = (TextView) findViewById(R.id.tvContent);
                eTitle.setEnabled(false);
                eContent.setEnabled(false);
                eTitle.setVisibility(View.GONE);
                eContent.setVisibility(View.GONE);
                tvTitle.setText(entry.get_title());
                tvContent.setText(entry.get_content());
                tvTitle.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.VISIBLE);


                break;
            case Constant.CHECKLIST:

                bar.setTitle("CheckList");
                bAddItem = (Button) findViewById(R.id.bAddChecklistItem);
                bAddItem.setVisibility(View.GONE);
                final List<CheckListItem> items = databaseHandler
                        .databaseToListCheckList(entry.get_id());

                eTitle = (EditText) findViewById(R.id.ecTitle);
                eTitle.setVisibility(View.GONE);

                tvTitle = (TextView) findViewById(R.id.tvTitle);
                tvTitle.setVisibility(View.VISIBLE);
                if (entry.get_title().length() > 0) {
                    tvTitle.setText(entry.get_title());

                } else {
                    tvTitle.setText(entry.get_date());
                }
                eTitle.setEnabled(false);

                lvCheckListAdd = (ListView) findViewById(R.id.lvChecklistAdd);
                final CheckListAdapter adapter = new CheckListAdapter(this, items);
                lvCheckListAdd.setAdapter(adapter);
                Utils.getListViewSize(lvCheckListAdd);

                lvCheckListAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String check = items.get(position).get_checked();
                        switch (check) {
                            case Constant.CHECKED:
                                items.get(position).set_checked(Constant.UNCHECKED);
                                break;
                            case Constant.UNCHECKED:
                                items.get(position).set_checked(Constant.CHECKED);
                                break;
                        }
                        databaseHandler.updateCheckListItemChecked(items.get(position));

                        adapter.notifyDataSetChanged();
                    }
                });

                break;
            case Constant.REMINDER:

                bar.setTitle("Reminder");
                bDate = (Button) findViewById(R.id.bSelectDate);
                bTime = (Button) findViewById(R.id.bSelectTime);

                bDate.setVisibility(View.GONE);
                bTime.setVisibility(View.GONE);

                eContent = (EditText) findViewById(R.id.eNotes);

                tvContent = (TextView) findViewById(R.id.tvNotes);
                tvDate = (TextView) findViewById(R.id.tvDate);
                tvTime = (TextView) findViewById(R.id.tvTime);

                tvContent.setText("Message    : " + entry.get_content());
                tvDate.setText("Reminder Date : " + entry.get_reminderDate() + "  ");
                tvTime.setText("Reminder Time : " + entry.get_reminderTime() + "   ");

                eContent.setEnabled(false);

                eContent.setVisibility(View.GONE);

                tvContent.setVisibility(View.VISIBLE);


                rbOnce = (RadioButton) findViewById(R.id.rbOnce);
                rbDaily = (RadioButton) findViewById(R.id.rbDaily);
                rbMonthly = (RadioButton) findViewById(R.id.rbMonthly);
                rbYearly = (RadioButton) findViewById(R.id.rbYearly);

                rbOnce.setEnabled(false);
                rbMonthly.setEnabled(false);
                rbDaily.setEnabled(false);
                rbYearly.setEnabled(false);

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


                break;
        }

        frame = (FrameLayout) findViewById(R.id.map_space);

        if (geo != null && geo.get_id() > 0) {

            latitude = Double.parseDouble(geo.get_latitude());
            longitude = Double.parseDouble(geo.get_longitude());
            frame.setVisibility(View.VISIBLE);
            MapFragment mapFragment = MapFragment.newInstance().newInstance();

            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map_space, mapFragment);
            fragmentTransaction.commit();

            mapFragment.getMapAsync(this);
        }


    }

    public void setView(String type) {
        switch (type) {
            case Constant.TEXT:
                setContentView(R.layout.activity_add_text);
                break;
            case Constant.CHECKLIST:
                setContentView(R.layout.activity_add_check_list);
                break;
            case Constant.REMINDER:
                setContentView(R.layout.activity_add_reminder);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.iViewEdit:
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra(Constant.EDIT, Constant.EDIT);
                intent.putExtra(Constant.INTENT_ADD, entry.get_type());
                intent.putExtra(Constant.ID, entry.get_id());
                startActivity(intent);
                break;
            case R.id.iViewDelete:
                databaseHandler.deleteEntry(id);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title(latitude + ":" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
    }


}
