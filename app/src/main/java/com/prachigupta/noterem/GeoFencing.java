package com.prachigupta.noterem;


public class GeoFencing {

    private int _id;
    private String _latitude;
    private String _longitude;
    private int _noteId;
    private String _running;

    public GeoFencing(String _latitude, String _longitude, int _noteId, String _running) {
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._noteId = _noteId;
        this._running = _running;
    }

    public GeoFencing() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_latitude() {
        return _latitude;
    }

    public void set_latitude(String _latitude) {
        this._latitude = _latitude;
    }

    public String get_longitude() {
        return _longitude;
    }

    public void set_longitude(String _longitude) {
        this._longitude = _longitude;
    }

    public int get_noteId() {
        return _noteId;
    }

    public void set_noteId(int _noteId) {
        this._noteId = _noteId;
    }

    public String get_running() {
        return _running;
    }

    public void set_running(String _running) {
        this._running = _running;
    }
}
