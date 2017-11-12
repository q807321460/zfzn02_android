package com.jia.znjj2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/11.
 */
public class SceneFragment extends Fragment {
    private View view;
    private DataControl mDC;
    private ImageView ivTitleBack;
    private TextView tvTitletext;
    private TextView tvTitleAdd;
    private TextView tvSceneMode;
    private TextView tvlinkageControl;
    private ListView lvSceneMode;
    private ListView lvLinkageControl;
    private int saveposition;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_scene, container, false);
        initView();
        addListener();
        updateListView();
        return view;
    }


    @Override


    public void onResume() {
        super.onResume();
        updateListView();
        saveListView();
    }


    private void initView(){
        mDC = DataControl.getInstance();
        ivTitleBack = (ImageView) view.findViewById(R.id.scene_title_back);
        tvTitletext = (TextView) view.findViewById(R.id.scene_title_text);
        tvTitleAdd = (TextView) view.findViewById(R.id.scene_title_add);
        tvSceneMode = (TextView) view.findViewById(R.id.scene_scene_mode_tv);
        tvlinkageControl = (TextView) view.findViewById(R.id.scene_linkage_control_tv);
        lvSceneMode = (ListView) view.findViewById(R.id.scene_scene_mode_list);
        lvLinkageControl = (ListView) view.findViewById(R.id.scene_linkage_control_list);

        tvSceneMode.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_select));
        tvlinkageControl.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color));
        lvSceneMode.setVisibility(View.VISIBLE);
        lvLinkageControl.setVisibility(View.GONE);
    }

    private void addListener(){
        tvTitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDC.mUserList.get(0).getIsAdmin() != 1){
                    Toast.makeText(getContext(), "非管理员，不能添加情景", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getContext(), SceneAdd.class);
                startActivity(intent);
            }
        });
        tvSceneMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSceneMode.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_select));
                tvlinkageControl.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color));
                lvSceneMode.setVisibility(View.VISIBLE);
                lvLinkageControl.setVisibility(View.GONE);
            }
        });
        tvlinkageControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSceneMode.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color));
                tvlinkageControl.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_select));
                lvSceneMode.setVisibility(View.GONE);
                lvLinkageControl.setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateListView()
    {
        int i = mDC.mSceneList.size();
        ArrayList<HashMap<String,String>> localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap<String,String> localHashMap = new HashMap<>();
            localHashMap.put("scene_name", mDC.mSceneList.get(j).getSceneName());
            localHashMap.put("scene_img", ""+mDC.mSceneList.get(j).getSceneImg());
            localList.add(localHashMap);
        }
        SceneModeAdapter adapter = new SceneModeAdapter(localList, getContext(),
                new String[] {"scene_img", "scene_name", "scene_more", "scene_ll" },
                new int[] {R.id.scene_mode_item_img, R.id.scene_mode_item_name, R.id.scene_mode_item_more,
                        R.id.scene_mode_item_ll });
        lvSceneMode.setAdapter(adapter);
    }
    private void saveListView() {
        lvSceneMode.setSelection(saveposition);
    }
    public class SceneModeAdapter extends BaseAdapter
    {
        private SceneModeViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, String>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public SceneModeAdapter(ArrayList<HashMap<String, String>> paramArrayList, Context context,
                               String[] paramArrayOfString, int[] paramArrayOfInt)
        {
            this.mAppList = paramArrayList;
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.keyString = new String[paramArrayOfString.length];
            this.valueViewID = new int[paramArrayOfInt.length];
            System.arraycopy(paramArrayOfString, 0, this.keyString, 0, paramArrayOfString.length);
            System.arraycopy(paramArrayOfInt, 0, this.valueViewID, 0, paramArrayOfInt.length);
        }

        public int getCount()
        {
            return this.mAppList.size();
        }

        public Object getItem(int paramInt)
        {
            return this.mAppList.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            paramView = this.mInflater.inflate(R.layout.scene_mode_item, null);
            this.holder = new SceneModeViewHolder();
            this.holder.sceneImg = ((ImageView) paramView.findViewById(this.valueViewID[0]));
            this.holder.sceneName = ((TextView)paramView.findViewById(this.valueViewID[1]));
            this.holder.sceneMore = ((ImageView) paramView.findViewById(this.valueViewID[2]));
            this.holder.listItem = ((RelativeLayout) paramView.findViewById(this.valueViewID[3]));
            paramView.setTag(this.holder);
            HashMap<String,String> localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String str1 = (String)((HashMap)localHashMap).get(this.keyString[1]);
                this.holder.sceneName.setText(str1);
                int resourceId = Integer.parseInt(localHashMap.get(keyString[0]));
                this.holder.sceneImg.setImageResource(mDC.mSceneTypeImages.getResourceId(resourceId,0));
                this.holder.listItem.setOnClickListener(new SceneModeItemListener(paramInt));
            }
            return paramView;
        }

        private class SceneModeViewHolder
        {
            TextView sceneName;
            ImageView sceneImg;
            ImageView sceneMore;
            RelativeLayout listItem;

            private SceneModeViewHolder() {}
        }

        /**
         * 情景列表的事件监听类
         */
        class SceneModeItemListener
                implements View.OnClickListener
        {
            private int position;

            SceneModeItemListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                Intent intent = new Intent(getActivity(),SceneInfo.class);
                intent.putExtra("scenePosition",position);
                startActivity(intent);
                saveposition=position;
            }
        }
    }
}
