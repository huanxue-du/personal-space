package com.sz.autolink.javademo;

/**
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */

public class RadioEntity  {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String FREQUENCY = "frequency";
    public static final String FREQUENCY_TYPE = "frequency_Type";
    public static final String ISCOLL = "iscoll";


    private int id;
    private int index;
    private float freq;

    public float getFreq() {
        return freq;
    }

    public void setFreq(float freq) {
        this.freq = freq;
    }

    private String type;
    private String frequency;
    private String frequencyType;
    private int isColl;

    public int getIsColl() {
        return isColl;
    }

    public int setIsColl(int isColl) {
        this.isColl = isColl;
        return isColl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    //    @Override
    //    public int describeContents() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public void writeToParcel(Parcel out, int flags) {
    //        out.writeInt(id);
    //        out.writeString(type);
    //        out.writeString(frequency);
    //        out.writeInt(isColl);
    //        out.writeString(frequencyType);
    //    }

    //    public static final Parcelable.Creator<RadioEntity> CREATOR = new Creator<RadioEntity>() {
    //        @Override
    //        public RadioEntity[] newArray(int size) {
    //            return new RadioEntity[size];
    //        }
    //
    //        @Override
    //        public RadioEntity createFromParcel(Parcel in) {
    //            return new RadioEntity(in);
    //        }
    //    };
    //
    //    public RadioEntity(Parcel in) {
    //        id = in.readInt();
    //        type = in.readString();
    //        frequency = in.readString();
    //        isColl = in.readInt();
    //        frequencyType = in.readString();
    //
    //    }

    public RadioEntity() {


    }
}
