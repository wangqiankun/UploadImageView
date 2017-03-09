package com.alittletext.uploadimage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 上传图片用，避免加入与业务相关的成员变量
 * Created by wqk on 16/9/13.
 */
public class UploadImageBean implements Parcelable {

    public UploadImageBean() {

    }

    public UploadImageBean(String localPath) {
        this.localPath = localPath;
    }

    public UploadImageBean(String netUrl, boolean isUploaded, boolean isUploadSucceed) {
        this.netUrl = netUrl;
        this.isUploaded = isUploaded;
        this.isUploadSucceed = isUploadSucceed;
    }

    public String localPath;
    public String localName;
    public boolean isUploaded; //是否已经调用上传接口
    public boolean isUploadSucceed;
    public String netUrl;

    protected UploadImageBean(Parcel in) {
        localPath = in.readString();
        localName = in.readString();
        isUploaded = in.readByte() != 0;
        isUploadSucceed = in.readByte() != 0;
        netUrl = in.readString();
    }

    public static final Creator<UploadImageBean> CREATOR = new Creator<UploadImageBean>() {
        @Override
        public UploadImageBean createFromParcel(Parcel in) {
            return new UploadImageBean(in);
        }

        @Override
        public UploadImageBean[] newArray(int size) {
            return new UploadImageBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localPath);
        dest.writeString(localName);
        dest.writeByte((byte) (isUploaded ? 1 : 0));
        dest.writeByte((byte) (isUploadSucceed ? 1 : 0));
        dest.writeString(netUrl);
    }
}
