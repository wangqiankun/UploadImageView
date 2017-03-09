package com.alittletext.uploadimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 上传图片用
 * Created by wangqiankun on 2016/11/16.
 */
public class UploadImageView extends RecyclerView {

    private static final String TAG = UploadImageView.class.getSimpleName();
    private static final int IMAGE_MAX_SIZE = 8;
    private static final int SPAN_CODE = 4;

    //显示相关
    private UploadImageAdapter mUploadImageAdapter;
    private ItemDecoration decoration;
    private List<UploadImageBean> imageList = new ArrayList<>();
    private int imageMaxSize = IMAGE_MAX_SIZE;  //最大上传图片数量
    private int spanCount = SPAN_CODE;          //单行item个数
    private int spacing;                        //item 间距 dp

    //上传图片用
//    private UpdateImageRecyclerViewHelper mHelper;
    private boolean isUploading; //是否正在上传图片  true-不可进行删除操作

    private UploadImageListener mUploadImageListener;


    public UploadImageView(Context context) {
        this(context, null);
    }

    public UploadImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UploadImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.UploadImageView);
            imageMaxSize = typedArray.getInteger(
                    R.styleable.UploadImageView_upload_image_max_num, IMAGE_MAX_SIZE);
            spacing = typedArray.getDimensionPixelSize(
                    R.styleable.UploadImageView_upload_image_spacing, 8);
            typedArray.recycle();
        }

        if (spacing != 0) {
            decoration = new UploadImageItemDecoration(spanCount, spacing, false);
            addItemDecoration(decoration);
        }

        if (getLayoutManager() == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, SPAN_CODE);
            setLayoutManager(gridLayoutManager);
            spanCount = SPAN_CODE;
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) getLayoutManager()).getSpanCount();
        } else {
            spanCount = 1;
        }

        setHasFixedSize(true);
        setItemAnimator(null);
        bindAdapter();
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        if (decoration != null) {
            removeItemDecoration(decoration);
        }
        decoration = decor;
        super.addItemDecoration(decor);
    }

    /**
     * 选择图片
     */
    private void openImageSelectActivity() {
        if (!hadUploadImageListener()) {
            return;
        }
        ArrayList<String> strings = getLocalFileList();
        mUploadImageListener.openPhotoAlbum(this, strings);
    }


    /**
     * 获取本地图片列表
     */
    private ArrayList<String> getLocalFileList() {
        ArrayList<String> strings = new ArrayList<>();

        for (UploadImageBean item : imageList) {
            if (!TextUtils.isEmpty(item.localPath)) {
                strings.add(item.localPath);
            }
        }

        return strings;
    }

    private void uploadImages(ArrayList<String> pathList) {
        if (!hadUploadImageListener()) {
            return;
        }

        isUploading = true;
        mUploadImageListener.uploadImages(pathList);
    }


    /**
     * 已经展示的图片数量
     */
    public int getShowedImageListSize() {
        return imageList.size();
    }

    /**
     * 是否正在上传图片
     */
    public boolean isUploading() {
        if (isUploading) {
            Toast.makeText(getContext(), getContext().getString(R.string.upload_image_toast_uploading),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 上传后 更新 ImageList 中的参数
     */
    private void updateImageList(String path, boolean isSucceed, String url) {
        int i = 0;
        for (UploadImageBean ui : imageList) {
            if (ui.localPath != null && ui.localPath.equalsIgnoreCase(path)) {
                ui.isUploaded = true;
                ui.netUrl = url;
                ui.isUploadSucceed = isSucceed;
                mUploadImageAdapter.notifyItemChanged(i);
                return;
            }
            i++;
        }
    }

    private void bindAdapter() {
        mUploadImageAdapter =
                new UploadImageAdapter(getContext(),
                        imageList,
                        new MyUploadImageAdapterListener(),
                        imageMaxSize,
                        spanCount,
                        spacing);
        setAdapter(mUploadImageAdapter);
    }


    private boolean hadUploadImageListener() {
        if (mUploadImageListener == null) {
            Toast.makeText(getContext(), "need set UploadImageListener",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void refresh() {
        mUploadImageAdapter.notifyDataSetChanged();
    }

    /**
     * 设置最大图片数量
     */
    public void setImageMaxSize(int imageMaxSize) {
        if (this.imageMaxSize == imageMaxSize) {
            return;
        }

        this.imageMaxSize = imageMaxSize;
        if (mUploadImageAdapter != null) {
            mUploadImageAdapter.setMaxImageSum(imageMaxSize);
            mUploadImageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取具体 ImageList 数据
     */
    public List<UploadImageBean> getImageList() {
        return imageList;
    }

    /**
     * 设置图片列表 （外层为非复用型 View 使用）
     *
     * @param list 单个 list
     */
    public void setImageList4Client(List<UploadImageBean> list) {
        if (list == null) {
            return;
        }
        this.imageList = list;
        mUploadImageAdapter.setList(imageList);
        refresh();
    }

    /**
     * 针对外层是可复用 View 开发。
     * 在使用方中需维护一个 SparseArray<List<UploadImageBean>> 并在 bindView 时传来
     *
     * @param allImageList 使用者纪录的上传状态
     * @param position     可复用 View 中所处位置
     */
    public void setImageLists4Client(ArrayList<List<UploadImageBean>> allImageList,
                                     int position) {
        Log.d(TAG, "setListSparseArray pos = " + position);

        if (allImageList.size() <= position - 1) {
            Toast.makeText(getContext(), getContext().getString(R.string.upload_image_error_position),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (allImageList.size() == position) {
            imageList = new ArrayList<>();
            allImageList.add(position, imageList);
        } else {
            imageList = allImageList.get(position);
            if (imageList == null) {
                imageList = new ArrayList<>();
                allImageList.set(position, imageList);
            }
        }
        mUploadImageAdapter.setList(imageList);
        Log.d(TAG, "setListSparseArray size = " + allImageList.get(position).size());

        refresh();
    }

    /**
     * 选择图片后，调用此方法。过滤图片后，调用上传方法
     */
    public void setSelectImages4Client(List<String> path) {

        if (path == null) {
            path = new ArrayList<>();
        }

        ArrayList<UploadImageBean> finalImageList
                = new ArrayList<>(imageList.size() + path.size());
        ArrayList<String> needUpdateList = new ArrayList<>();

        finalImageList.addAll(imageList);

        HashSet<String> pathListSet = new HashSet<>(path);

        //获取之前的本地图片
        HashSet<String> localPathSet = new HashSet<>();
        for (UploadImageBean ui : imageList) {
            if (TextUtils.isEmpty(ui.localPath)) {
                continue;
            }
            localPathSet.add(ui.localPath);
            if (!pathListSet.contains(ui.localPath)) {
                finalImageList.remove(ui);
            }
        }

        for (String s : path) {
            if (!localPathSet.contains(s)) {
                finalImageList.add(new UploadImageBean(s));
                needUpdateList.add(s);
            }
        }

        imageList.clear();
        imageList.addAll(finalImageList);

        refresh();

        uploadImages(needUpdateList);
    }


    public void setUploadImageListener(UploadImageListener listener) {
        mUploadImageListener = listener;
    }

    public void uploadImageSucceed(String path, String url) {
        updateImageList(path, true, url);
    }

    public void uploadImageFail(String path) {
        updateImageList(path, false, null);
    }

    public void uploadImageEnd() {
        isUploading = false;
        refresh();
    }

    private class MyUploadImageAdapterListener
            implements UploadImageAdapter.UploadImageAdapterListener {

        @Override
        public void addImage() {
            openImageSelectActivity();
        }

        @Override
        public void delImage(int position) {
            if (isUploading()) {
                return;
            }

            if (position > imageList.size() - 1) {
                Log.e(TAG, "IndexOutOfBoundsException: Invalid index " + position
                        + ", size is " + imageList.size());
                return;
            }

            imageList.remove(position);
            mUploadImageAdapter.notifyDataSetChanged();
        }

        @Override
        public void uploadAgain(int position) {
            if (position > imageList.size() - 1) {
                Log.e(TAG, "IndexOutOfBoundsException: Invalid index " + position
                        + ", size is " + imageList.size());
                return;
            }

            UploadImageBean ui = imageList.get(position);
            ArrayList<String> pls = new ArrayList<>();
            pls.add(ui.localPath);

            ui.isUploaded = false;
            ui.isUploadSucceed = false;
            mUploadImageAdapter.notifyItemChanged(position);

            uploadImages(pls);
        }
    }
}
