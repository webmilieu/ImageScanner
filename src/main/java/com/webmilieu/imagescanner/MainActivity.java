package com.webmilieu.imagescanner;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.File;
import android.os.Environment;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

    Button getimage, save, cancel;
    Bitmap bmp;
    ImageView orginalImageView, blackImageView;
    Uri myuri;
   // called on app create
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);                               //start layout called
        check();                                                      // check permission function called
        getimage = findViewById(R.id.startButton);
        getimage.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 101);
                setContentView(R.layout.main);                          //main function layout called
                orginalImageView = findViewById(R.id.image);

            }
        });
    }
    //Check Storage permission granted or not
    public  void check() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            REQUEST() ;}}
            //Requests storage permission
  public  void REQUEST(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Storage permission needed to save your edits")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                        public  void onClick(DialogInterface dialog,int which){
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                        }
                    }).create().show();
        }
        else{
            finishActivity(101);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)              //Check inputed data
    {
        if (resultCode==Activity.RESULT_OK)
            switch(requestCode){
                case 101:
                    Uri selectedimage=data.getData();
                    orginalImageView.setImageURI(selectedimage);
                    blackImageView =  findViewById(R.id.blackimage);
                    blackImageView.setImageBitmap(test(imageView2Bitmap(orginalImageView)));
                    blackImageView.setDrawingCacheEnabled(true);
                    blackImageView.buildDrawingCache();
                    bmp=blackImageView.getDrawingCache();
                    save=findViewById(R.id.save);
                    save.setOnClickListener(new Button.OnClickListener(){
                        public void onClick(View v){
                            save(bmp);
                        }
                    });
                    cancel=findViewById(R.id.cancel);
                    cancel.setOnClickListener(new Button.OnClickListener(){
                        public void onClick(View v){
                            cancel();
                        }
                    });
            }
    }
    // Image to bitamp
    private Bitmap imageView2Bitmap(ImageView view){
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        return bitmap;
    }
    // Conversion code
    public static Bitmap test(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                //int gray = (int) (0.32 * R + 0.32 * G + 0.32 * B);
                int gray = (int) (0.30 * R + 0.30 * G + 0.30 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 128) {
                    gray = 255;
                }
                else{
                    gray = 0;
                }
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }
    // Save file
    public void save(Bitmap b) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String newname = dtf.format(now);
        String dir="ImageScanner";
        File newdir=new File(Environment.getExternalStorageDirectory()+File.separator+dir);
        if(!newdir.exists())
            newdir.mkdirs();
        String fle = "/"+newdir+"/" + newname + ".jpg";
        try {
            OutputStream fOutputStream = new FileOutputStream(fle);

            b.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            try {
                fOutputStream.flush();
                fOutputStream.close();
                Toast.makeText(this,"Image Saved",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }


// Exit application

public void cancel(){
        setContentView(R.layout.start);
finish();
}
}
