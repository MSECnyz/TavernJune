package com.msecnyz.tavernjune.legionsupport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.MainActivity;
import com.msecnyz.tavernjune.R;

import java.io.File;
import java.io.FileNotFoundException;

public class PortraitActivity extends BaseActivity {

    ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);

        imageView = (ImageView)findViewById(R.id.bigportrait);
        initPortrait();
    }

    private void initPortrait(){
        File outputImage = new File(getExternalCacheDir(),"outputImage.jpg");
        if (outputImage.exists()){
            if (Build.VERSION.SDK_INT >= 24){
                //第二个参数为了使provider相应可以是任意字符串，要和Manifest里注册的一致
                imageUri = FileProvider.getUriForFile(this,"com.msecnyz.tavernjune.mainfragment0",outputImage);
            }else {
                imageUri = Uri.fromFile(outputImage);
            }
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
                PortraitActivity.this.finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        return true;
    }

}
