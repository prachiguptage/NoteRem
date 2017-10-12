package com.prachigupta.noterem;


public class CheckListItem {

    private int _id;
    private String _itemName;
    private String _checked;
    private int _noteId;

    public CheckListItem() {
    }

    public CheckListItem(String _itemName, String _checked, int _noteId) {
        this._itemName = _itemName;
        this._checked = _checked;
        this._noteId = _noteId;
    }

    public CheckListItem(String _itemName, String _checked) {
        this._itemName = _itemName;
        this._checked = _checked;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_itemName() {
        return _itemName;
    }

    public void set_itemName(String _itemName) {
        this._itemName = _itemName;
    }

    public String get_checked() {
        return _checked;
    }

    public void set_checked(String _checked) {
        this._checked = _checked;
    }

    public int get_noteId() {
        return _noteId;
    }

    public void set_noteId(int _noteId) {
        this._noteId = _noteId;
    }
}

