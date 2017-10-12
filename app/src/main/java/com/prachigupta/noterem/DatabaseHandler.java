package com.prachigupta.noterem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler singleton = null;

    synchronized static DatabaseHandler getInstance(Context context) {
        if (singleton == null) {
            singleton = new DatabaseHandler(context);
        }
        return singleton;
    }

    private DatabaseHandler(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + Constant.TABLE_NOTE + " ( " +
                Constant.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Constant.COLUMN_TYPE + " TEXT ," +
                Constant.COLUMN_TITLE + " TEXT , " +
                Constant.COLUMN_CONTENT + " TEXT , " +
                Constant.COLUMN_DATE + " TEXT ," +
                Constant.COLUMN_COLOR + " TEXT ," +
                Constant.COLUMN_FREQUENCY + " TEXT ," +
                Constant.COLUMN_REMINDER_DATE + " TEXT ," +
                Constant.COLUMN_REMINDER_TIME + " TEXT " +
                ")";
        db.execSQL(query);
        query = "CREATE TABLE " + Constant.TABLE_CHECKLIST + " ( " +
                Constant.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Constant.COLUMN_ITEMNAME + " TEXT ," +
                Constant.COLUMN_CHECKED + " TEXT , " +
                Constant.COLUMN_NOTEID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + Constant.COLUMN_NOTEID + " ) REFERENCES " +
                Constant.TABLE_NOTE + " (" + Constant.COLUMN_ID + " ));";
        db.execSQL(query);

        query = "CREATE TABLE " + Constant.TABLE_GEOFENCING + " ( " +
                Constant.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Constant.COLUMN_LATITUDE + " TEXT ," +
                Constant.COLUMN_LONGITUDE + " TEXT , " +
                Constant.COLUMN_RUNNING + " TEXT , " +
                Constant.COLUMN_NOTEID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + Constant.COLUMN_NOTEID + " ) REFERENCES " +
                Constant.TABLE_NOTE + " (" + Constant.COLUMN_ID + " ));";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_CHECKLIST);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_GEOFENCING);
        onCreate(db);

    }

    public List<NoteEntry> databaseToList() {

        List<NoteEntry> noteList = new ArrayList<NoteEntry>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + Constant.TABLE_NOTE + " WHERE 1;";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(Constant.COLUMN_DATE)) != null) {
                NoteEntry entry = new NoteEntry();
                entry.set_id(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_ID)));
                entry.set_type(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TYPE)));
                entry.set_title(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TITLE)));
                entry.set_content(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                entry.set_date(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_DATE)));
                entry.set_color(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_COLOR)));
                entry.set_frequency(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_FREQUENCY)));
                entry.set_reminderDate(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_REMINDER_DATE)));
                entry.set_reminderTime(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_REMINDER_TIME)));
                noteList.add(entry);
            }
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return noteList;
    }

    public int addEntry(NoteEntry entry) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_TYPE, entry.get_type());
        values.put(Constant.COLUMN_TITLE, entry.get_title());
        values.put(Constant.COLUMN_CONTENT, entry.get_content());
        values.put(Constant.COLUMN_COLOR, entry.get_color());
        values.put(Constant.COLUMN_DATE, entry.get_date());
        values.put(Constant.COLUMN_FREQUENCY, entry.get_frequency());
        values.put(Constant.COLUMN_REMINDER_DATE, entry.get_reminderDate());
        values.put(Constant.COLUMN_REMINDER_TIME, entry.get_reminderTime());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(Constant.TABLE_NOTE, null, values);
        Log.i("Database", "Save entry ID " + id);
        db.close();
        return (int) id;
    }

    public NoteEntry getEntry(int id) {
        NoteEntry entry = new NoteEntry();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + Constant.TABLE_NOTE + " WHERE " + Constant.COLUMN_ID + " = " + id + " ;";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(Constant.COLUMN_DATE)) != null) {
                entry.set_id(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_ID)));
                entry.set_type(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TYPE)));
                entry.set_title(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_TITLE)));
                entry.set_content(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_CONTENT)));
                entry.set_date(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_DATE)));
                entry.set_color(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_COLOR)));
                entry.set_frequency(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_FREQUENCY)));
                entry.set_reminderDate(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_REMINDER_DATE)));
                entry.set_reminderTime(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_REMINDER_TIME)));
            }
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return entry;
    }

    public void deleteEntry(int id) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(Constant.TABLE_NOTE, Constant.COLUMN_ID + " = " + id, null);
        db.delete(Constant.TABLE_CHECKLIST, Constant.COLUMN_NOTEID + " = " + id, null);
        db.delete(Constant.TABLE_GEOFENCING, Constant.COLUMN_NOTEID + " = " + id, null);

        db.close();
    }

    public List<CheckListItem> databaseToListCheckList(int id) {

        List<CheckListItem> noteList = new ArrayList<CheckListItem>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + Constant.TABLE_CHECKLIST + " WHERE " +
                Constant.COLUMN_NOTEID + " = " + id + " ;";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_NOTEID)) > 0) {
                CheckListItem item = new CheckListItem();
                item.set_id(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_ID)));
                item.set_itemName(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_ITEMNAME)));
                item.set_checked(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_CHECKED)));
                item.set_noteId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_NOTEID)));
                noteList.add(item);
            }
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return noteList;
    }

    public int addCheckListItem(CheckListItem item) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_ITEMNAME, item.get_itemName());
        values.put(Constant.COLUMN_CHECKED, item.get_checked());
        values.put(Constant.COLUMN_NOTEID, item.get_noteId());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(Constant.TABLE_CHECKLIST, null, values);
        Log.i("Database", "Save item ID checklist " + id);
        db.close();
        return (int) id;
    }

    public void updateCheckListItemChecked(CheckListItem item) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_CHECKED, item.get_checked());

        SQLiteDatabase db = getWritableDatabase();
        db.update(Constant.TABLE_CHECKLIST, values, Constant.COLUMN_ID + " = " + item.get_id(), null);
        db.close();
    }

    public void updateCheckListItem(CheckListItem item) {


        if (item.get_id() > 0) {
            ContentValues values = new ContentValues();
            values.put(Constant.COLUMN_ITEMNAME, item.get_itemName());

            SQLiteDatabase db = getWritableDatabase();
            db.update(Constant.TABLE_CHECKLIST, values, Constant.COLUMN_ID + " = " + item.get_id(), null);
            db.close();

        } else {
            addCheckListItem(item);
        }


    }

    public void updateNoteEntry(NoteEntry entry) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_TITLE, entry.get_title());
        values.put(Constant.COLUMN_CONTENT, entry.get_content());
        values.put(Constant.COLUMN_COLOR, entry.get_color());
        values.put(Constant.COLUMN_DATE, entry.get_date());
        values.put(Constant.COLUMN_FREQUENCY, entry.get_frequency());
        values.put(Constant.COLUMN_REMINDER_DATE, entry.get_reminderDate());
        values.put(Constant.COLUMN_REMINDER_TIME, entry.get_reminderTime());

        SQLiteDatabase db = getWritableDatabase();
        db.update(Constant.TABLE_NOTE, values, Constant.COLUMN_ID + " = " + entry.get_id(), null);
        db.close();
    }

    public List<GeoFencing> databaseToListGeofencing() {

        List<GeoFencing> noteList = new ArrayList<GeoFencing>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + Constant.TABLE_GEOFENCING + " WHERE 1 ;";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_NOTEID)) > 0) {
                GeoFencing item = new GeoFencing();
                item.set_id(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_ID)));
                item.set_latitude(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_LATITUDE)));
                item.set_longitude(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_LONGITUDE)));
                item.set_running(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_RUNNING)));
                item.set_noteId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_NOTEID)));
                noteList.add(item);
            }
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return noteList;
    }

    public int addGeofencingItem(GeoFencing item) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_LATITUDE, item.get_latitude());
        values.put(Constant.COLUMN_LONGITUDE, item.get_longitude());
        values.put(Constant.COLUMN_RUNNING, item.get_running());
        values.put(Constant.COLUMN_NOTEID, item.get_noteId());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(Constant.TABLE_GEOFENCING, null, values);
        Log.i("Database", "Save item ID geoFencing" + id + "");
        db.close();
        return (int) id;
    }

    public void updateGeofencingItemFinished(GeoFencing item) {


        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_RUNNING, item.get_running());

        SQLiteDatabase db = getWritableDatabase();
        db.update(Constant.TABLE_GEOFENCING, values, Constant.COLUMN_ID + " = " + item.get_id(), null);
        db.close();
    }

    public GeoFencing getEntryGeoByNoteId(int id) {
        GeoFencing entry = new GeoFencing();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + Constant.TABLE_GEOFENCING + " WHERE " + Constant.COLUMN_NOTEID + " = " + id + " ;";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(Constant.COLUMN_NOTEID)) != null) {
                entry.set_id(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_ID)));
                entry.set_latitude(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_LATITUDE)));
                entry.set_longitude(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_LONGITUDE)));
                entry.set_noteId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_NOTEID)));
                entry.set_running(cursor.getString(cursor.getColumnIndex(Constant.COLUMN_RUNNING)));
            }
            cursor.moveToNext();
        }
        db.close();
        cursor.close();
        return entry;
    }

    public void deleteCheckListItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Constant.TABLE_CHECKLIST, Constant.COLUMN_ID + " = " + id, null);
        db.close();
    }

}
