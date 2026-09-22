package com.alxad.sdk.demo

import com.alxad.sdk.demo.admob.AdmobDemoListActivity
import com.alxad.sdk.demo.gam.GamDemoListActivity
import com.alxad.sdk.demo.ironsource.IronSourceDemoListActivity
import com.alxad.sdk.demo.max.MaxDemoListActivity
import com.alxad.sdk.demo.topon.TopOnDemoListActivity
import com.alxad.sdk.demo.tradplus.TradPlusDemoListActivity

class OtherPlatformActivity : BaseListViewActivity(){
    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData("TopOn AD Demo", TopOnDemoListActivity::class.java))
        list.add(AdapterData("Admob AD Demo", AdmobDemoListActivity::class.java))
        list.add(AdapterData("Gam AD Demo", GamDemoListActivity::class.java))
        list.add(AdapterData("TradPlus AD Demo", TradPlusDemoListActivity::class.java))
        list.add(AdapterData("LevelPlay AD Demo", IronSourceDemoListActivity::class.java))
        list.add(AdapterData("Max AD Demo", MaxDemoListActivity::class.java))
        return list
    }
}