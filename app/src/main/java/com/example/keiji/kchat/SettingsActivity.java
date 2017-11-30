package com.example.keiji.kchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    /* CONSTANTS
    * ---------------------------------------------------*/
    private static final String TAG = "==> Keiji_SETTINGS";


    /* Global properties vars and instances
    * ---------------------------------------------------*/
    // firebase user
    FirebaseUser mCurrentUser;

    // firebase db
    DatabaseReference mUserDatabase;

    // UI elements
    CircleImageView mCircleImageView;
    TextView    mDisplayName,
                mStatus;
    Button  mChangeImage,
            mChangeStatus;

    // gallery pick
    private static final int GALLERY_PICK_CODE = 1;

    // create firebase storage ref to store profile image
    private StorageReference mImageStorage;

    // progress dialog
    private ProgressDialog mProgressDialog;


    /**
     * CONSTRUCTOR
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize UI elements
        mCircleImageView = (CircleImageView)findViewById(R.id.settings_image_civ);
        mDisplayName = (TextView)findViewById(R.id.settings_displayName_tv);
        mStatus = (TextView)findViewById(R.id.settings_status_tv);
        mChangeImage = (Button)findViewById(R.id.settings_changeImage_btn);
        mChangeStatus = (Button)findViewById(R.id.settings_changeStatus_btn);


        // initialize firebase user and get the id
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mCurrentUser.getUid();

        // initialize firebase db and point to the current user db
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        // initialize firebase storage reference (root of the storage, still need to point to profile_image folder)
        mImageStorage = FirebaseStorage.getInstance().getReference();

        // retrieve data from database using addValueEventListener
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get user's values from db
                String  name        = dataSnapshot.child("name").getValue().toString(),
                        image       = dataSnapshot.child("image").getValue().toString(),
                        thumbImage  = dataSnapshot.child("thumb_image").getValue().toString(),
                        status      = dataSnapshot.child("status").getValue().toString();

                // update the ui elements with data from db
                mDisplayName.setText(name);
                mStatus.setText(status);

                // update image, note: use picasso library: https://square.github.io/picasso
                // check if the image is not default (if there is no image set)
                if (!image.equals("default")) {

                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mCircleImageView);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });//end valueeventlistener

        // onClick listener for change status btn
        mChangeStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            // get value of the current status
            String current_status = mStatus.getText().toString();

            // send user to Status Activity with status data
            Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
            statusIntent.putExtra("statusValue", current_status);
            startActivity(statusIntent);

        }//end onclick

    });//end status onclick listener

        // onclick listener for change image
        mChangeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // using regular image picker
                // start intent to pick image from gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // start activity for result
                // use gallery intent to open the image picker
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK_CODE);

                /*
                // using Android Image Cropper library
                // https://github.com/ArthurHub/Android-Image-Cropper
                // 1. If you want to use the Image picker from the Image Cropper library
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                // and GALLERPICKCODE = 203
                */



            }//end onclick

        });//end of mchange image onclick listener

    }//--end of CONSTRUCTOR


    /**
     * gets results from the gallery Intent created in the CONSTRUCTOR
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK) {

            // get uri of the image
            Uri imageUri = data.getData();

            // start cropping using Android Image Cropper library
            // use setaspectratio to limit the cropping to square resolution
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

        }//request code check


        // get the uri of the new cropped image
        // check if the result is from the crop activity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            // store the data in the result var
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            // if result ok start upload
            if (resultCode == RESULT_OK) {

                // start progress bar
                mProgressDialog = new ProgressDialog( SettingsActivity.this);
                mProgressDialog.setTitle("Uploading image");
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                // get cropped image uri
                Uri resultUri = result.getUri();

                // dl file from uri and create a file
                final File thumbFilePath = new File(resultUri.getPath());

                // get user id to use in the filename
                String currentUserId = mCurrentUser.getUid();

                // compressing the image
                Bitmap thumbBitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(75).compressToBitmap(thumbFilePath);

                // preparing the bitmap image to be stored in firebase (code from firebase)
                // firebase.google.com/docs/storage/android/upload-files
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumbByte = baos.toByteArray();

                // store the croped image in the profile_images folder in firebase using
                // mImageStorage var that is pointing to the root right now
                // also, pass two childs, one is the profile_images folder and the other is the file name
                StorageReference filePath = mImageStorage.child("profile_images").child(currentUserId + ".jpg");
                // storing the thumb image (needs to be final)
                final StorageReference thumbPath = mImageStorage.child("profile_images").child("thumbs").child(currentUserId + ".jpg");

                Log.d(TAG, "cropped image uri: " + resultUri.toString());

                // send file to firebase using the uri of the cropped image
                // tip: add a oncomplete listener to check if all went ok
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            // store link in the firebase user object in the db
                            // get download uri
                            // note: use supresswarnings for getdownloadurl
                            @SuppressWarnings("VisibleForTests") final
                            String downloadUrl = task.getResult().getDownloadUrl().toString();

                            // upload task for the bitmap image
                            UploadTask uploadTask = thumbPath.putBytes(thumbByte);
                            // add an on complete listener to the task
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

                                    // set DL url for the thumb
                                    @SuppressWarnings("VisibleForTests")
                                    String thumbDownloadUrl = thumbTask.getResult().getDownloadUrl().toString();

                                    // if task is successfull
                                    if (thumbTask.isSuccessful()) {

                                        // updating the data (overwrite the previeous)
                                        Map updateHashMap = new HashMap<>();
                                        updateHashMap.put("image", downloadUrl);
                                        updateHashMap.put("thumb_image", thumbDownloadUrl);

                                        // set in db using the hashmap (update children instead of setvalue)
                                        mUserDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            // if successful then dismiss dialog
                                            if (task.isSuccessful()) {

                                                mProgressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Image updated.", Toast.LENGTH_SHORT).show();

                                            }

                                        }//end oncomplete

                                    });//end oncompletelistener

                                    }//--end issuccessfull check
                                    else {

                                        Toast.makeText(SettingsActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();

                                        // dismiss progress dialog
                                        mProgressDialog.dismiss();

                                    }

                                }//--end of oncomplete nested function

                            });//--end of oncompletelistener anonymous function

                        }
                        else {

                            Toast.makeText(SettingsActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();

                            // dismiss progress dialog
                            mProgressDialog.dismiss();

                        }

                    }// end oncomplete

                });//end send file to firebase

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }//end of check if result ok

        }//end of get crop image uri



    }//--end of onActivityResult FUNCTION


    /**
     * Generates a random string
     * @return randomStringBuilder
     */
    public static String randomString() {

        Random generator = new Random();

        StringBuilder randomStringBuilder = new StringBuilder();

        int maxLength = 50;

        int randomLength = generator.nextInt(maxLength);

        char tempChar;

        for (int i = 0; i < randomLength; i++){

            tempChar = (char) (generator.nextInt(96) + 32);

            randomStringBuilder.append(tempChar);

        }

        return randomStringBuilder.toString();

    }//--end of randomString FUNCTION


}//--end SettingsActivity ACTIVITY
