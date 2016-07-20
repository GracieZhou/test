
package com.eostek.streamnetplusservice.service;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class MyMap implements Parcelable {

    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public MyMap(Map<String, String> map) {
        this.map = map;
    }

    public MyMap(Parcel source) {
        this.map = source.readHashMap(HashMap.class.getClassLoader());
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public static final Parcelable.Creator<MyMap> CREATOR = new Creator<MyMap>() {

        @Override
        public MyMap createFromParcel(Parcel source) {
            return new MyMap(source);
        }

        @Override
        public MyMap[] newArray(int size) {
            return new MyMap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(map);
    }
}
