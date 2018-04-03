package com.contentproviderdemo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.contentproviderdemo.R;
import com.contentproviderdemo.activity.ContactsActivity;
import com.contentproviderdemo.datamodel.ContactData;

import java.util.ArrayList;

/**
 * Created by root on 21/2/18.
 */

public class ContactAsyncTask extends AsyncTask<String, String, ArrayList<ContactData>> {

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private ContactData mContactData;
    private int mCounter =0 ;
    public ContactAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        settingProgressBar();

    }

    @Override
    protected ArrayList<ContactData> doInBackground(String... strings) {
        ArrayList<ContactData> mContactDataArrayList = new ArrayList<>();
        Cursor mCursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        if (mCursor != null && mCursor.getCount() > 0) {
            do {
                publishProgress(String.valueOf(mCounter++));
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

    @Override
    protected void onPostExecute(ArrayList<ContactData> contactDataArrayList) {
        super.onPostExecute(contactDataArrayList);
        //validate context instance
        ((ContactsActivity) mContext).setRvAdapter(contactDataArrayList);
        mProgressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(Integer.parseInt(String.valueOf(values[0])));
    }

    /**
     * setting progress bar
     */
    private void settingProgressBar(){
        mProgressDialog = new ProgressDialog(mContext, R.style.MyDialog);
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setMessage("Loading contact details..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
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
