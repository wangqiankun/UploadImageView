package com.alittletext.uploadimage;

import java.util.ArrayList;

/**
 * Created by wangqiankun on 08/03/2017.
 */

public interface UploadImageListener {
    void openPhotoAlbum(UploadImageView uploadImageView, ArrayList<String> strings);

    void uploadImages(ArrayList<String> pathList);
}
