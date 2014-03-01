package com.april.uubdemos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.Menu;
import android.widget.ImageView;

import com.april.utils.Constant;
import com.april.utils.Utils;

public class MainActivity extends Activity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) this.findViewById(R.id.imageView1);

        callCamera();
        //        callGallery();
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

                Utils.log("imagePath : " + imagePath);

                // TODO 这里需要考虑是不是需要另存一份图片们到某个路径下，相应的数据库里就存该图片的uri

            }
            break;
        }

    }

}
