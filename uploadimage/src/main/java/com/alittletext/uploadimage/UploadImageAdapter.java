package com.alittletext.uploadimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by wqk on 16/9/12.
 */
class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.UploadImageViewHolder> {

    private int maxImageSum = 100;
    private UploadImageAdapterListener mListener;
    private Context context;
    private List<UploadImageBean> list;
    private int numColumns;
    private int spacing;
    /**
     * 删除图片 Listener
     */
    private View.OnClickListener delImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mListener.delImage(position);
        }
    };
    /**
     * 重新上传 Listener
     */
    private View.OnClickListener uploadAgainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mListener.uploadAgain(position);

        }
    };
    /**
     * 添加图片 Listener
     */
    private View.OnClickListener addImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.addImage();
        }
    };

    UploadImageAdapter(Context context,
                       List<UploadImageBean> imageList,
                       UploadImageAdapterListener listener,
                       int maxImageSum,
                       int numColumns,
                       int spacing) {
        this.context = context;
        this.list = imageList;
        this.mListener = listener;
        this.maxImageSum = maxImageSum;
        this.numColumns = numColumns;
        this.spacing = spacing;
    }

    @Override
    public UploadImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upload_image_item, parent, false);
        return new UploadImageViewHolder(view, numColumns);
    }

    @Override
    public void onBindViewHolder(UploadImageViewHolder holder, int position) {
        //当 list.size() ==1 count == 2 pos =0 通过 pos==1 return
        //当 list.size() ==2 count == 3 pos =0 通过 pos==1 通过  pos==2 return

        if (list.size() == position && list.size() != maxImageSum) {
            holder.iv_add_image.setVisibility(View.VISIBLE);
            holder.iv_add_image.setOnClickListener(addImageListener);

            holder.image.setImageURI("");
            holder.ib_del.setVisibility(View.GONE);
            holder.tv_retry.setVisibility(View.GONE);
            return;
        } else {
            holder.iv_add_image.setVisibility(View.GONE);
        }

        UploadImageBean item = list.get(position);

        if (!TextUtils.isEmpty(item.localPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(item.localPath, options);
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageURI(item.netUrl);
        }


        holder.ib_del.setVisibility(item.isUploaded
                || !TextUtils.isEmpty(item.netUrl) ? View.VISIBLE : View.GONE);
        holder.ib_del.setOnClickListener(delImageListener);
        holder.ib_del.setTag(position);

        holder.tv_retry.setVisibility(
                TextUtils.isEmpty(item.netUrl)
                        && item.isUploaded
                        && !item.isUploadSucceed ? View.VISIBLE : View.GONE);
        holder.tv_retry.setTag(position);
        holder.tv_retry.setOnClickListener(uploadAgainListener);
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size() < maxImageSum ? list.size() + 1 : maxImageSum;
        }
    }

    void setList(List<UploadImageBean> imageList) {
        this.list = imageList;
    }

    void setMaxImageSum(int maxImageSum) {
        this.maxImageSum = maxImageSum;
    }


    interface UploadImageAdapterListener {
        void addImage();

        void delImage(int position);

        void uploadAgain(int position);
    }


    class UploadImageViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        AppCompatTextView tv_retry;
        ImageButton ib_del;
        ImageView iv_add_image;

        private UploadImageViewHolder(View itemView, int numColumns) {
            super(itemView);

            if (numColumns != 0) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = (context.getResources().getDisplayMetrics().widthPixels
                        - spacing * (numColumns + 1)) / numColumns;
                itemView.setLayoutParams(params);
            }

            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            tv_retry = (AppCompatTextView) itemView.findViewById(R.id.tv_retry);
            ib_del = (ImageButton) itemView.findViewById(R.id.ib_del);
            iv_add_image = (ImageView) itemView.findViewById(R.id.iv_add_image);

        }
    }
}
