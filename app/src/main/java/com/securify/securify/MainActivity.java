package com.securify.securify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.securify.securify.model.MainModel;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs=null;    //needed to check if first app start

    MainModel model;
    TabLayout tabLayout;
    ViewPager vp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs=getSharedPreferences("com.securify.securify",MODE_PRIVATE);//get preferences to check if the app is started the first time

        //setting the tabs and fragments
        vp = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);

        pagerAdapter.addPage(new GameTabFragment());
        pagerAdapter.addPage(new OtherTabFragment());
        vp.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(vp);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
        //setupTabIcons();
    }


    @SuppressLint("ApplySharedPref")
    @Override
    protected void onResume(){
        super.onResume();


        if(prefs.getBoolean("firstrun",true)){//check if first run  https://stackoverflow.com/questions/7217578/check-if-application-is-on-its-first-run
            model = new MainModel(getApplicationContext(),true);
            prefs.edit().putBoolean("firstrun",false).commit();
        }

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.game);
        tabLayout.getTabAt(1).setIcon(R.drawable.menu);
    }

    public void next_fragment(View view) {
        vp.setCurrentItem(vp.getCurrentItem()+1);
    }

    public void previous_fragment(View view) {
        vp.setCurrentItem(vp.getCurrentItem()-1);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> pages = new ArrayList<>();

        String[] tabTitles =  new String[] {"  Spiele", "  Anderes"};
        Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new GameTabFragment();
                case 1:
                    return new OtherTabFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position){
            View tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            TextView tv = tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            tv.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
            return tab;
        }

        //needed?
        public void addPage(Fragment f) {
            pages.add(f);
        }
    }

}
