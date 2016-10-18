package com.brendon.simplecameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button mTakePictureButton;
    ImageView mCameraPicture;

    private String mImageFilename = "temp_photo_file";
    private Uri mImageFileUri;

    private static final String PIRCTURE_TO_DISPLAY = "There is.";
    private boolean mIsPictureToDisplay = false;

    private static int TAKE_PICTURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            mIsPictureToDisplay = savedInstanceState.getBoolean(PIRCTURE_TO_DISPLAY, false);

        }

        mCameraPicture = (ImageView) findViewById(R.id.camera_picture);
        mTakePictureButton = (Button) findViewById(R.id.take_picture_button);

        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Checks if there is a camera app on device.
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                    File imageFile = new File(Environment.getExternalStorageDirectory(), mImageFilename);

                    mImageFileUri = Uri.fromFile(imageFile);

                    // Include URI as Extra
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);

                    startActivityForResult(pictureIntent, TAKE_PICTURE);

                } else {

                    Toast.makeText(MainActivity.this, "Bad", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE) {

            mIsPictureToDisplay = true;

        } else {

            mIsPictureToDisplay = false;
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && mIsPictureToDisplay) {

            Bitmap image = scaleBitmap();
            mCameraPicture.setImageBitmap(image);

            // This saves the file to your device.
            MediaStore.Images.Media.insertImage(getContentResolver(), image, "SimpleCameraApp", "Photo Taken by app.");

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(PIRCTURE_TO_DISPLAY, mIsPictureToDisplay);

    }

    private Bitmap scaleBitmap() {

        // Step 1: What size is the ImageView?
        int imageViewHeight = mCameraPicture.getHeight();
        int imageViewWidth = mCameraPicture.getWidth();

        // Step 2: Decode file.

        BitmapFactory.Options bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;

        File file = new File(Environment.getExternalStorageDirectory(), mImageFilename);
        Uri imageFileUri = Uri.fromFile(file);
        String photoFilePath = imageFileUri.getPath();
        BitmapFactory.decodeFile(photoFilePath, bOptions);

        int pictureHeight = bOptions.outHeight;
        int pictureWidth = bOptions.outWidth;

        //Step 3: Calculate scale factor(can use original picture)

        int scaleFactor = Math.min(pictureHeight / imageViewHeight, pictureWidth / imageViewWidth);

        //Step 4: Decode the file into a new Bitmap, Scaled to fit ImageView.

        bOptions.inJustDecodeBounds = false;
        bOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, bOptions);
        return bitmap;


    }


}
