package com.github.jruanodev.facedetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author Jose Ruano
 */

public class FaceDetectorActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnChooseGallery;

    private final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detector);

        final int CameraAndStoragePermissionID = 1000;

        btnChooseGallery = findViewById(R.id.btnChooseGallery);

        btnChooseGallery.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(FaceDetectorActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, CameraAndStoragePermissionID);
            return;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnChooseGallery:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                            null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);

                    cursor.close();

                    Intent resultIntent = new Intent(FaceDetectorActivity.this,
                            FaceDetectorResultActivity.class);

                    resultIntent.putExtra("image", imagePath);
                    startActivity(resultIntent);

                }

                break;
        }
    }
}
