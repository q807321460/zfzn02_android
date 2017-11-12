package com.jia.znjj2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.connection.ServiceSocket;
import com.jia.data.DataControl;
import com.jia.util.NetworkUtil;
import com.jia.widget.SlidingMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * Created by Administrator on 2016/10/17.
 */
public class MainPageActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MainPageActivity";
    private SlidingMenu mLeftMenu ;

    private LinearLayout bottom_ll_main;
    private LinearLayout bottom_ll_area;
    private LinearLayout bottom_ll_scene;
    private LinearLayout bottom_ll_security;
    private TextView mTvMain;


    private ImageView ivTitleUser;
    private TextView tvTitleText;
    private ImageView ivTitleAlarm;
    private ImageView ivTitleAdd;
    private PopupMenu popup;
    private Intent intent;
    private BrdcstReceiver receiver;
    private DataControl mDC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage_test);
        mDC = DataControl.getInstance();
        init();
        connectToServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void init(){
        mLeftMenu = (SlidingMenu) findViewById(R.id.mainpage_slide_menu);

        bottom_ll_main = (LinearLayout) findViewById(R.id.bottom_ll_main);
        bottom_ll_area = (LinearLayout) findViewById(R.id.bottom_ll_area);
        bottom_ll_scene = (LinearLayout) findViewById(R.id.bottom_ll_scene);
        bottom_ll_security = (LinearLayout) findViewById(R.id.bottom_ll_security);
        mTvMain = (TextView) findViewById(R.id.bottom_tv_main);
        mTvMain.setTextColor(getResources().getColor(R.color.text_color_select));
        bottom_ll_main.setOnClickListener(this);
        bottom_ll_area.setOnClickListener(this);
        bottom_ll_scene.setOnClickListener(this);
        bottom_ll_security.setOnClickListener(this);


        ivTitleUser = (ImageView) findViewById(R.id.mainpage_title_user);
        ivTitleAlarm = (ImageView) findViewById(R.id.mainpage_title_alarm);
        ivTitleAdd = (ImageView) findViewById(R.id.mainpage_title_add);
        tvTitleText = (TextView) findViewById(R.id.mainpage_title_text);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bottom_ll_area:
                intent = new Intent(MainPageActivity.this, AreaActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_scene:
                intent = new Intent(MainPageActivity.this, SenceActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_security:
                intent = new Intent(MainPageActivity.this, SecurityActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 连接服务器，开启一个多线程，避免卡死页面
     */
    private void connectToServer() {
        new Thread(){
            @Override
            public void run() {
                /*连接主节点*/

                if(!mDC.bIsRemote) {
                    try {
                        int port = mDC.iUserPort;
                        Socket socket = new Socket(mDC.sUserIP, port);
                        /**
                         * 设置网络，输入流，输出流
                         */
                        NetworkUtil.socket = socket;
                        NetworkUtil.out = new PrintWriter(socket.getOutputStream(), true);
                        NetworkUtil.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*启动后台Service服务，接受网络数据*/
                MainPageActivity.this.startService(new Intent(MainPageActivity.this, ServiceSocket.class));

                /*设置接收后台的广播消息*/
                receiver = new BrdcstReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.MY_RECEIVER");
                registerReceiver(receiver, filter);

            }
        }.start();
    }
    public void titleWarnorder(View view){
        Intent intent =new Intent(MainPageActivity.this,Warnifo.class);
        startActivity(intent);
    }
    public void toggleMenu(View view){
        mLeftMenu.toggle();
    }

    public void onPopupButtonClick(View button)
    {
        // 创建PopupMenu对象
        popup = new PopupMenu(this, button);
        // 将R.menu.popup_menu菜单资源加载到popup菜单中
        getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
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
                                intent = new Intent(MainPageActivity.this, AreaAdd.class);
                                startActivity(intent);
                                break;
                            case R.id.add_scene:
                                intent = new Intent(MainPageActivity.this, SceneAdd.class);
                                startActivity(intent);
                                break;
                            case R.id.add_electric:
                                if(mDC.mAreaList.size() ==0){
                                    Toast.makeText(MainPageActivity.this,"请先添加区域",Toast.LENGTH_LONG).show();
                                    break;
                                }
                                intent = new Intent(MainPageActivity.this, ElectricAdd.class);
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

    /**
     *广播：接受后台的service发送的广播
     */
    private  class BrdcstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    }
    private void updateUI(){
        System.out.println("MainaPageActivity updateUI");
    }
}
