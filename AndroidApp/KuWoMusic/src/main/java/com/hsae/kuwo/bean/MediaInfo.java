package com.hsae.kuwo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.2.26.
 */
public class MediaInfo implements Parcelable {
    private int type;
    private String name;
    private String artist;
    private String imageUrl;


    public MediaInfo(int type,String name,String artist,String imageUrl) {
        this.type=type;
        this.name=name;
        this.artist=artist;
        this.imageUrl=imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeString(this.artist);
        dest.writeString(this.imageUrl);
    }

    protected MediaInfo(Parcel in) {
        this.type = in.readInt();
        this.name = in.readString();
        this.artist = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel source) {
            return new MediaInfo(source);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist == null ? "" : artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImageUrl() {
        return imageUrl == null ? "" : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
