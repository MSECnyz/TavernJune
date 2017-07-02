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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.UserListAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;

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
    private static final int CHOOSE_PHOTO = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        portrait = (ImageView)v.findViewById(R.id.users_portrait);
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
                            openAlbum();
                        }
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
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this.getActivity(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+ "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //若是file形的uri，直接获得图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

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
}
