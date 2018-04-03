package com.contentproviderdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.contentproviderdemo.R;
import com.contentproviderdemo.adapter.ContactsRecyclerViewAdapter;
import com.contentproviderdemo.datamodel.ContactData;
import com.contentproviderdemo.interfaces.OnCardviewClick;
import com.contentproviderdemo.service.ContactFetchIntentServiceDemo;
import com.contentproviderdemo.utils.ContactAsyncTask;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener, OnCardviewClick {

    private RecyclerView mRvContacts;
    private ImageView mIvToolbar;
    private TextView mTvToolbar;
    private int mPosition;
    private ArrayList<ContactData> mContactDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        initializing();
        setToolbar();
        askPermissions();
        /*callingService();
        gettingBroadcast();*/
        new ContactAsyncTask(this).execute();
    }
    /**
     * getting broadcast from service ContactFetchIntentServiceDemo
     */
    /*
    private void gettingBroadcast(){
        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("BUNDLE");
                ArrayList<ContactData> contactData = (ArrayList<ContactData>) bundle.getSerializable("ContactList");
                setRvAdapter(contactData);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("ContactUpdates"));
    }

    */
    /**
     * calling service
     */
    /*private void callingService(){
        Intent i= new Intent(this, ContactFetchIntentServiceDemo.class);
        startService(i);
    }
*/
    /**
     * setting toolbar
     */
    private void setToolbar(){
        mTvToolbar.setText(R.string.s_contacts);
        mIvToolbar.setVisibility(View.VISIBLE);
        mIvToolbar.setOnClickListener(this);
    }

    /**
     * initialize views and recyclerview
     */
    private void initializing() {
        mRvContacts = findViewById(R.id.rv_contacts);
        mIvToolbar = findViewById(R.id.iv_toolbar);
        mTvToolbar = findViewById(R.id.tv_toolbar);
        mContactDataArrayList = new ArrayList<>();
    }

    /**
     * setting recyclerview adapter
     */
    public void setRvAdapter(ArrayList<ContactData> mContactDataArrayList) {
        this.mContactDataArrayList.addAll(mContactDataArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvContacts.setLayoutManager(layoutManager);
        mRvContacts.setAdapter(new ContactsRecyclerViewAdapter(this, this.mContactDataArrayList, this));
    }

    /**
     * asking permissions from user
     */
    private void askPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, 7);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, 7);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == 1) {
            new ContactAsyncTask(this).execute();
        } else {
            askPermissions();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_toolbar){
            startActivity(new Intent(ContactsActivity.this, CountDownActivity.class));
        }
    }

    @Override
    public void onCardviewClick(int position, View view) {
        mPosition = position;
        Intent intent = new Intent(ContactsActivity.this, EditActivity.class);
        if(mContactDataArrayList.get(position).getmImageUri()!=null) {
            intent.putExtra("photo_uri", mContactDataArrayList.get(position).getmImageUri().toString());
        }else {
            intent.putExtra("photo_uri", "");
        }
        intent.putExtra("contact_name", mContactDataArrayList.get(position).getmContactName());
        intent.putExtra("contact_number", mContactDataArrayList.get(position).getmContactNumber());
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == 3 && data!=null) {
            ContactData contactData = new ContactData();
            if(!data.getStringExtra("uri").equals(null)) {
                contactData.setmImageUri(Uri.parse(data.getStringExtra("uri")));
            }
            contactData.setmContactName(data.getStringExtra("name"));
            contactData.setmContactNumber(data.getStringExtra("contact"));
            mContactDataArrayList.set(mPosition, contactData);
            mRvContacts.getAdapter().notifyItemChanged(mPosition);
        }
    }

/*
   @Override
    public void sendData(ArrayList<ContactData> arrayList) {
        setRvAdapter(arrayList);
    }
*/


}
