package com.example.smart_ar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button cap_btn,det_btn;
    private ImageView imageView;
    private TextView textView;
    public Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cap_btn=findViewById(R.id.cap_button);
        det_btn=findViewById(R.id.det_button);
        imageView=findViewById(R.id.imgv1);
        textView=findViewById(R.id.dsp_text);


        cap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });

        det_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect_from_img();
            }
        });
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detect_from_img() {
  FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
  FirebaseVisionTextRecognizer textRecognizer= FirebaseVision.getInstance().getOnDeviceTextRecognizer();
  textRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
      @Override
      public void onSuccess(FirebaseVisionText firebaseVisionText) {
            displaytextondevice(firebaseVisionText);
      }
  }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
          Toast.makeText(MainActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT);
      }
  });

    }

    private void displaytextondevice(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.TextBlock> blockList=firebaseVisionText.getTextBlocks();

        if(blockList.size()==0)
        {
            Toast.makeText(this,"no text was detected ",Toast.LENGTH_SHORT);
        }
        else
        {
            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
            {
                String text=block.getText();
                textView.setText(text);
            }
        }
    }
}
