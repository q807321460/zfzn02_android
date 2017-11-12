package com.jia.znjj2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */
public class AreaFragment extends Fragment {
    private DataControl mDC;

    private View view;
    private CategoryTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private ImageView ivAreaAdd;


    public ViewPager getPager() {
        return pager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_area, container, false);
        initView();
        addListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("AreaFragment onResume()");
        initView();
        pager.setCurrentItem(((MainActivity)getActivity()).getCurrentAreaPage());
        int i = ((MainActivity)getActivity()).getCurrentAreaPage();
        System.out.println(((MainActivity)getActivity()).getCurrentAreaPage());

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setCurrentAreaPage(pager.getCurrentItem());
    }

    private void initView(){
        mDC = DataControl.getInstance();
        tabs = (CategoryTabStrip) view.findViewById(R.id.area_fragment_category_strip);
        pager = (ViewPager) view.findViewById(R.id.area_fragment_view_pager);
        ivAreaAdd = (ImageView) view.findViewById(R.id.area_fragment_add_area);
        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

    }

    private void addListener(){
        ivAreaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDC.mUserList.get(0).getIsAdmin() != 1){
                    Toast.makeText(getContext(), "非管理员，不能添加区域", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getContext(), AreaAdd.class);
                startActivity(intent);
            }
        });
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


}
