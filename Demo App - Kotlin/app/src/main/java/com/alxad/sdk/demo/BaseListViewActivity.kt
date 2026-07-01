package com.alxad.sdk.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

abstract class BaseListViewActivity : BaseActivity(), OnItemClickListener {

    override val TAG: String = BaseListViewActivity::class.java.getSimpleName()

    private var mAdapter: MyAdapter? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setActionBar()

        //        checkNavigationBar(findViewById(R.id.root_view));
        val listView = findViewById<View?>(R.id.listView) as ListView
        mAdapter = MyAdapter(this, initAdapterData())
        listView.setAdapter(mAdapter)
        listView.setOnItemClickListener(this)
    }

    abstract fun initAdapterData(): MutableList<AdapterData>?


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = mAdapter!!.getItem(position)
        val intent = Intent(this, item.jumpActivity)
        startActivity(intent)
    }


    public class AdapterData(var name: String?, var jumpActivity: Class<*>?)


    public class MyAdapter(private val mContext: Context?, private val mList: MutableList<AdapterData>?) :
        BaseAdapter() {
        private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

        override fun getCount(): Int {
            return if (mList.isNullOrEmpty()) 0 else mList.size
        }

        override fun getItem(position: Int): AdapterData {
            return mList!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = convertView
            var textView: TextView?=null
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            }
            textView = convertView as? TextView

            val item = getItem(position)
            textView?.text = item.name
            return convertView
        }
    }

    /**
     * 通过activity的全型定型类名来获取包名跳转，这样做有可能这个模块没有被引用时不会报错
     *
     * @param desc
     * @param activityClassName
     * @return
     */
    protected fun createAdapterItemData(desc: String?, activityClassName: String?): AdapterData? {
        if (TextUtils.isEmpty(activityClassName)) {
            return null
        }
        try {
            val activityClass = Class.forName(activityClassName)
            if (activityClass.newInstance() is Activity) {
                return AdapterData(desc, activityClass)
            }
        } catch (e: Exception) {
            Log.i(TAG, "err:" + e.message)
        }
        return null
    }

}