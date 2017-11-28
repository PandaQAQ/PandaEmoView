package com.pandaq.pandaemoview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pandaq.emoticonlib.sticker.StickerCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huxinyu on 2017/11/27 0027.
 * description ：
 */

public class CategoryAdapter extends RecyclerView.Adapter {

    private ArrayList<StickerCategory> mStickerCategories;
    private Context mContext;

    public CategoryAdapter(Context context, List<StickerCategory> categories) {
        mStickerCategories = (ArrayList<StickerCategory>) categories;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.item_category, null, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StickerCategory category = mStickerCategories.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvEmoticonName.setText(category.getTitle());
        Picasso.with(mContext)
                .load(category.getCoverPath())
                .into(viewHolder.mIvCover);

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
}
