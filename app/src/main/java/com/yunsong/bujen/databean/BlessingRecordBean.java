package com.yunsong.bujen.databean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BlessingRecordBean implements Parcelable {
    public int recordId;
    public int userId;
    public String blessingTime;
    public int blessingId;
    public String blessingTitle;
    public String blessingMethod;
    public String blessingContent;
    public String blessingAudioUrl;
    public String blessingImageUrl;
    public String wishTime;
    public String achieveTime;


    public BlessingRecordBean() {}


    protected BlessingRecordBean(Parcel in) {
        recordId = in.readInt();
        userId = in.readInt();
        blessingTime = in.readString();
        blessingId = in.readInt();
        blessingTitle = in.readString();
        blessingMethod = in.readString();
        blessingContent = in.readString();
        blessingAudioUrl = in.readString();
        blessingImageUrl = in.readString();
        wishTime = in.readString();
        achieveTime = in.readString();
    }

    public static final Creator<BlessingRecordBean> CREATOR = new Creator<BlessingRecordBean>() {
        @Override
        public BlessingRecordBean createFromParcel(Parcel in) {
            return new BlessingRecordBean(in);
        }

        @Override
        public BlessingRecordBean[] newArray(int size) {
            return new BlessingRecordBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recordId);
        dest.writeInt(userId);
        dest.writeString(blessingTime);
        dest.writeInt(blessingId);
        dest.writeString(blessingTitle);
        dest.writeString(blessingMethod);
        dest.writeString(blessingContent);
        dest.writeString(blessingAudioUrl);
        dest.writeString(blessingImageUrl);
        dest.writeString(wishTime);
        dest.writeString(achieveTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
