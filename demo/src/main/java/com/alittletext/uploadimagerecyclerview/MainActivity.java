package com.alittletext.uploadimagerecyclerview;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alittletext.uploadimage.UploadImageListener;
import com.alittletext.uploadimage.UploadImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    UploadImageView uploadImageView;

    IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
        @Override
        public void onStart() {
            Log.i(TAG, "onStart: 开启");
        }

        @Override
        public void onSuccess(List<String> photoList) {
            Log.i(TAG, "onSuccess: 返回数据");
            for (String s : photoList) {
                Log.i(TAG, s);
            }
            uploadImageView.setSelectImages4Client(photoList);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel: 取消");
        }

        @Override
        public void onFinish() {
            Log.i(TAG, "onFinish: 结束");
        }

        @Override
        public void onError() {
            Log.i(TAG, "onError: 出错");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        uploadImageView = (UploadImageView) findViewById(R.id.upload_image);


        uploadImageView.setUploadImageListener(new UploadImageListener() {
            @Override
            public void openPhotoAlbum(UploadImageView uploadImageView, ArrayList<String> strings) {
                GalleryConfig galleryConfig = new GalleryConfig.Builder()
                        .imageLoader(new FrescoImageLoader(MainActivity.this))    // ImageLoader 加载框架（必填）
                        .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                        .provider("com.alittletext.uploadimagerecyclerview.fileprovider")   // provider (必填)
                        .pathList(strings)                         // 记录已选的图片
                        .multiSelect(true, 9)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                        .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                        .filePath("/Gallery/Pictures")          // 图片存放路径
                        .build();
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(MainActivity.this);
            }

            @Override
            public void uploadImages(final ArrayList<String> pathList) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (String s : pathList) {
                                    uploadImageView.uploadImageSucceed(s, "http//" + s);
                                }
                                uploadImageView.uploadImageEnd();
                            }
                        });

                    }
                }).start();
            }
        });
    }

}
