package com.alxad.sdk.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SettingsActivity extends BaseListViewActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    /** 防止快速连点并发执行清理 */
    private final AtomicBoolean cacheClearing = new AtomicBoolean(false);

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();
        list.add(new AdapterData(getString(R.string.clear_local_cache), null));
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            confirmClearCache();
        }
    }

    /**
     * 弹出确认对话框，用户确认后真正执行清除缓存
     */
    private void confirmClearCache() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_local_cache)
                .setMessage(R.string.clear_cache_confirm)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> clearLocalCache())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * 清理本地缓存：
     * 1. 清除所有 SharedPreferences（清空内容并删除 xml 文件本身）
     * 2. 清空内部私有目录（/data/user/<pkg>/files、/cache）下的所有内容，保留顶层目录
     * 3. 清空外部私有目录（/sdcard/Android/data/<pkg>/files、/cache）下的所有内容，保留顶层目录
     * 若外部私有目录不可用（设备无外部存储 / 存储被移除）则跳过并打日志
     */
    private void clearLocalCache() {
        // 重入保护：已在清理中则直接忽略，避免并发删除同一目录树
        if (!cacheClearing.compareAndSet(false, true)) {
            Log.w(TAG, "clearLocalCache ignored, already running");
            return;
        }
        // 耗时 IO 操作放到子线程执行，避免阻塞主线程
        new Thread(() -> {
            try {
                // 1. 清除所有 SharedPreferences
                clearAllSharedPreferences();

                // 2. 清空内部私有目录：files + cache（保留顶层目录，只删内容）
                deleteDirectoryContents(getFilesDir());
                deleteDirectoryContents(getCacheDir());

                // 3. 清空外部私有目录：/sdcard/Android/data/<pkg>/files + cache（保留顶层目录，只删内容）
                File extFilesDir = getExternalFilesDir(null);
                File extCacheDir = getExternalCacheDir();
                if (extFilesDir == null || extCacheDir == null) {
                    Log.d(TAG, "external storage unavailable, skip external dirs");
                } else {
                    deleteDirectoryContents(extFilesDir);
                    deleteDirectoryContents(extCacheDir);
                }

                // 回到主线程提示结果；Activity 已销毁则跳过，避免用已销毁 Activity 作 Context
                if (!isFinishing() && !isDestroyed()) {
                    runOnUiThread(() ->
                            Toast.makeText(SettingsActivity.this,
                                    getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "clearLocalCache failed", e);
            } finally {
                cacheClearing.set(false);
            }
        }).start();
    }

    /**
     * 删除顶层目录内的所有文件和子目录，但保留顶层目录本身
     */
    private void deleteDirectoryContents(File topDir) {
        if (topDir == null || !topDir.exists() || !topDir.isDirectory()) {
            return;
        }
        File[] children = topDir.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursive(child);
            }
        }
    }

    /**
     * 递归删除目录及其中的所有文件、子目录（连同目录本身一起删除）
     */
    private void deleteRecursive(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        // 先删除子项，再删除自身，文件与目录一并删除
        if (!file.delete()) {
            Log.w(TAG, "delete failed: " + file.getAbsolutePath());
        }
    }

    /**
     * 清除应用包下所有的 SharedPreferences：
     * 先清空内存中已打开的 SP 实例内容，再删除对应的 xml 文件及其 .bak 备份
     */
    @SuppressLint("ApplySharedPref")
    private void clearAllSharedPreferences() {
        File prefsDir = new File(getFilesDir().getParentFile(), "shared_prefs");
        File[] files = prefsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            String name = f.getName();
            // 先清空内存中的实例，避免删除后被缓存重新写回
            if (name.endsWith(".xml")) {
                String prefName = name.substring(0, name.length() - 4);
                SharedPreferences prefs = getSharedPreferences(prefName, Context.MODE_PRIVATE);
                prefs.edit().clear().commit(); // 此处必须使用 commit 同步写入
            }
            // 删除文件本身（同时处理 .xml.bak 备份）
            if (!f.delete()) {
                Log.w(TAG, "delete sp failed: " + f.getAbsolutePath());
            }
            if (name.endsWith(".xml")) {
                File bak = new File(prefsDir, name + ".bak");
                if (bak.exists() && !bak.delete()) {
                    Log.w(TAG, "delete sp bak failed: " + bak.getAbsolutePath());
                }
            }
        }
    }
}
