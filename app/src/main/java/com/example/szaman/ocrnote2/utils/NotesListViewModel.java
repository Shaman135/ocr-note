package com.example.szaman.ocrnote2.utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.example.szaman.ocrnote2.MainActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.database.AppDatabase;
import com.example.szaman.ocrnote2.database.Note;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

/**
 * Created by szaman on 23.12.17.
 */

public class NotesListViewModel extends AndroidViewModel {

    private final LiveData<List<Note>> notesList;
    private AppDatabase appDatabase;


    public NotesListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
        notesList = appDatabase.noteDao().getNotes();
    }

    public LiveData<List<Note>> getNotesList() {
        return notesList;
    }

    public void deleteNote(Note note) {
        new deleteAsyncTask(appDatabase).execute(note);
    }

    public void addNote(Note note){
        new addAsyncTask(appDatabase).execute(note);
    }

    public void updateNote(Note note){
        new updateAsyncTask(appDatabase).execute(note);
    }

    public void clearNotes(){
        new clearAsyncTask(appDatabase).execute();
    }

    public void generateMock(MainActivity context) {
        new mockAsyncTask(appDatabase, context).execute();
    }

    public static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {

        private AppDatabase database;

        deleteAsyncTask(AppDatabase appDatabase){
            database = appDatabase;
        }

        @Override
        protected Void doInBackground(Note... params) {
            database.noteDao().deleteNote(params[0]);
            return null;
        }
    }

    public static class addAsyncTask extends AsyncTask<Note, Void, Void> {

        private AppDatabase database;

        addAsyncTask(AppDatabase appDatabase){
            database = appDatabase;
        }

        @Override
        protected Void doInBackground(Note... params) {
            database.noteDao().addNote(params[0]);
            return null;
        }
    }

    public static class updateAsyncTask extends AsyncTask<Note, Void, Void> {

        private AppDatabase database;

        updateAsyncTask(AppDatabase appDatabase){
            database = appDatabase;
        }

        @Override
        protected Void doInBackground(Note... params) {
            database.noteDao().updateNote(params[0]);
            return null;
        }
    }


    public static class clearAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase database;

        clearAsyncTask(AppDatabase appDatabase){
            database = appDatabase;
        }

        @Override
        protected Void doInBackground(Void... params) {
            database.noteDao().deleteAllNotes();
            return null;
        }
    }

    public static class mockAsyncTask extends AsyncTask<Void, Void, LiveData<List<Note>>> {

        private WeakReference<MainActivity> activityReference;
        private ProgressDialog dialog;
        private AppDatabase appDatabase;
        private Context context;
        LiveData<List<Note>> notes;

        mockAsyncTask(AppDatabase appDatabase, MainActivity context) {
            activityReference = new WeakReference<MainActivity>(context);
            dialog = new ProgressDialog(context);
            this.appDatabase = appDatabase;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(context.getResources().getString(R.string.wait));
            this.dialog.show();
        }

        @Override
        protected LiveData<List<Note>> doInBackground(Void... voids) {
            appDatabase = AppDatabase.getInstance(activityReference.get());
            notes = appDatabase.noteDao().getNotes();
            appDatabase.noteDao().deleteAllNotes();
            for (int i = 0; i < 20; i++) {
                appDatabase.noteDao().addNote(new Note("Testowa notatka " + Integer.toString(i),
                        "Pierwsza linijka tekstu\n Druga linijka tekstu \n No i trzecia \n W sumie można jeszcze więcej.",
                        Calendar.getInstance().getTime()));
            }
            return appDatabase.noteDao().getNotes();
        }

        @Override
        protected void onPostExecute(LiveData<List<Note>> notes) {
            MainActivity mainActivity = activityReference.get();
            if(mainActivity == null){
                return;
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
