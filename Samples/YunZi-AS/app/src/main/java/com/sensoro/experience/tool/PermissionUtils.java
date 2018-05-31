package com.sensoro.experience.tool;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by ddong1031 on 18/4/16.
 */
public final class PermissionUtils {

    private volatile PermissionsResultObserve mListener;
    private volatile boolean mNeedFinish = false;
    //界面传递过来的权限列表,用于二次申请
    private ArrayList<String> mPermissionsList = new ArrayList<>();
    private SoftReference<Activity> mContext;

    public PermissionUtils(Activity activity) {
        mContext = new SoftReference<>(activity);
    }


    /**
     * 权限允许或拒绝对话框
     *
     * @param permissions 需要申请的权限
     * @param needFinish  如果必须的权限没有允许的话，是否需要finish当前 Activity
     */
    public void requestPermission(final ArrayList<String> permissions, final boolean needFinish,
                                  final int myRequestPermissionCode) {
        if (permissions == null || permissions.size() == 0) {
            return;
        }
        if (mListener == null) {
            throw new NullPointerException("请先注册监听!");
        }
        mNeedFinish = needFinish;
        mPermissionsList = permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取未通过的权限列表
            ArrayList<String> newPermissions = checkEachSelfPermission(permissions);
            if (newPermissions.size() > 0) {// 是否有未通过的权限
                requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]),
                        myRequestPermissionCode);
            } else {// 权限已经都申请通过了
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */
    private void requestEachPermissions(String[] permissions, int myRequestPermissionCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(permissions, myRequestPermissionCode);
        } else {
            if (hasActivity()) {
                ActivityCompat.requestPermissions(mContext.get(), permissions,
                        myRequestPermissionCode);
            }
        }
    }

    private boolean hasActivity() {
        return mContext.get() != null;
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param permissions
     */
    private void showRationaleDialog(final String[] permissions, final int myRequestPermissionCode) {
        if (hasActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext.get());
            builder.setTitle("提示")
                    .setMessage("为了应用可以正常使用，请您点击确认申请权限。")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(mContext.get(), permissions,
                                            myRequestPermissionCode);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (mNeedFinish) {
                                        Toast.makeText(mContext.get(), "需要权限！请重新打开应用", Toast.LENGTH_SHORT).show();
                                        mContext.get().finish();
                                    }
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

    }

    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return newPermissions.size > 0 表示有权限需要申请
     */
    private ArrayList<String> checkEachSelfPermission(ArrayList<String> permissions) {
        ArrayList<String> newPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if (hasActivity() && ContextCompat.checkSelfPermission(mContext.get(), permission) != PackageManager
                    .PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        return newPermissions;
    }

    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (hasActivity() && ActivityCompat.shouldShowRequestPermissionRationale(mContext.get(), permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(final int myRequestPermissioncode, int requestCode, @NonNull String[]
            permissions,
                                           @NonNull int[] grantResults, ArrayList<String> permissionsList) {
        if (requestCode == myRequestPermissioncode && permissions != null) {
            // 获取被拒绝的权限列表
            ArrayList<String> deniedPermissions = new ArrayList<>();
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            } else {
                for (String permission : permissions) {
                    if (hasActivity() && ContextCompat.checkSelfPermission(mContext.get(), permission) !=
                            PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }
                // 判断被拒绝的权限中是否有包含必须具备的权限
                ArrayList<String> forceRequirePermissionsDenied =
                        checkForceRequirePermissionDenied(permissionsList, deniedPermissions);
                if (forceRequirePermissionsDenied != null && forceRequirePermissionsDenied.size() > 0) {
                    // 必备的权限被拒绝，
                    if (mNeedFinish) {
                        showPermissionSettingDialog(myRequestPermissioncode);
                    } else {
                        if (mListener != null) {
                            mListener.onPermissionDenied();
                        }
                    }
                } else {
                    // 不存在必备的权限被拒绝，可以进首页
                    if (mListener != null) {
                        mListener.onPermissionGranted();
                    }
                }
            }

        }
    }

    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> checkForceRequirePermissionDenied(
            ArrayList<String> forceRequirePermissions, ArrayList<String> deniedPermissions) {
        ArrayList<String> forceRequirePermissionsDenied = new ArrayList<>();
        if (forceRequirePermissions != null && forceRequirePermissions.size() > 0
                && deniedPermissions != null && deniedPermissions.size() > 0) {
            for (String forceRequire : forceRequirePermissions) {
                if (deniedPermissions.contains(forceRequire)) {
                    forceRequirePermissionsDenied.add(forceRequire);
                }
            }
        }
        return forceRequirePermissionsDenied;
    }

    /**
     * 手动开启权限弹窗
     */
    private void showPermissionSettingDialog(final int myRequestPermissionCode) {
        if (hasActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext.get());
            builder.setTitle("提示")
                    .setMessage("必要的权限被拒绝")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", mContext.get().getPackageName(), null);
                            in.setData(uri);
                            mContext.get().startActivityForResult(in, myRequestPermissionCode);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (mNeedFinish) {
                                restart(mContext.get());
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, int
            myRequestPermissionCode) {
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == myRequestPermissionCode) {
            requestPermission(mPermissionsList, mNeedFinish, myRequestPermissionCode);
        }
    }

    /**
     * 获取App具体设置
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context, int requestCode) {
        getAppDetailsSettings(context, context.getPackageName(), requestCode);
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void getAppDetailsSettings(Context context, String packageName, int requestCode) {
        if (TextUtils.isEmpty(packageName)) return;
        ((AppCompatActivity) context).startActivityForResult(
                getAppDetailsSettingsIntent(packageName), requestCode);
    }

    /**
     * 获取App具体设置的意图
     *
     * @param packageName 包名
     * @return intent
     */
    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 通过任务管理器杀死进程
     * 需添加权限 {@code <uses-permission android:name="android.permission.RESTART_PACKAGES"/>}</p>
     *
     * @param context
     */
    public static void restart(Context context) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
        }
    }


    public void registerObserver(PermissionsResultObserve permissionsResultObserve) {
        mListener = permissionsResultObserve;
    }

    public void unregisterObserver(PermissionsResultObserve permissionsResultObserve) {
        if (permissionsResultObserve != mListener) {
            throw new IllegalArgumentException("注册对象不一致！");
        }
        if (mListener != null) {
            mListener = null;
        }
        if (mContext != null) {
            mContext.clear();
            mContext = null;
        }
    }

}
