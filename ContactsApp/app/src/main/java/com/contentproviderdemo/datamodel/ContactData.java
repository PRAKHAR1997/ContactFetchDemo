package com.contentproviderdemo.datamodel;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 21/2/18.
 */

public class ContactData{
    public ContactData(){}
    private Uri mImageUri;
    private String mContactName, mContactNumber;

    public Uri getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }

    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

}
