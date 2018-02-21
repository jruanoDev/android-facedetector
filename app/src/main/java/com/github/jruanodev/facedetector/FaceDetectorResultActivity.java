package com.github.jruanodev.facedetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Jose Ruano
 */

public class FaceDetectorResultActivity extends AppCompatActivity {
    ImageView resultImageView;
    ImageButton btnBack;
    ListView detailsListView;
    Bitmap image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detector_result);

        resultImageView = findViewById(R.id.resultImageView);
        detailsListView = findViewById(R.id.detailsListView);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent comingIntent = getIntent();
        image = BitmapFactory.decodeFile(comingIntent.getStringExtra("image"));

        analyzeImage();
    }

    public void analyzeImage() {

        // New paint with no fill no draw the rectangle
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        // Create a temporal Bitmap of the same size of the original
        Bitmap tempBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(image, 0, 0, null);

        // Instantiate the FaceDetector Object (tracking ENABLED to use with getId())
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Check if the detector is available
        if(faceDetector.isOperational()) {

            // Create new frame and set the original bitmap
            Frame frame = new Frame.Builder().setBitmap(image).build();

            // Get all the detected faces
            SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

            // Make a list to storage each one of the face property
            ArrayList<String> detailsArrayList = new ArrayList<>();

            for (int i = 0; i < faceSparseArray.size(); i++) {
                Face face = faceSparseArray.valueAt(i);

                // Format the probability and populate the list with it
                DecimalFormat decimalFormat = new DecimalFormat("##.#");
                float probability = Float.valueOf(decimalFormat.format(face.getIsSmilingProbability() * 100));
                if (face.getIsSmilingProbability() == -1.0)
                    // If smile is not recognized display Unknown
                    detailsArrayList.add("Smiling probabilty of face " + (face.getId()) + ": Unknown");
                else
                    detailsArrayList.add("Smiling probabilty of face " + (face.getId()) + ": " + probability + " %");

                // Get the bounds of the face to draw the rectangle
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1 + face.getWidth();
                float y2 = y1 + face.getHeight();

                // Make the rectangle with the desired bounds and draw it to a Canvas
                RectF rectF = new RectF(x1, y1, x2, y2);
                canvas.drawRoundRect(rectF, 2, 2, paint);

                // Create new paint with fill to draw the text
                Paint textPaint = new Paint();
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setColor(Color.GREEN);

                // Calculate the text size depending on the with of the rectangle
                int textSize = 0;
                while (textPaint.measureText("Hola") < rectF.width()) {
                    textPaint.setTextSize(++textSize);
                }

                // Draw the text inside each rectangle
                canvas.drawText("" + face.getId(), rectF.centerX(), rectF.centerY(), textPaint);

            }

            // Make a ArrayAdapter and populate the ListView with the data
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, detailsArrayList);
            detailsListView.setAdapter(adapter);

            // Set the final Bitmap on the ImageView
            resultImageView.setImageBitmap(tempBitmap);
        }
    }
}
