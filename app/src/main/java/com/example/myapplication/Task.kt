package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var id: String = "", // Change to Long type
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    var timestamp: Long = System.currentTimeMillis()
) : Parcelable {

    // No-argument constructor for Firebase deserialization
    constructor() : this("", "", "", false, System.currentTimeMillis())

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // Read id as Long
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id) // Write id as Long
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
