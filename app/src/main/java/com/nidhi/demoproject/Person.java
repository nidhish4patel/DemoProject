package com.nidhi.demoproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nidhi on 8/16/2017.
 */

public class Person implements Parcelable {
    private String username;
    public Person() {
        super();
    }

    public Person(Parcel in) {
        this.username=in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int i) {
            return new Person[i];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    public String getUsername() {
        return username ;
    }

    public void setUsername(String firstname) {
        this.username = firstname;
    }

    @Override
    public void writeToParcel(Parcel in, int i) {
        in.writeString(this.username);

    }
}
