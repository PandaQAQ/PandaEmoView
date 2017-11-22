package com.pandaq.emoticonlib.photopicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;

import java.util.ArrayList;

/**
 * Created by PandaQ on 2017/3/30.
 * email:767807368@qq.com
 */

public class CheckPicAdapter extends BaseAdapter {
    private ArrayList<String> mPicPaths;
    private ArrayList<String> mSelectedPaths;
    private Context mContext;
    private boolean showCheckBox;
    public static final String IC_ACTION_CAMERA = "ic_action_camera";
    public static final String IC_ACTION_ADD = "ic_action_add";

    CheckPicAdapter(Context context, ArrayList<String> picPaths) {
        mPicPaths = picPaths;
        mContext = context;
        mSelectedPaths = new ArrayList<>();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_pic_item, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = mPicPaths.get(position);
        switch (path) {
            case IC_ACTION_CAMERA:
                holder.mIvPic.setImageResource(R.drawable.ic_action_camera);
                holder.mCheckBox.setVisibility(View.GONE);
                break;
            case IC_ACTION_ADD:
                holder.mIvPic.setImageResource(R.drawable.ic_action_add);
                holder.mCheckBox.setVisibility(View.GONE);
                break;
            default:
                EmoticonManager.getInstance().getIImageLoader().displayImage("file://" + mPicPaths.get(position), holder.mIvPic);
                if (showCheckBox) {
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                    holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (!mSelectedPaths.contains(mPicPaths.get(position)))
                                    mSelectedPaths.add(mPicPaths.get(position));
                            } else {
                                mSelectedPaths.remove(mPicPaths.get(position));
                            }
                        }
                    });
                    holder.mIvPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
                        }
                    });
                } else {
                    holder.mCheckBox.setVisibility(View.GONE);
                    holder.mIvPic.setOnClickListener(null);
                }
                break;
        }

        return convertView;
    }

    void setPicPaths(ArrayList<String> picPaths) {
        mPicPaths = picPaths;
        notifyDataSetChanged();
    }

    void showCheckBox(boolean show) {
        showCheckBox = show;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        SquareImage mIvPic;
        CheckBox mCheckBox;

        ViewHolder(View view) {
            mIvPic = (SquareImage) view.findViewById(R.id.iv_pic);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    public ArrayList<String> getSelectedPath() {
        return mSelectedPaths;
    }

    public void notifyDelete() {
        mPicPaths.removeAll(mSelectedPaths);
        notifyDataSetChanged();
    }
}
