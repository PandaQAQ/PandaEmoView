package com.pandaq.pandaemoview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.sticker.StickerManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huxinyu on 2017/11/27 0027.
 * description ：
 */

public class ItemStickerAdapter extends RecyclerView.Adapter {

    private ArrayList<StickerEntity> mStickerCategories;
    private Context mContext;

    public ItemStickerAdapter(Context context, List<StickerEntity> categories) {
        mStickerCategories = (ArrayList<StickerEntity>) categories;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.item_stickers, null, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StickerEntity entity = mStickerCategories.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvEmoticonName.setText(entity.name);
        Picasso.with(mContext)
                .load(entity.picCover)
                .into(viewHolder.mIvCover);
        if (entity.isDownLoaded) {
            viewHolder.mBtnLoad.setText("删除");
            viewHolder.mBtnLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除表情库
                    File file = new File(entity.downLoadUrl);
                    deleteFile(file);
                    file.deleteOnExit();
                    PandaEmoManager.getInstance().getManagedView().reloadEmos(0);
                }
            });
        } else {
            viewHolder.mBtnLoad.setText("下载");
            viewHolder.mBtnLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StickerManager.getInstance().addZipResource(entity.downLoadUrl);
                    viewHolder.mBtnLoad.setText("已完成");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStickerCategories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView mIvCover;
        @BindView(R.id.tv_emoticon_name)
        TextView mTvEmoticonName;
        @BindView(R.id.btn_load)
        Button mBtnLoad;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (File file1 : files) { // 遍历目录下所有的文件
                    this.deleteFile(file1); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            Log.d("PandEmoView", "文件不存在！");
        }
    }
}
