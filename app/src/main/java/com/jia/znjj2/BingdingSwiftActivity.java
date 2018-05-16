package com.jia.znjj2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.RoomData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShengYi on 2018/5/11.
 */

public class BingdingSwiftActivity extends Activity {
    private DataControl mDC;
    private ExpandableListView elvSwiftElectric;
    private ArrayList<ElectricInfoData> getmSwiftInfoDataList;
    public static int BindingSwiftIndex;
    public static int SwiftRoomIndex;
    public static int checkSwiftChange;
    public int RoomPosition;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actuivity_binding_swift);
        initView();
        ExpandalbeGridAdaper adaper = new ExpandalbeGridAdaper();
        elvSwiftElectric.setAdapter(adaper);
    }

    private void initView() {
        mDC = DataControl.getInstance();
        elvSwiftElectric = (ExpandableListView)findViewById(R.id.binding_swift_electric_list);
        ExpandalbeGridAdaper adaper = new ExpandalbeGridAdaper();
        elvSwiftElectric.setAdapter(adaper);
        checkSwiftChange = 0;
        int group = elvSwiftElectric.getCount();
        if(1 <= group){
            elvSwiftElectric.expandGroup(0);
        }
    }
    public class ExpandalbeGridAdaper extends BaseExpandableListAdapter {
        private List<RoomData.RoomDataInfo> areaList;
        private List<List<ElectricInfoData>> electricList;
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        public ExpandalbeGridAdaper(){
            areaList = mDC.mAreaList;
        }

        @Override
        public int getGroupCount() {
            return areaList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(BingdingSwiftActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL);
//            ImageView logo = new ImageView(SceneAddElectric.this);
//            logo.setImageResource(logos[groupPosition]);
//            ll.addView(logo);
            TextView textView = new TextView(BingdingSwiftActivity.this);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(36, 6, 0, 6);
            textView.setTextSize(20);
            textView.setText(areaList.get(groupPosition).getRoomName());
            ll.addView(textView);
            return ll;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.electric_list_item_ex, null);
            }
            GridView gridView = (GridView) convertView .findViewById(R.id.electric_list_item_ex_grid);

            gridView.setNumColumns(3);
            gridView.setHorizontalSpacing(10);
            gridView.setGravity(Gravity.CENTER);

            GridAdapter adapter = new GridAdapter(getBaseContext(), groupPosition);
            gridView.setAdapter(adapter);// Adapter

            int totalHeight = 0;


            if(adapter.getCount() !=0){
                RelativeLayout relativeLayout = (RelativeLayout) adapter.getView(0, null, gridView);
                LinearLayout rl = (LinearLayout) relativeLayout.getChildAt(0);
                rl.measure(0, 0);
                totalHeight = (int)Math.ceil(adapter.getCount()/3.0) * rl.getMeasuredHeight();
            }
            gridView.getLayoutParams().height = totalHeight;
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RoomPosition = groupPosition;

                    for(int j=0;j<mDC.mAreaList.get( RoomPosition).getmElectricInfoDataList().size();j++){
                        if((mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==1)
                                ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==2)
                                ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==3)){
                            getmSwiftInfoDataList.add(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j));
                        }
                    }
                    SwiftRoomIndex = RoomPosition;
                    BindingSwiftIndex = getmSwiftInfoDataList.get(position).getElectricIndex();
                    checkSwiftChange = 1;
                    finish();
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    class GridAdapter extends BaseAdapter {
        private int groupPosition;
        private Context context;
        public GridAdapter(Context context,int position){
            this.context = context;
            this.groupPosition = position;
        }
        @Override
        public int getCount() {
            getmSwiftInfoDataList = new ArrayList<ElectricInfoData>() ;
            for(int i=0;i<mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().size();i++){
                if((mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(i).getElectricType()==1)
                    ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(i).getElectricType()==2)
                    ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(i).getElectricType()==3)){
                    getmSwiftInfoDataList.add(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(i));
                }
            }
            return getmSwiftInfoDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.electric_list_item, null, false);
            viewHolder.electricrl = (RelativeLayout) convertView.findViewById(R.id.electric_list_item_rl);
            viewHolder.electricll = (LinearLayout) convertView.findViewById(R.id.electric_list_item_ll);
            viewHolder.electricImg = (ImageView) convertView.findViewById(R.id.electric_list_item_img);
            viewHolder.electricName = (TextView) convertView.findViewById(R.id.electric_list_item_name);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.electric_list_item_delete);
            viewHolder.delete.setVisibility(View.GONE);

            DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
            System.out.println("$$$$$$$$$$$"+loacalDisplayMetrics);
            int i = (int)(loacalDisplayMetrics.heightPixels/loacalDisplayMetrics.density);

            if (position < mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().size()) {
                for(int j=0;j<mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().size();j++){
                    if((mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==1)
                            ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==2)
                            ||(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j).getElectricType()==3)){
                        getmSwiftInfoDataList.add(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(j));
                    }
                }
                viewHolder.electricImg.setBackgroundResource(mDC.mElectricTypeImages.getResourceId(getmSwiftInfoDataList.get(position).getElectricType(),0));
                viewHolder.electricName.setText(getmSwiftInfoDataList.get(position).getElectricName());
            }else {
                viewHolder.electricImg.setBackgroundResource(R.drawable.add);
                viewHolder.electricName.setVisibility(View.GONE);
            }
            viewHolder.electricrl.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i / 3));
            viewHolder.electricImg.setLayoutParams(new LinearLayout.LayoutParams(i / 6, i / 6));
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(i/15,i/15);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.setMargins(0,10,10,0);
            viewHolder.delete.setLayoutParams(lp2);
            if (i <= 800)
            {
                viewHolder.electricName.setTextSize(18.0F);
            }
            else
            {
                viewHolder.electricName.setTextSize(22.0F);
            }

            return convertView;
        }
    }

    class ViewHolder{
        public RelativeLayout electricrl;
        public LinearLayout electricll;
        public ImageView electricImg;
        public TextView electricName;
        public ImageView delete;
    }
    public void bindingSwiftBack(View view){
        finish();
    }
}
