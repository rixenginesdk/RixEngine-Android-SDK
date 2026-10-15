package com.alxad.sdk.demo

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.edit
import androidx.appcompat.app.AlertDialog
import java.io.File
import kotlin.concurrent.thread
import java.util.concurrent.atomic.AtomicBoolean

class SettingsActivity : BaseListViewActivity() {
    override val TAG: String = SettingsActivity::class.java.simpleName

    /** 防止快速连点并发执行清理 */
    private val cacheClearing = AtomicBoolean(false)

    override fun initAdapterData(): MutableList<AdapterData> {
        return mutableListOf(
            AdapterData(getString(R.string.clear_local_cache), null)
        )
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        when (position) {
            0 -> confirmClearCache()
        }
    }

    /**
     * 弹出确认对话框，用户确认后真正执行清除缓存
     */
    private fun confirmClearCache() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_local_cache)
            .setMessage(R.string.clear_cache_confirm)
            .setPositiveButton(android.R.string.ok) { _, _ -> clearLocalCache() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    /**
     * 清理本地缓存：
     * 1. 删除所有 SharedPreferences 文件（连同 xml 文件本身）
     * 2. 清空内部私有目录（/data/user/<pkg>/files、/cache）下的所有内容，保留顶层目录
     * 3. 清空外部私有目录（/sdcard/Android/data/<pkg>/files、/cache）下的所有内容，保留顶层目录
     * 若外部私有目录不可用（设备无外部存储 / 存储被移除）则跳过并打日志
     */
    private fun clearLocalCache() {
        // 重入保护：已在清理中则直接忽略，避免并发删除同一目录树
        if (!cacheClearing.compareAndSet(false, true)) {
            Log.w(TAG, "clearLocalCache ignored, already running")
            return
        }
        // 耗时 IO 操作放到子线程执行，避免阻塞主线程
        thread {
            try {
                // 1. 删除所有 SharedPreferences 文件
                clearAllSharedPreferences()

                // 2. 清空内部私有目录：files + cache（保留顶层目录，只删内容）
                deleteDirectoryContents(filesDir)
                deleteDirectoryContents(cacheDir)

                // 3. 清空外部私有目录：/sdcard/Android/data/<pkg>/files + cache（保留顶层目录，只删内容）
                val extFilesDir = getExternalFilesDir(null)
                val extCacheDir = externalCacheDir
                if (extFilesDir == null || extCacheDir == null) {
                    Log.d(TAG, "external storage unavailable, skip external dirs")
                } else {
                    deleteDirectoryContents(extFilesDir)
                    deleteDirectoryContents(extCacheDir)
                }

                // 回到主线程提示结果；Activity 已销毁则跳过，避免用已销毁 Activity 作 Context
                if (!isFinishing && !isDestroyed) {
                    runOnUiThread {
                        Toast.makeText(this@SettingsActivity, getString(R.string.cache_cleared), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "clearLocalCache failed", e)
            } finally {
                cacheClearing.set(false)
            }
        }
    }

    /**
     * 删除顶层目录内的所有文件和子目录，但保留顶层目录本身
     */
    private fun deleteDirectoryContents(topDir: File?) {
        if (topDir == null || !topDir.exists() || !topDir.isDirectory) {
            return
        }
        topDir.listFiles()?.forEach { child ->
            deleteRecursive(child)
        }
    }

    /**
     * 递归删除目录及其中的所有文件、子目录（连同目录本身一起删除）
     */
    private fun deleteRecursive(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        // 先删除子项，再删除自身，文件与目录一并删除
        if (!file.delete()) {
            Log.w(TAG, "delete failed: " + file.absolutePath)
        }
    }

    /**
     * 删除应用包下所有的 SharedPreferences 文件（连同 xml 文件本身及 .bak 备份）：
     * 先清空内存中已打开的 SP 实例内容，再删除对应文件，避免删除后被缓存重新写回
     */
    private fun clearAllSharedPreferences() {
        val prefsDir = File(filesDir.parentFile, "shared_prefs")
        val files = prefsDir.listFiles() ?: return
        for (f in files) {
            val name = f.name
            // 先清空内存中的实例，避免删除后被缓存重新写回
            if (name.endsWith(".xml")) {
                val prefName = name.substring(0, name.length - 4)
                getSharedPreferences(prefName, MODE_PRIVATE)
                    .edit(commit = true) { clear() }
            }
            // 删除文件本身（同时处理 .xml.bak 备份）
            if (!f.delete()) {
                Log.w(TAG, "delete sp failed: " + f.absolutePath)
            }
            if (name.endsWith(".xml")) {
                val bak = File(prefsDir, "$name.bak")
                if (bak.exists() && !bak.delete()) {
                    Log.w(TAG, "delete sp bak failed: " + bak.absolutePath)
                }
            }
        }
    }
}
