package com.jia.znjj2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
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

import com.jia.data.AccountData;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.RoomData;

import java.util.List;

public class SceneAddElectric extends Activity {

    private DataControl mDC;
    private ExpandableListView elvElectric;
    private int roomPosition;
    private int electricPosition;
    private int scenePosition;
    private String electricOrder;
    private String orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add_electric);
        initView();
        addListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        scenePosition = getIntent().getIntExtra("scenePosition", -1);
        elvElectric = (ExpandableListView) findViewById(R.id.scene_add_electric_list);
        ExpandalbeGridAdaper adaper = new ExpandalbeGridAdaper();
        elvElectric.setAdapter(adaper);
        int group = elvElectric.getCount();
        if(1 <= group){
            elvElectric.expandGroup(0);
        }
    }

    private void addListener(){
        elvElectric.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(SceneAddElectric.this,SceneAddElectricDetail.class);
                intent.putExtra("groupPosition",groupPosition);
                intent.putExtra("groupPosition",childPosition);
                startActivity(intent);
                return false;
            }
        });
    }

    public void sceneAddElectricBack(View view){
        finish();
    }

    public class ExpandalbeGridAdaper extends BaseExpandableListAdapter{
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
            LinearLayout ll = new LinearLayout(SceneAddElectric.this);
            ll.setOrientation(LinearLayout.VERTICAL);
//            ImageView logo = new ImageView(SceneAddElectric.this);
//            logo.setImageResource(logos[groupPosition]);
//            ll.addView(logo);
            TextView textView = new TextView(SceneAddElectric.this);
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
                    Intent intent = new Intent(SceneAddElectric.this,SceneAddElectricDetail.class);
                    intent.putExtra("roomPosition", groupPosition);
                    intent.putExtra("electricPosition", position);
                    intent.putExtra("scenePosition", scenePosition);
                    roomPosition = groupPosition;
                    electricPosition = position;
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    class GridAdapter extends BaseAdapter{
        private int groupPosition;
        private Context context;
        public GridAdapter(Context context,int position){
            this.context = context;
            this.groupPosition = position;
        }
        @Override
        public int getCount() {
            return mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().size();
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
                viewHolder.electricImg.setBackgroundResource(mDC.mElectricTypeImages.getResourceId(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(position).getElectricType(),0));
                viewHolder.electricName.setText(mDC.mAreaList.get(groupPosition).getmElectricInfoDataList().get(position).getElectricName());
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
}
