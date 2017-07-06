package com.msecnyz.tavernjune.mainfragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.FirstActivity;
import com.msecnyz.tavernjune.MainActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.legionsupport.PortraitActivity;
import com.msecnyz.tavernjune.listitem.UserListAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserFragment extends Fragment {

    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;
    private List<ImageTextItem> optionList = new ArrayList<>();
    private Button userExit;
    private TextView setUserId;
    private ImageView portrait;
    private Uri imageUri;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int RESULT_PHOTO = 3;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        portrait = (ImageView)v.findViewById(R.id.users_portrait);
        initPortrait();
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PortraitActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        mainBar = (Toolbar)v.findViewById(R.id.mainbar_user);
        mainBar.inflateMenu(R.menu.usermenu);
        mainBar.setTitle("更多");
        mainBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_portrait:
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }else {
                            File outputImage = new File(getActivity().getExternalCacheDir(),"outputImage.jpg");
                            try {
                                if (outputImage.exists()){
                                    outputImage.delete();
                                    outputImage.createNewFile();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (Build.VERSION.SDK_INT >= 24){
                                //第二个参数为了使provider相应可以是任意字符串，要和Manifest里注册的一致
                                imageUri = FileProvider.getUriForFile(getActivity(),"com.msecnyz.tavernjune.mainfragment0",outputImage);
                            }else {
                                imageUri = Uri.fromFile(outputImage);
                            }
                            openAlbum();
                        }
                        break;
                    case R.id.user_takingnew:
                        //getExternalCacheDir()方法得到应用关联目录，不需要SD卡读取危险权限
                        File outputImage = new File(getActivity().getExternalCacheDir(),"outputImage.jpg");
                        try {
                            if (outputImage.exists()){
                                outputImage.delete();
                                outputImage.createNewFile();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= 24){
                            //第二个参数为了使provider相应可以是任意字符串，要和Manifest里注册的一致
                            imageUri = FileProvider.getUriForFile(getActivity(),"com.msecnyz.tavernjune.mainfragment0",outputImage);
                        }else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(intent,TAKE_PHOTO);
                        break;
                    default:break;
                }
                return true;
            }
        });

        if (optionList.size()==0){
            initList();
        }
        UserListAdapter adapter = new UserListAdapter(this.getActivity(),R.layout.item_useroption,optionList);
        ListView listView = (ListView)v.findViewById(R.id.userlist);
        listView.setAdapter(adapter);

        final SharedPreferences sharedPreferences = UserFragment.this.getActivity().getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);

        setUserId = (TextView)v.findViewById(R.id.setuserid);
        setUserId.setText(sharedPreferences.getString("userId","userId"));


        userExit = (Button)v.findViewById(R.id.userexit);
        userExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId","我的ID");
                editor.putString("password","");
                editor.putString("autoLogIn","false");
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(UserFragment.this.getActivity(), FirstActivity.class);
                intent.putExtra("extraData","userExit");
                UserFragment.this.getActivity().startActivity(intent);
            }
        });

        return v;
    }

    private void initList(){
        ImageTextItem optionA = new ImageTextItem("这是第一个暂时没用的选项",R.drawable.userlistitem1);
        optionList.add(optionA);
        ImageTextItem optionB = new ImageTextItem("这是第二个暂时没用的选项",R.drawable.userlistitem2);
        optionList.add(optionB);
        ImageTextItem optionC = new ImageTextItem("下面是第四个暂时没用的选项",R.drawable.userlistitem3);
        optionList.add(optionC);
        ImageTextItem optionD = new ImageTextItem("上面是第三个暂时没用的选项",R.drawable.userlistitem4);
        optionList.add(optionD);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this.getActivity(),"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    tailorPhoto(imageUri,imageUri);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    //4.4版本以上必须解析封装过的Uri才能得到图片资源
//                    if (Build.VERSION.SDK_INT >= 19){
//                        handleImageOnKitKat(data);
//                    }else {
//                        handleImageBeforeKitKat(data);
//                    }
                    tailorPhoto(data.getData(),imageUri);
                }
                break;
            case RESULT_PHOTO:
                //保证结果码为确认进行操作
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        portrait.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
                break;
        }
    }
    //4.4以上版本Android需要对Uri进行解析
//    @TargetApi(19)
//    private void handleImageOnKitKat(Intent data){
//        String imagePath = null;
//        Uri uri = data.getData();
//        Uri realUri;
//        if (DocumentsContract.isDocumentUri(this.getActivity(),uri)){
//            String docId = DocumentsContract.getDocumentId(uri);
//            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
//                String id = docId.split(":")[1];//解析出数字格式的id
//                String selection = MediaStore.Images.Media._ID+ "=" + id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
//                realUri = Uri.parse(imagePath);
//            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
//                imagePath = getImagePath(contentUri,null);
//            }
//        }else if ("content".equalsIgnoreCase(uri.getScheme())){
//            //如果是content类型的Uri,则使用普通方式处理
//            imagePath = getImagePath(uri,null);
//        }else if ("file".equalsIgnoreCase(uri.getScheme())){
//            //若是file形的uri，直接获得图片路径即可
//            imagePath = uri.getPath();
//        }
//        displayImage(imagePath);//根据图片路径显示图片
//    }
//
//    private void handleImageBeforeKitKat(Intent data){
//        Uri uri = data.getData();
//        String imagePath = getImagePath(uri,null);
//        displayImage(imagePath);
//    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if (imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            portrait.setImageBitmap(bitmap);
        }else {
            Toast.makeText(getActivity(),"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }


    public void tailorPhoto(Uri inputUri,Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri,"image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片XY像素大小
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("scale", true);
        //return如果设置为false，则不会返回data,一个Bitmap数据(？)
        // 这个uri关联了一张外存设备中的一张图片，就是裁剪后的图片,若需要裁剪出大图，则选择存在Uri里
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        //压缩格式JPEG
        intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
        // 是否去除面部检测， 如果需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, RESULT_PHOTO);
    }

    private void initPortrait(){
            File outputImage = new File(getActivity().getExternalCacheDir(),"outputImage.jpg");
            if (outputImage.exists()){
                if (Build.VERSION.SDK_INT >= 24){
                    //第二个参数为了使provider相应可以是任意字符串，要和Manifest里注册的一致
                    imageUri = FileProvider.getUriForFile(getActivity(),"com.msecnyz.tavernjune.mainfragment0",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                portrait.setImageBitmap(bitmap);
            }
    }
}
