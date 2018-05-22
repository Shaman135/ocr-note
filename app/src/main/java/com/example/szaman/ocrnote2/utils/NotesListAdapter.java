package com.example.szaman.ocrnote2.utils;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.database.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by szaman on 23.12.17.
 */

public class NotesListAdapter extends RecyclerView.Adapter <NotesListAdapter.ViewHolder> {

    private List<Note> values;
    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;
    private NotesListAdapter adapter;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView description;
        public TextView timestamp;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            description = itemView.findViewById(R.id.description);
            timestamp = itemView.findViewById(R.id.timestamp);
        }



    }

    public NotesListAdapter(List<Note> dataSet, RecyclerClickActions clickActions){
        this.values = dataSet;
        this.longClickListener = clickActions;
        this.clickListener = clickActions;
        setHasStableIds(true);
    }

    public void addItems(List<Note> notes){
        this.values = notes;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotesListAdapter.ViewHolder holder, int position) {
        final Note note = values.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        Date ts = note.getTimestamp();
        String formatted = dateFormat.format(ts);
        holder.description.setText(note.getDesc());
        holder.timestamp.setText(formatted);
        holder.itemView.setTag(note);
        holder.itemView.setOnLongClickListener(longClickListener);
        holder.itemView.setOnClickListener(clickListener);
        if(holder.itemView.isSelected()){
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}
