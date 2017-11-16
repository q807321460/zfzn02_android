package com.jia.znjj2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.jdplay.JdDeviceListActivity;
import com.jia.update.UpdateService;
import com.jia.util.CreateImage;
import com.jia.util.Util;
import com.jia.widget.SlidingMenu;

import java.io.File;

/**
 * Created by Administrator on 2016/11/11.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private DataControl mDC;
    private SlidingMenu mLeftMenu ;
    private View view;
    private LayoutInflater mInflater;
    private ImageView ivTitleUser;
    private TextView tvTitleText;
    private ImageView ivTitleAlarm;
    private ImageView ivTitleAdd;
    private GridView gvScene;
    private GridView gvRoom;

    private ImageView ivPerson;
    private TextView tvAccountName;
    private TextView tvAccountCode;
    private RelativeLayout rlMyMaster;
    private RelativeLayout rlJdPlay;
    private RelativeLayout rlMyElectric;
    private RelativeLayout rlMyArea;
    private RelativeLayout rlAlarmPhone;
    private RelativeLayout rlRefreshData;
    private RelativeLayout rlHelp;
    private RelativeLayout rlSetting;
    private RelativeLayout rlVersion;
    private Intent intent;
    private PopupMenu popup;
    private String appVersion;
    public static String warnInfo;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1110:
                    Toast.makeText(getContext(), "检查更新失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1111:
                    Toast.makeText(getContext(), "检查更新失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1112:
                    Toast.makeText(getContext(), "检查更新成功", Toast.LENGTH_LONG).show();
                    if(!appVersion.equals(Util.getVersionName(getContext()))){
                        updateAppVersion();
                    }else {
                        isTheNewestVersion();
                    }
                    break;
                case 0x1113:
                    Toast.makeText(getContext(), "无报警信息", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        view = mInflater.inflate(R.layout.mainpage_test, container, false);
        initView();
        updateGridView();
        addListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("HomeFragment onResume()");
        initView();
        updateGridView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        mLeftMenu = (SlidingMenu) view.findViewById(R.id.mainpage_slide_menu);

        ivTitleUser = (ImageView) view.findViewById(R.id.mainpage_title_user);
        ivTitleAlarm = (ImageView) view.findViewById(R.id.mainpage_title_alarm);
        ivTitleAdd = (ImageView) view.findViewById(R.id.mainpage_title_add);
        tvTitleText = (TextView) view.findViewById(R.id.mainpage_title_text);
        gvScene = (GridView) view.findViewById(R.id.mainpage_scene_gv);
        gvRoom = (GridView) view.findViewById(R.id.mainpage_room_gv);
        ivPerson = (ImageView) view.findViewById(R.id.left_menu_person);
        tvAccountName = (TextView) view.findViewById(R.id.left_menu_account_name);
        tvAccountCode = (TextView) view.findViewById(R.id.left_menu_account_code);
        rlMyMaster = (RelativeLayout) view.findViewById(R.id.left_menu_master_rl);
        rlJdPlay = (RelativeLayout) view.findViewById(R.id.left_menu_jdplay_rl);
        rlMyElectric = (RelativeLayout) view.findViewById(R.id.left_menu_electric_rl);
        rlMyArea = (RelativeLayout) view.findViewById(R.id.left_menu_area_rl);
        rlAlarmPhone= (RelativeLayout) view.findViewById(R.id.left_menu_alarm_rl);
        rlRefreshData = (RelativeLayout) view.findViewById(R.id.left_menu_refresh_rl);
        rlHelp = (RelativeLayout) view.findViewById(R.id.left_menu_help_rl);
        rlSetting = (RelativeLayout) view.findViewById(R.id.left_menu_set_rl);
        rlVersion = (RelativeLayout) view.findViewById(R.id.left_menu_version_rl);


        setTitleText();


        Bitmap bitmap = CreateImage.getLoacalBitmap(mDC.sUrlDir + mDC.sAccountCode+".jpg");
        ivTitleUser.setImageBitmap(bitmap);
        ivPerson.setImageBitmap(bitmap);
//        if(mDC.mAccount.getAccountName()!=null){
//        tvAccountName.setText(mDC.mAccount.getAccountName());}
//        if(mDC.mAccount.getAccountCode()!=null){
//            tvAccountCode.setText(mDC.mAccount.getAccountCode());
//        }

    }

    public void setTitleText(){
        if(mDC.bIsRemote){
            tvTitleText.setText("-远程");
        }else {
            tvTitleText.setText("-本地");
        }
    }

    public void updateGridView() {
        BaseAdapter adapter1 = new BaseAdapter() {
            @Override
            public int getCount() {
                return (mDC.mSceneList.size()>=4) ? 4 : mDC.mSceneList.size();
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
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.cell_electric_type, null, false);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.electric_add_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.electric_add_name);


                DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = (int)(loacalDisplayMetrics.heightPixels/loacalDisplayMetrics.density);
                viewHolder.imageView.setBackgroundResource(getResources().obtainTypedArray(R.array.scene_type_images)
                        .getResourceId(mDC.mSceneList.get(position).getSceneImg(),0));
                viewHolder.textView.setText(mDC.mSceneList.get(position).getSceneName());
                viewHolder.imageView.setLayoutParams(new LinearLayout.LayoutParams(i / 6, i / 6));
                if (i <= 800)
                {
                    viewHolder.textView.setTextSize(18.0F);
                }
                else
                {
                    viewHolder.textView.setTextSize(22.0F);
                }

                return convertView;
            }
        };

        BaseAdapter adapter2 = new BaseAdapter() {
            @Override
            public int getCount() {
                return (mDC.mAreaList.size()>=4) ? 4 : mDC.mAreaList.size();
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
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.cell_electric_type, null, false);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.electric_add_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.electric_add_name);


                DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = (int)(loacalDisplayMetrics.heightPixels/loacalDisplayMetrics.density);
                viewHolder.imageView.setBackgroundResource(getResources().obtainTypedArray(R.array.area_type_press_images)
                        .getResourceId(mDC.mAreaList.get(position).getRoomImg(),0));
                viewHolder.textView.setText(mDC.mAreaList.get(position).getRoomName());
                viewHolder.imageView.setLayoutParams(new LinearLayout.LayoutParams(i / 6, i / 6));
                if (i <= 800)
                {
                    viewHolder.textView.setTextSize(18.0F);
                }
                else
                {
                    viewHolder.textView.setTextSize(22.0F);
                }

                return convertView;
            }
        };
        gvScene.setAdapter(adapter1);
        gvRoom.setAdapter(adapter2);
    }
    public void addListener(){
        ivTitleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu(v);
            }
        });
        rlMyMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMyMasterNode(v);
            }
        });
        rlJdPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JdDeviceListActivity.class);
                startActivity(intent);
            }
        });
        rlVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        appVersion = mDC.mWS.getAppVersion();
                        Message msg = new Message();
                        if(appVersion.startsWith("-1")){
                            msg.what = 0x1110;
                        }else if(appVersion.startsWith("-2")){
                            msg.what = 0x1111;
                        }else {
                            msg.what = 0x1112;
                        }
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeAccount.class);
                startActivity(intent);

            }
        });
        ivPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personAccount(v);
            }
        });
        ivTitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupButtonClick(v);
            }
        });
        ivTitleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleWarnorder(v);
            }
        });
        gvScene.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),SceneInfo.class);
                intent.putExtra("scenePosition",position);
                startActivity(intent);
            }
        });

        gvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setCurrentAreaPage(position);
                ((MainActivity)getActivity()).resetImg();
                ((MainActivity)getActivity()).setSelect(1);
            }
        });
    }
    public void titleWarnorder(View view){
        new Thread(){
            public void run(){
                warnInfo = mDC.mWS.loadAlarmRecord(mDC.sMasterCode);
                System.out.print(warnInfo);
                Message msg = new Message();
                if(warnInfo.equals("[]")){
                    msg.what=0x1113;
                }else{
                    Intent intent= new Intent(getContext(),Warnifo.class);
                    startActivity(intent);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    public void onPopupButtonClick(View button)
    {
        // 创建PopupMenu对象
        popup = new PopupMenu(getContext(), button);
        System.out.println("%%%%%%%%"+getContext());
        // 将R.menu.popup_menu菜单资源加载到popup菜单中
        getActivity().getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        // 为popup菜单的菜单项单击事件绑定事件监听器
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.mainpage_title_add:
                                // 隐藏该对话框
                                popup.dismiss();
                                break;
                            case R.id.add_area:
                                if(mDC.mUserList.get(0).getIsAdmin() != 1){
                                    Toast.makeText(getContext(),"非管理员，不能添加区域",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                intent = new Intent(getContext(), AreaAdd.class);
                                startActivity(intent);
                                break;
                            case R.id.add_scene:
                                if(mDC.mUserList.get(0).getIsAdmin() != 1){
                                    Toast.makeText(getContext(),"非管理员，不能添加情景",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                intent = new Intent(getContext(), SceneAdd.class);
                                startActivity(intent);
                                break;
                            case R.id.add_electric:
                                if(mDC.mAreaList.size() ==0){
                                    Toast.makeText(getContext(),"请先添加区域",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                if(mDC.bIsRemote){
                                    Toast.makeText(getContext(),"远程不能添加电器",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                if(mDC.mUserList.get(0).getIsAdmin() != 1){
                                    Toast.makeText(getContext(),"非管理员，不能添加电器",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                intent = new Intent(getContext(), ElectricAdd.class);
                                startActivity(intent);
                                break;
                            default:
                                popup.dismiss();
                                break;
                        }
                        return true;
                    }
                });
        popup.show();
    }

    public void toggleMenu(View view){
        mLeftMenu.toggle();
    }

    public void personAccount(View view){
        Intent intent = new Intent(getContext(),PersonAccount.class);
        startActivity(intent);
    }

    public void toMyMasterNode(View view){
        Intent intent = new Intent(getContext(),MyMasterNode.class);
        startActivity(intent);
        Log.d(TAG, "toMyMasterNode() called with: " + "view = [" + view + "]");
    }

    public void toMyArea(View view){
        Log.d(TAG, "toMyArea() called with: " + "view = [" + view + "]");
    }

    public void toMyElectric(View view){
        Log.d(TAG, "toMyElectric() called with: " + "view = [" + view + "]");
    }

    public void toMyAlarmPhone(View view){
        Log.d(TAG, "toMyAlarmPhone() called with: " + "view = [" + view + "]");
    }

    public void helpAndFeedback(View view){
        Log.d(TAG, "helpAndFeedback() called with: " + "view = [" + view + "]");
    }

    public void refreshData(View view){
        Log.d(TAG, "refreshData() called with: " + "view = [" + view + "]");
    }



    private void updateAppVersion(){
        File sd = Environment.getExternalStorageDirectory();
        System.out.println("SD权限："+ sd.canWrite());
        new AlertDialog.Builder(getContext()).setTitle("更新版本:")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                            {
                                Intent intent = new Intent(getContext(), UpdateService.class);
                                intent.putExtra("lastVersion", appVersion);
                                getActivity().startService(intent);

                            }
                        })
                .setNegativeButton("取消", null)
                .setMessage("当前版本："+Util.getVersionName(getContext())+"\n更新版本：" + appVersion).show();
    }

    private void isTheNewestVersion(){
        new AlertDialog.Builder(getContext()).setTitle("已经是最新版本")
                .setPositiveButton("确定",null)
                .setMessage("当前版本："+Util.getVersionName(getContext()))
                .show();
    }



    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
