package com.prachigupta.noterem;


public class NoteEntry {

    private int _id;
    private String _type;
    private String _title;
    private String _content;
    private String _date;
    private String _color;
    private String _frequency;
    private String _reminderDate;
    private String _reminderTime;

    public NoteEntry() {
    }

    public NoteEntry(String _type, String _title, String _content, String _date, String _color, String _frequency, String _reminderDate, String _reminderTime) {
        this._type = _type;
        this._title = _title;
        this._content = _content;
        this._date = _date;
        this._color = _color;
        this._frequency = _frequency;
        this._reminderDate = _reminderDate;
        this._reminderTime = _reminderTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_content() {
        return _content;
    }

    public void set_content(String _content) {
        this._content = _content;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_color() {
        return _color;
    }

    public void set_color(String _color) {
        this._color = _color;
    }

    public String get_frequency() {
        return _frequency;
    }

    public void set_frequency(String _frequency) {
        this._frequency = _frequency;
    }

    public String get_reminderDate() {
        return _reminderDate;
    }

    public void set_reminderDate(String _reminderDate) {
        this._reminderDate = _reminderDate;
    }

    public String get_reminderTime() {
        return _reminderTime;
    }

    public void set_reminderTime(String _reminderTime) {
        this._reminderTime = _reminderTime;
    }
}
