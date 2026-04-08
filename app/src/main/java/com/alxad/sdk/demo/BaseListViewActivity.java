package com.alxad.sdk.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public abstract class BaseListViewActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private MyAdapter mAdapter;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_list);
        setActionBar();
        checkNavigationBar(findViewById(R.id.root_view));

        ListView listView = (ListView)findViewById(R.id.listView);
        mAdapter = new MyAdapter(this, initAdapterData());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    public abstract List<AdapterData> initAdapterData();


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterData item = mAdapter.getItem(position);
        Intent intent = new Intent(this, item.jumpActivity);
        startActivity(intent);
    }

    public static class AdapterData {
        public String name;
        public Class<?> jumpActivity;

        public AdapterData(String name, Class<?> jumpActivity) {
            this.name = name;
            this.jumpActivity = jumpActivity;
        }
    }

    private static class MyAdapter extends BaseAdapter {
        private Context mContext;
        private final List<AdapterData> mList;
        private final LayoutInflater mInflater;

        public MyAdapter(Context context, List<AdapterData> list) {
            this.mContext = context;
            this.mList = list;
            this.mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return (mList == null || mList.isEmpty()) ? 0 : mList.size();
        }

        @Override
        public AdapterData getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            textView = (TextView) convertView;

            AdapterData item = getItem(position);
            textView.setText(item.name);
            return convertView;
        }

    }
}

