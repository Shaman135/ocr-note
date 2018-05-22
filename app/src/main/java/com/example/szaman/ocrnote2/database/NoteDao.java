package com.example.szaman.ocrnote2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by szaman on 23.12.17.
 */

@Dao
@TypeConverters(Converters.class)
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNote(Note note);

    @Query("SELECT * FROM Note")
    LiveData<List<Note>>getNotes();

    @Query("SELECT * FROM Note WHERE id=:id")
    Note findNoteById(int id);

    @Query("SELECT * FROM Note WHERE timestamp BETWEEN :from AND :to")
    LiveData< List<Note> > findNotesCreatedBetween(Date from, Date to);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(Note note);

    @Query("delete from note where id = :id")
    void deleteById(int id);

    @Query("delete from note")
    void deleteAllNotes();

    @Delete
    void deleteNote(Note note);
}
