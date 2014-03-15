package com.openfarmanager.android.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ParcelableWrapper<T> implements Parcelable, Serializable {

    public T value;

    public ParcelableWrapper(T object) {
        value = object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        try {
            parcel.writeValue(value);
        } catch (Exception ignore) {
        }
    }

    public final Parcelable.Creator<ParcelableWrapper> CREATOR = new Parcelable.Creator<ParcelableWrapper>() {
        public ParcelableWrapper createFromParcel(Parcel source) {
            try {
                //noinspection unchecked
                return new ParcelableWrapper<T>((T) source.readValue(ClassLoader.getSystemClassLoader()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //noinspection unchecked
            return new ParcelableWrapper<T>((T) new Object());
        }

        public ParcelableWrapper[] newArray(int size) {
            throw new UnsupportedOperationException();
        }

    };
}
