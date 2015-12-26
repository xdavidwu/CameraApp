package xdavidwu.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri imageuri;
    private Uri getSaveFileUri() {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CameraApp");
        if (dir.exists() == false) {
            if (dir.mkdirs() == false) {
                Toast.makeText(MainActivity.this, "Fail to create directory for storing!", Toast.LENGTH_SHORT).show();
            }
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        File savefile = new File(dir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return Uri.fromFile(savefile);
    }

    private void takePhoto(){
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageuri=getSaveFileUri();
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);

        startActivityForResult(camera_intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void takePhotoOnClick(View view){
        takePhoto();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ImageView imgv = (ImageView) findViewById(R.id.imgv);
            imgv.setVisibility(View.VISIBLE);
            Bitmap raw;

            if (data != null ) {
                Toast.makeText(MainActivity.this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                raw = BitmapFactory.decodeFile(data.getData().getPath());
            }
            else {
                Toast.makeText(MainActivity.this, "Image saved to:\n" + imageuri, Toast.LENGTH_LONG).show();
                raw = BitmapFactory.decodeFile(imageuri.getPath());
            }

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int rawwidth = raw.getWidth();
            int rawhight = raw.getHeight();

            if (rawwidth>width){
                float scale = (float) width/rawwidth;
                Matrix matrix = new Matrix();
                matrix.postScale(scale,scale);
                Bitmap cooked = Bitmap.createBitmap(raw,0,0,rawwidth,rawhight,matrix,true);
                imgv.setImageBitmap(cooked);
            }
            else imgv.setImageBitmap(raw);

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Fail to capture!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
