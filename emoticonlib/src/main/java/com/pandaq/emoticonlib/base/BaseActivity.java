package com.pandaq.emoticonlib.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.pandaq.emoticonlib.R;

/**
 * Created by huxinyu on 2017/11/20 0020.
 * description ：
 */

public class BaseActivity extends AppCompatActivity {

    private PermissionCall mPermissionCall;
    private final int permissionRequestCode = 110;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* 处理权限问题*/

    /**
     * 对子类提供的申请权限方法
     *
     * @param permissions 申请的权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestRunTimePermissions(String[] permissions, PermissionCall call) {
        if (permissions == null || permissions.length == 0)
            return;
        mPermissionCall = call;
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)) {
            //提示已经拥有权限
            mPermissionCall.requestSuccess();
        } else {
            //申请权限
            requestPermission(permissions, permissionRequestCode);
        }
    }

    public boolean checkPermissionGranted(String... permissions) {
        boolean result = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void requestPermission(final String[] permissions, final int requestCode) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(R.string.content_to_request_permission)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                mPermissionCall.requestSuccess();
                mPermissionCall = null;
            } else {
                mPermissionCall.refused();
                mPermissionCall = null;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface PermissionCall {
        //申请成功
        void requestSuccess();

        //拒绝
        void refused();
    }
}
