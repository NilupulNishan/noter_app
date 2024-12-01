package com.example.notesapp.databaseM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotesDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_PINNED,
            MySQLiteHelper.COLUMN_DATE,
            MySQLiteHelper.COLUMN_NOTES,
            MySQLiteHelper.COLUMN_TITLE
    };

    public NotesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note insert(Note note) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PINNED, note.isPinned() ? 1 : 0);
        values.put(MySQLiteHelper.COLUMN_DATE, note.getDate());
        values.put(MySQLiteHelper.COLUMN_NOTES, note.getNotes());
        values.put(MySQLiteHelper.COLUMN_TITLE, note.getTitle());
        long insertId = database.insert(MySQLiteHelper.TABLE_NOTES, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NOTES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NOTES,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }

    public void update(int id, String title, String notes) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_NOTES, notes);
        database.update(MySQLiteHelper.TABLE_NOTES, values,
                MySQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void delete(Note note) {
        long id = note.getID();
        System.out.println("Note deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_NOTES, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void pin(int id, boolean pin) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PINNED, pin ? 1 : 0);
        database.update(MySQLiteHelper.TABLE_NOTES, values,
                MySQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setID(cursor.getInt(0));
        note.setPinned(cursor.getInt(1) > 0);
        note.setDate(cursor.getString(2));
        note.setNotes(cursor.getString(3));
        note.setTitle(cursor.getString(4));
        return note;
    }
}
