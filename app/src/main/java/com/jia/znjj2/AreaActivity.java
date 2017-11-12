package com.jia.znjj2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public class AreaActivity extends FragmentActivity implements View.OnClickListener{
    /*与底部菜单有关部分start*/
    private LinearLayout bottom_ll_main;
    private LinearLayout bottom_ll_area;
    private LinearLayout bottom_ll_scene;
    private LinearLayout bottom_ll_security;
    private TextView mTvArea;
    /*与底部菜单有关部分end*/

    private DataControl mDC;

    private CategoryTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        initBottom();
        mDC = DataControl.getInstance();

        tabs = (CategoryTabStrip) findViewById(R.id.area_fragment_category_strip);
        pager = (ViewPager) findViewById(R.id.area_fragment_view_pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        tabs.setViewPager(pager);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<String> catalogs = new ArrayList<String>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            for(int i=0;i<mDC.mAreaList.size();i++){
                catalogs.add(mDC.mAreaList.get(i).getRoomName());
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return catalogs.get(position);
        }

        @Override
        public int getCount() {
            return catalogs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return AreaElectricFragment.newInstance(position);
        }

    }


    /*与底部菜单有关部分start*/
    private void initBottom(){
        bottom_ll_main = (LinearLayout) findViewById(R.id.bottom_ll_main);
        bottom_ll_area = (LinearLayout) findViewById(R.id.bottom_ll_area);
        bottom_ll_scene = (LinearLayout) findViewById(R.id.bottom_ll_scene);
        bottom_ll_security = (LinearLayout) findViewById(R.id.bottom_ll_security);
        mTvArea = (TextView) findViewById(R.id.bottom_tv_area);
        mTvArea.setTextColor(getResources().getColor(R.color.text_color_select));
        bottom_ll_main.setOnClickListener(this);
        bottom_ll_area.setOnClickListener(this);
        bottom_ll_scene.setOnClickListener(this);
        bottom_ll_security.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bottom_ll_main:
                intent = new Intent(AreaActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_scene:
                intent = new Intent(AreaActivity.this, SenceActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_security:
                intent = new Intent(AreaActivity.this, SecurityActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    /*与底部菜单有关部分end*/
}
