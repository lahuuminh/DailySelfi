package com.example.dailyselfi;

import java.io.Serializable;

public class PhotoModel implements Serializable {
    private String path;
    private String dateTime;
    private boolean isSelected=false;

    public PhotoModel(String path, String dateTime) {
        this.path = path;
        this.dateTime = dateTime;
        this.isSelected = false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
