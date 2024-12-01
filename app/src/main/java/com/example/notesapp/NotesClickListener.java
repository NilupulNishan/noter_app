package com.example.notesapp;

import androidx.cardview.widget.CardView;

import com.example.notesapp.databaseM.Note;

public interface NotesClickListener {
    void onClick(Note notes);
    void onLongClick(Note notes, CardView cardView);
}
