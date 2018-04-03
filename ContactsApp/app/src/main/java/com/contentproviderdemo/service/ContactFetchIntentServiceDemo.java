package com.contentproviderdemo.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.contentproviderdemo.R;
import com.contentproviderdemo.datamodel.ContactData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by appinventiv on 7/3/18.
 */

public class ContactFetchIntentServiceDemo extends IntentService {

    private ProgressDialog mProgressDialog;
    private final Context mContext = this;
    private ContactData mContactData;
    private int mCounter =0 ;

    /**
     * Creates an IntentService.Invoked by your subclass's constructor.
     */
    public ContactFetchIntentServiceDemo() {
        super("ContactService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //settingProgressBar();
        ArrayList<ContactData> mContactDataArrayList = fetchContactData();
        //sendContactData.sendData(mContactDataArrayList);
        sendBroadcast(mContactDataArrayList);
    }
    /**
     * setting progress bar
     */
    /*
    private void settingProgressBar(){
        mProgressDialog = new ProgressDialog(mContext, R.style.MyDialog);
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setMessage("Loading contact details..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
    }*/


    /**
     * fetching contact from Contacts
     */

    private ArrayList<ContactData> fetchContactData(){
        ArrayList<ContactData> mContactDataArrayList = new ArrayList<>();
        Cursor mCursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        if (mCursor != null && mCursor.getCount() > 0) {
            do {
                mContactData = new ContactData();
                String mContactID = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
                getName(mCursor);
                getImage(mCursor);
                getNumber(mCursor, Long.parseLong(mContactID));
                mContactDataArrayList.add(mContactData);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return mContactDataArrayList;
    }


    /**
     * getting contact name
     */
    private void getName(Cursor cursor) {
        if (cursor.getCount() > 0 && cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))!=null){
            mContactData.setmContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
        }
    }

    /**
     * setting contact photo
     */
    private void getImage(Cursor cursor) {
        if(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))!=null)
            mContactData.setmImageUri(Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))));
    }

    /**
     * for contact number
     */
    private void getNumber(Cursor cursor, long contactID) {
        if (cursor.getCount() > 0) {
            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor cursorPhone = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        new String[]{String.valueOf(contactID)},
                        null);
                if (cursorPhone != null && cursorPhone.getCount() > 0) {
                    while (cursorPhone.moveToNext()) {
                        mContactData.setmContactNumber(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }
                    cursorPhone.close();
                }
            } else {
                mContactData.setmContactNumber(mContext.getString(R.string.s_dummy_contact));
            }
        }
    }

    /**
     * sending broadcast to activity
     * @param contactDataArrayList
     */
    private void sendBroadcast(ArrayList<ContactData> contactDataArrayList){
        Intent intent = new Intent("ContactUpdates");
        Bundle args = new Bundle();
        args.putSerializable("ContactList",(Serializable)contactDataArrayList);
        intent.putExtra("BUNDLE",args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
