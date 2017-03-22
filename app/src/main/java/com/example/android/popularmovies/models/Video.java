package com.example.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Michalis on 2/3/2017.
 */

public class Video implements Parcelable{
    @SerializedName("id")
    private String id;
    @SerializedName("iso_639_1")
    private String iso_first;
    @SerializedName("iso_3166_1")
    private String iso_sec;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private int size;
    @SerializedName("type")
    private String type;

    public Video(String id, String iso_first, String iso_sec,
                 String key, String name, String site, int size, String type) {
        this.type = type;
        this.id = id;
        this.iso_first = iso_first;
        this.iso_sec = iso_sec;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
    }

    protected Video(Parcel in) {
        id = in.readString();
        iso_first = in.readString();
        iso_sec = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_first() {
        return iso_first;
    }

    public void setIso_first(String iso_first) {
        this.iso_first = iso_first;
    }

    public String getIso_sec() {
        return iso_sec;
    }

    public void setIso_sec(String iso_sec) {
        this.iso_sec = iso_sec;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso_first);
        dest.writeString(iso_sec);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);

    }
}
