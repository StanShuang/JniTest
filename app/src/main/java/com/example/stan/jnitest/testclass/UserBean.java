package com.example.stan.jnitest.testclass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author Stan
 * @Description
 * @Date 2023/3/1 16:35
 */
public class UserBean implements Parcelable {
    public int userId;
    public String userName;
    public boolean isMale;

    public UserBean(int userId, String userName, boolean isMale) {
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
    }

    protected UserBean(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readInt() == 1;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeInt(isMale ? 0 : 1);
    }
}
