package com.example.szaman.ocrnote2.utils;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.szaman.ocrnote2.AuthActivity;
import com.example.szaman.ocrnote2.NoteEditActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.database.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szaman on 23.12.17.
 */

public class RecyclerClickActions implements View.OnLongClickListener, View.OnClickListener{

    private boolean multiSelect = false;
    private List<Note> selectedItems = new ArrayList<Note>();
    private List<View> selectedViews = new ArrayList<View>();
    private NotesListViewModel notesListViewModel;
    private NotesListAdapter adapter;
    private FloatingActionButton fab;

    public void setAdapter(NotesListAdapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerClickActions(NotesListViewModel notesListViewModel, FloatingActionButton fab) {
        this.notesListViewModel = notesListViewModel;
        this.fab = fab;
    }

    private void selectItem(Note item, View view) {
        if (multiSelect) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
                selectedViews.remove(view);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setSelected(false);
            } else {
                selectedItems.add(item);
                selectedViews.add(view);
                view.setBackgroundColor(Color.LTGRAY);
                view.setSelected(true);
            }
        }
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            fab.hide();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (int i = 0; i < selectedItems.size(); i++) {
                notesListViewModel.deleteNote(selectedItems.get(i));
                selectedViews.get(i).setSelected(false);
                adapter.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            for(View view : selectedViews){
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setSelected(false);
            }
            selectedViews.clear();
            adapter.notifyDataSetChanged();
            fab.show();
        }
    };

    @Override
    public boolean onLongClick(View view) {
        if(!multiSelect){
            ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
            selectItem((Note) view.getTag(), view);
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        if(multiSelect){
            selectItem((Note) view.getTag(), view);
        } else {
            Note selected = (Note) view.getTag();
            if(selected.isProtected()){
                Intent intent = new Intent(view.getContext(), AuthActivity.class);
                intent.putExtra("note", (Note) view.getTag());
                view.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(view.getContext(), NoteEditActivity.class);
                intent.putExtra("note", (Note) view.getTag());
                view.getContext().startActivity(intent);
            }
        }
    }
}
