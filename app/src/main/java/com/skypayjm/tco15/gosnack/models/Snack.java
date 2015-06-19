package com.skypayjm.tco15.gosnack.models;

import java.io.Serializable;

// Container class to hold Snack information.
public class Snack implements Serializable {
    public int mId;
    private String mName;
    private int mThumbnailDrawable;
    private String mNumber;

    public Snack(int id, String name, int thumbnailDrawable, String number) {
        mId = id;
        mName = name;
        mThumbnailDrawable = thumbnailDrawable;
        mNumber = number;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getThumbnailDrawable() {
        return mThumbnailDrawable;
    }

    public String getNumber() {
        return mNumber;
    }
}
