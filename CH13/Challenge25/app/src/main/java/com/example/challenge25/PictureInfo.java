package com.example.challenge25;


public class PictureInfo {

    String path;
    String displayName;
    String dateAdded;
    int id;
    int _id;

    public PictureInfo(String path, String displayName, String dateAdded) {
        this.path = path;
        this.displayName = displayName;
        this.dateAdded = dateAdded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setId(int id){this.id = id;}

    public void setPrimaryKey(int _id){this._id = _id;}

    @Override
    public String toString() {
        return "PictureInfo{" +
                "path='" + path + '\'' +
                ", displayName='" + displayName + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }

}