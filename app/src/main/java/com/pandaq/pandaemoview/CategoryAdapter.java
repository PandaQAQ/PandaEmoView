package com.pandaq.pandaemoview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pandaq.emoticonlib.sticker.StickerCategory;

import java.util.ArrayList;
import java.util.List;

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
        View item = LayoutInflater.from(mContext).inflate(R.layout)
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
