package edu.phystech.annnigm.cards

import android.os.Parcel
import android.os.Parcelable
import edu.phystech.annnigm.MeasureType


data class Card(val type: MeasureType,                       // eat
                val description: String,                // big club melt + souses
                val time: String,                        // mm:ss dd.MM.yyyy
                var serverId: Int = -1
) : Parcelable {     // time
    constructor(parcel: Parcel) : this(
        MeasureType.valueOf(parcel.readString()),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.toString())
        parcel.writeString(description)
        parcel.writeString(time)
        parcel.writeInt(serverId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }

}