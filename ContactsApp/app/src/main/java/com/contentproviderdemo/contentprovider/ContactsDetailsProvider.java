package com.contentproviderdemo.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.contentproviderdemo.R;
import com.contentproviderdemo.datamodel.ContactData;

import java.util.ArrayList;

/**
 * Created by root on 21/2/18.
 */

public class ContactsDetailsProvider {

    private ContactData mContactData;
    private Context mContext;

    public ContactsDetailsProvider(Context context) {
        this.mContext = context;
    }

    public ArrayList<ContactData> retrieveData(){
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

}
