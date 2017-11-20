package com.pandaq.emoticonlib.photopicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import java.util.ArrayList;

/**
 * Created by PandaQ on 2017/3/30.
 * email:767807368@qq.com
 */

public class CheckPicAdapter extends BaseAdapter {
    private ArrayList<String> mPicPaths;
    private Context mContext;
    public static final String IC_ACTION_CAMERA = "ic_action_camera";
    public static final String IC_ACTION_ADD = "ic_action_add";

    CheckPicAdapter(Context context, ArrayList<String> picPaths) {
        mPicPaths = picPaths;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPicPaths == null ? 0 : mPicPaths.size();
    }

    @Override
    public String getItem(int position) {
        return mPicPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_pic_item, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = mPicPaths.get(position);
        int value = EmoticonUtils.dp2px(mContext, 30);
        switch (path) {
            case IC_ACTION_CAMERA:
                holder.mIvPic.setPadding(value, value, value, value);
                holder.mIvPic.setImageResource(R.drawable.ic_action_camera);
                break;
            case IC_ACTION_ADD:
                holder.mIvPic.setPadding(value, value, value, value);
                holder.mIvPic.setImageResource(R.drawable.ic_action_add);
                System.out.println(path);
                break;
            default:
                holder.mIvPic.setPadding(0, 0, 0, 0);
                EmoticonManager.getIImageLoader().displayImage("file://" + mPicPaths.get(position), holder.mIvPic);
                break;
        }

        return convertView;
    }

    void setPicPaths(ArrayList<String> picPaths) {
        mPicPaths = picPaths;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        SquareImage mIvPic;

        ViewHolder(View view) {
            mIvPic = (SquareImage) view.findViewById(R.id.iv_pic);
        }
    }
}
