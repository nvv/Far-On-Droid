package com.openfarmanager.android.model.actions

import android.os.Parcel
import android.os.Parcelable
import com.openfarmanager.android.R

enum class FileAction(val resId: Int) : Parcelable {

    INFO(R.string.info),
    COPY(R.string.copy),
    DELETE(R.string.delete);

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileAction> {
        override fun createFromParcel(parcel: Parcel): FileAction {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<FileAction?> {
            return arrayOfNulls(size)
        }
    }

}