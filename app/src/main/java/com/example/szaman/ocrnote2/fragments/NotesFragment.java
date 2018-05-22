package com.example.szaman.ocrnote2.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.szaman.ocrnote2.NoteAddActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.database.Note;
import com.example.szaman.ocrnote2.utils.NotesListAdapter;
import com.example.szaman.ocrnote2.utils.NotesListViewModel;
import com.example.szaman.ocrnote2.utils.RecyclerClickActions;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private List<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NotesListViewModel notesListViewModel;
    private RecyclerClickActions recyclerClickActions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        notesListViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);
        recyclerView = view.findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerClickActions = new RecyclerClickActions(notesListViewModel, fab);
        mAdapter = new NotesListAdapter(notes, recyclerClickActions);
        recyclerClickActions.setAdapter(mAdapter);

        recyclerView.setAdapter(mAdapter);

        //notesListViewModel.clearNotes();
        //notesListViewModel.generateMock((MainActivity) getActivity());

        notesListViewModel.getNotesList().observe(NotesFragment.this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                mAdapter.addItems(notes);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NoteAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_notes);
    }

}
