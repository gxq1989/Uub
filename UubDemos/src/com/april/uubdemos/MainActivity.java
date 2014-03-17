package com.april.uubdemos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.Menu;
import android.widget.ImageView;

import com.april.utils.Constant;
import com.april.utils.Utils;

public class MainActivity extends Activity {

    private ImageView mImageView;
    private File mStorePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) this.findViewById(R.id.imageView1);

        mStorePath = initStorePath(this);

        //        callCamera();
        callGallery();
    }

    private File initStorePath(Context context) {
        StringBuilder path = null;
        File file = null;
        //优先选择外部存储卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //正常
            File external = Environment.getExternalStorageDirectory();
            path = new StringBuilder(external.getPath());

        } else {
            // 内部存储
            File internal = Environment.getDataDirectory();
            // '/storage/sdcard0 ' 
            path = new StringBuilder(internal.getPath());
        }

        if (null != path) {
            path.append(Constant.URI_IMAGE);
            //若path不存在，则需创建
            file = new File(path.toString());
            if (!file.exists()) {
                boolean is = file.mkdirs();
                Utils.log("not exists");
                Utils.log("success :" + is);
            } else {
                Utils.log("exists");
            }

        }

        return file;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 调用系统相机
     */
    private void callCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(mStorePath.getAbsolutePath()));
        Utils.log("store path: " + Uri.parse(mStorePath.getAbsolutePath() + "/1.jpg"));
        startActivityForResult(intent, Constant.RESULT_CAMERA);
    }

    /**
     * 调用系统相册
     */
    private void callGallery() {
        Intent picture = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, Constant.RESULT_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case Constant.RESULT_CAMERA:
            Utils.log("resultCode = " + resultCode);
            if (Activity.RESULT_OK == resultCode && null != data) {
                Utils.log("yes");
                // 发intent时，没有指定MediaStore.EXTRA_OUTPUT时，才会返回data
                // 获取返回的bitmap
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                mImageView.setBackgroundDrawable(bitmapDrawable);

                // TODO 把图片存在某个路径下

            } else {
                Utils.log("no");

            }
            break;

        case Constant.RESULT_GALLERY:
            if (Activity.RESULT_OK == resultCode && null != data) {
                // 图片的Uri
                Uri uri = data.getData();
                String[] filePathColumns = { MediaStore.Images.Media.DATA };
                Cursor c = this.getContentResolver().query(uri, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imagePath = c.getString(columnIndex);
                c.close();

                //  03-17 17:56:42.304: D/UubDemos(5605): imagePath : /storage/sdcard0/DCIM/Camera/20140317_174236.jpg
                Utils.log("imagePath : " + imagePath);

                // 显示被选中的图片
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                mImageView.setBackgroundDrawable(bitmapDrawable);

                // TODO 这里需要考虑是不是需要另存一份图片们到某个路径下，相应的数据库里就存该图片的uri
                // 考虑在service里面做呀
                File imageFile = new File(imagePath);
                String imageName = imageFile.getName();
                StringBuilder desSb = new StringBuilder(mStorePath.getAbsolutePath()).append("/")
                        .append(Constant.IMAGE_PREFIX).append(imageName);
                try {
                    testCopyPic(imagePath, desSb.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
        }

    }

    private void testCopyPic(String src, String des) throws IOException {
        int byteRead = 0;

        File srcFile = new File(src);
        File desFile = new File(des);

        if (srcFile.exists()) {
            FileInputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(desFile);

            byte[] buffer = new byte[(int) srcFile.length()];
            Utils.log("src size : " + srcFile.length());

            while ((byteRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, byteRead);

            }
            inStream.close();
            outStream.close();

        } else {
            return;
        }

    }
}
