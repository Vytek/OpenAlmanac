package com.google.android.almanac;

import android.util.Log;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;

public class Almanac extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;
        Intent intent;
        
        //Create Tab Eventi
        Drawable myTab1 = getResources().getDrawable(R.drawable.list);
        intent = new Intent().setClass(this, AlmanacList.class); 
        tabSpec = tabHost.newTabSpec("Eventi").setIndicator(getString(R.string.almanacevents), myTab1).setContent(intent);
        tabHost.addTab(tabSpec);    

        //Create Tab Info
        Drawable myTab2 = getResources().getDrawable(R.drawable.info);
        intent = new Intent().setClass(this, AlmanacInfo.class);
        tabSpec = tabHost.newTabSpec("Info").setIndicator(getString(R.string.almanacinfo), myTab2).setContent(intent);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
        
    }
}