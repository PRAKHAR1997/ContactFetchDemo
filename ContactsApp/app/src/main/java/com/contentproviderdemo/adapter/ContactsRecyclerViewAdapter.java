package com.contentproviderdemo.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.contentproviderdemo.R;
import com.contentproviderdemo.datamodel.ContactData;
import com.contentproviderdemo.interfaces.OnCardviewClick;

import java.util.ArrayList;

/**
 * Created by root on 21/2/18.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private OnCardviewClick mOnCardviewClick;
    private ArrayList<ContactData> mContactDataArrayList = new ArrayList<>();

    public ContactsRecyclerViewAdapter(Context context, ArrayList<ContactData> mContactDataArrayList, OnCardviewClick mOnCardviewClick) {
        this.mContext = context;
        this.mContactDataArrayList = mContactDataArrayList;
        this.mOnCardviewClick = mOnCardviewClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_card_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //using Glide to set image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(mContext).load(mContactDataArrayList.get(position).getmImageUri())
                .apply(requestOptions).apply(RequestOptions.circleCropTransform())
                .into(holder.mIvContactPhoto);
        holder.mTvContactName.setText(mContactDataArrayList.get(position).getmContactName());
        holder.mTvContactPhoneNumber.setText(mContactDataArrayList.get(position).getmContactNumber());
    }

    @Override
    public int getItemCount() {
        return mContactDataArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvContactPhoto;
        private TextView mTvContactName, mTvContactPhoneNumber;

        private ViewHolder(View itemView) {
            super(itemView);
            mIvContactPhoto = itemView.findViewById(R.id.iv_contact_photo);
            mTvContactName = itemView.findViewById(R.id.tv_contact_name);
            mTvContactPhoneNumber = itemView.findViewById(R.id.tv_contact_number);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCardviewClick.onCardviewClick(getAdapterPosition(),v);
                }
            });
        }
    }

}
