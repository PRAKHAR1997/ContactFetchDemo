package com.contentproviderdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.contentproviderdemo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvProfilePic;
    private TextView mTvToolbarName;
    private String[] mEditProfileArray;
    private AlertDialog mAlertDialog;
    private EditText mEtName, mEtContact;
    private Intent mIntent;
    private Uri mUri;
    private Button mBtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initializeViewID();
        setValueInEdittext();
        mTvToolbarName.setText(R.string.s_edit);
        setOnClickListener();
    }
    /**
     * initializong array and views
     */
    private void initializeViewID() {
        mIntent = getIntent();
        mBtnSave = findViewById(R.id.btn_save);
        mIvProfilePic = findViewById(R.id.iv_user_image);
        mTvToolbarName = findViewById(R.id.tv_toolbar);
        mEditProfileArray = getResources().getStringArray(R.array.s_array_edit_image);
        mEtContact = findViewById(R.id.et_phone_number);
        mEtName = findViewById(R.id.et_name);
    }

    /**
     * setting image on click
     */
    private void setOnClickListener() {
        mIvProfilePic.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_image:
                mAlertDialog = openDialog().create();
                mAlertDialog.show();
                break;
            case R.id.btn_save:
                sendDataUsingIntent();
                break;
        }
    }

    /**
     * setting alery dialog
     * @return object of AlertDialog.Builder
     */
    private AlertDialog.Builder openDialog() {
        return new AlertDialog.Builder(this).
                setTitle(Html.fromHtml("<font color='#000000'>" + getString(R.string.s_change_picture) + "</font>")).
                setItems(mEditProfileArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                    mAlertDialog.dismiss();
                                    galleryEvent();
                                } else {
                                    mAlertDialog.dismiss();
                                    askGalleryPermissions();
                                }
                                break;
                            case 1:
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                    mAlertDialog.dismiss();
                                    cameraEvent();

                                } else {
                                    mAlertDialog.dismiss();
                                    askCameraPermissions();
                                }
                                break;
                            case 2:
                                mAlertDialog.dismiss();
                                removeImage();
                                break;
                            case 3:
                                mAlertDialog.dismiss();
                                break;
                        }
                    }
                });
    }

    /**
     * asking camera permissions
     */
    private void askCameraPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        } else {
            if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraEvent();
            } else {
                if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (shouldShowRequestPermissionRationale(CAMERA)) {
                    requestPermissions(new String[]{CAMERA}, 8);
                } else {
                    requestPermissions(new String[]{CAMERA}, 8);
                }
                if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 8);
                } else {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 8);
                }
            }
        }
    }

    /**
     * asking external storage permissions
     */
    private void askGalleryPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        } else {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                galleryEvent();
            }
            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 9);
            } else {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 9);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 1 && requestCode == 8) {
            cameraEvent();
        } else if (grantResults.length > 0 && requestCode == 9) {
            galleryEvent();
        }
    }

    /**
     *set image from camera
     */
    private void cameraEvent(){
        mAlertDialog.dismiss();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String photoFileName = null;
            try {
                photoFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFileName != null) {
                mUri = FileProvider.getUriForFile(this, "com.contentproviderdemo.activity.provider", getPhotoFileUri(photoFileName));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }

    /**
     *set Image from gallery
     */
    private void galleryEvent(){
        mAlertDialog.dismiss();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    /**
     *remove image from contact
     */
    private void removeImage(){
        mAlertDialog.dismiss();
        mUri = null;
        setImage(mIvProfilePic, mUri);
    }

    /**
     * setting data from intent to edittext
     */
    private void setValueInEdittext(){
        mEtName.setText(mIntent.getStringExtra("contact_name"));
        mEtContact.setText(mIntent.getStringExtra("contact_number"));
        setImage(mIvProfilePic, Uri.parse(mIntent.getStringExtra("photo_uri")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null){
            mUri = data.getData();
           setImage(mIvProfilePic, mUri);
        }else if(requestCode == 2 && resultCode == RESULT_OK && data!=null){
            setImage(mIvProfilePic, mUri);
        }
    }


    /**
     * setting image to imageview using glide
     * @param imageView image view where you want to set picture
     * @param uri uri of the image
     */
    private void setImage(ImageView imageView, Uri uri){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(this).load(uri)
                .apply(requestOptions).apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    /**
     * create image file
     * @return image file
     * @throws IOException
     */
    private String createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DSC_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(imageFileName,".jpeg", storageDir);
        String mCurrentPhotoPath = image.getAbsolutePath();
        return mCurrentPhotoPath;
    }

    /**
     * creating image file of path filename
     * @param fileName
     * @return
     */
    public File getPhotoFileUri(String fileName) {
            File file = new File(fileName);
            return file;
    }

    /**
     * used to send  data back to Contact Activity
     */
    private void sendDataUsingIntent(){
        Intent mSendIntent = new Intent();
                if(mUri!=null){
                    mSendIntent.putExtra("uri", mUri.toString());
                }else{
                    mSendIntent.putExtra("uri", "");
                }
                    mSendIntent.putExtra("name", mEtName.getText().toString());
                    mSendIntent.putExtra("contact", mEtContact.getText().toString());
                    setResult(3, mSendIntent);
                    finish();
    }
}
