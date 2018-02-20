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

        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(image, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        if(faceDetector.isOperational()) {
            Frame frame = new Frame.Builder().setBitmap(image).build();
            SparseArray<Face> faceSparseArray = faceDetector.detect(frame);
            ArrayList<String> detailsArrayList = new ArrayList<>();

            for(int i = 0; i < faceSparseArray.size(); i++) {
                Face face = faceSparseArray.valueAt(i);

                DecimalFormat decimalFormat = new DecimalFormat("##.#");
                float probability = Float.valueOf(decimalFormat.format(face.getIsSmilingProbability() * 100));
                if(face.getIsSmilingProbability() == -1.0)
                    detailsArrayList.add("Smiling probabilty of face " + (i + 1) + ": Unknown");
                else
                    detailsArrayList.add("Smiling probabilty of face " + (i + 1) + ": " + probability + " %");

                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1 + face.getWidth();
                float y2 = y1 + face.getHeight();

                RectF rectF = new RectF(x1, y1, x2, y2);
                canvas.drawRoundRect(rectF, 2, 2, paint);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, detailsArrayList);
            detailsListView.setAdapter(adapter);

            resultImageView.setImageBitmap(tempBitmap);
        }

    }
}
