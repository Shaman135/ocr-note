package com.example.szaman.ocrnote2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by szaman on 23.12.17.
 */

@Entity
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String desc;
    private String text;
    private Date timestamp;
    private boolean isProtected;

    public Note(String desc, String text, Date timestamp) {
        this.desc = desc;
        this.text = text;
        this.timestamp = timestamp;
        this.isProtected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }
}
