package com.jia.znjj2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.camera.business.Business;
import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.util.CreateImage;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Jia on 2016/3/29.
 */
public class LoginActivity extends Activity{
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final String TAG = "LoginActivity";
    private DataControl mDC;
    private ImageView mIvAccountPhoto;
    private ImageButton mIbAccountListIndicator;
    private ImageButton mIbAcccountDelete;
    private ListView mLvAccountList;
    private EditText mEtAccount;
    private EditText mEtPassword;
    private CheckBox mCbRemeberPassword;
    private TextView mTvForgetPassword;
    private ProgressDialog dialog;
    private LinearLayout ll_bt;

    String sMasterNodeWs="";
    String sAreaInfoTimeWs="";
    String sElectricInfoTimeWs="";
    private static final String lastAccount = "last_account";
    private Object[] accountArray;

    //两个Sharedpreference，一个用于存储上一个用户，一个yong
    SharedPreferences sp1;
    SharedPreferences sp2;
    SharedPreferences.Editor editor1;
    SharedPreferences.Editor editor2;
    private static boolean isVisible = false; //ListView 是否可见
    private static boolean isIndicatorUp = false;   //指示器方向
    public static int currentSelectPosition = -1;

    String[] from = {"accountPhoto","accountCode","deleteButton"};
    int[] to = {R.id.account_list_photo, R.id.account_list_account, R.id.account_list_delete};
    ArrayList<HashMap<String, Object>> list = new ArrayList<>();


    /*摄像头相关参数*/
    String url = "openapi.lechange.cn:443";
    String appid = "lce2ce9e43c32147a9";
    String appsecret = "b05c139f501149b09ecaaefcc12792";

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1001:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(LoginActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1002:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast toast = Toast.makeText(LoginActivity.this, "密码错误，请重新输入", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    break;
                case 0x1003:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                    dialog.show();
                    loginSuccess();
                    break;
                case 0x1004:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(LoginActivity.this, "不存在该用户，请检查后重试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    /**
     * 设置通知栏的颜色背景颜色，失败
     * @param on
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        Log.d(TAG, "onCreate: "+ "开始登陆页面");
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
        setContentView(R.layout.activity_login);

        StrictMode.setThreadPolicy(policy);
        init();
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    /**
     * 点击用户列表外的屏幕控件，让用户列表消失，并且更改用户的箭头的状态方向
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction()== MotionEvent.ACTION_DOWN && isVisible){
            int[] location=new int[2];
            //调用getLocationInWindow方法获得某一控件在窗口中左上角的横纵坐标
            mLvAccountList.getLocationInWindow(location);
            //获得在屏幕上点击的点的坐标
            int x=(int)event.getX();
            int y=(int)event.getY();
            if(x<location[0]|| x>location[0]+mLvAccountList.getWidth() ||
                    y<location[1]||y>location[1]+mLvAccountList.getHeight()){
                isIndicatorUp=false;
                isVisible=false;


                mIbAccountListIndicator.setBackgroundResource(R.drawable.indicator_down);
                mLvAccountList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！

            }


        }


        return super.onTouchEvent(event);
    }

    /**
     *初始化参数
     */
    public void init() {
        //弹出框相关设置
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在更新数据");

        mDC = DataControl.getInstance();
        mIvAccountPhoto = (ImageView) findViewById(R.id.login_account_img);
        mIbAccountListIndicator = (ImageButton) findViewById(R.id.login_account_list_indicator);
        mIbAcccountDelete = (ImageButton) findViewById(R.id.login_account_delete);
        mLvAccountList = (ListView) findViewById(R.id.login_account_list);
        mEtAccount = (EditText) findViewById(R.id.login_account);
        mEtPassword = (EditText) findViewById(R.id.login_password);
        mCbRemeberPassword = (CheckBox) findViewById(R.id.login_remember_password);
        mTvForgetPassword= (TextView) findViewById(R.id.login_forget_password);
        ll_bt= (LinearLayout) findViewById(R.id.ll_bt);

        ll_bt.setVisibility(View.VISIBLE);
        sp1 = getSharedPreferences("zfzn_account", MODE_PRIVATE);
        sp2 = getSharedPreferences("zfzn_last_account", MODE_PRIVATE);
        editor1 = sp1.edit();
        editor2 = sp2.edit();

        //在用户编辑窗口填入上次使用的账户
        String string = sp2.getString(lastAccount, "");
        mEtAccount.setText(string);
        mEtPassword.setText(sp1.getString(string,""));
        accountArray = sp1.getAll().keySet().toArray();

        list.clear();
        for (int i = 0; i< accountArray.length; i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("accountCode", accountArray[i]);
            list.add(map);
        }
        MyLoginListAdapter adapter=new MyLoginListAdapter(this, list, R.layout.account_list, from, to);
        mLvAccountList.setAdapter(adapter);
//        mEtAccount.setText("15109833919");
//        mEtPassword.setText("123456");



    }

    private void initCarema(){
        Business.getInstance().init(appid,appsecret,url);

        Business.getInstance().userlogin(mDC.sLePhoneNumber,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(0 == msg.what){
                    //该手机号与账户已经绑定
                    userlogin(mDC.sLePhoneNumber);
                }else {
                    //该手机号与账户没有绑定
                }
            }
        });
//        if(mDC.mAccount.getLeSign() == 1){
//            userlogin(mDC.sLePhoneNumber);
//        }
    }

    public void addListener() {
        mEtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEtAccount.getText().toString().equals("") == false){
                    mIbAcccountDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        mIbAcccountDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAccount.setText("");
                mEtPassword.setText("");
                currentSelectPosition = -1;
                mIbAcccountDelete.setVisibility(View.GONE);
            }
        });
        mLvAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                mEtAccount.setText((String)list.get(arg2).get(from[1]));
                mEtPassword.setText(sp1.getString(mEtAccount.getText().toString(),null));
                currentSelectPosition=arg2;

                //相应完点击后List就消失，指示箭头反向！
                mLvAccountList.setVisibility(View.GONE);
                ll_bt.setVisibility(View.VISIBLE);
                mIbAccountListIndicator.setBackgroundResource(R.drawable.indicator_down);

                System.out.println("---------Selected!!");

            }
        });

        mIbAccountListIndicator.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isIndicatorUp){
                    isIndicatorUp=false;
                    isVisible=false;
                    mIbAccountListIndicator.setBackgroundResource(R.drawable.indicator_down);
                    mLvAccountList.setVisibility(View.GONE);   //让ListView列表消失
                    ll_bt.setVisibility(View.VISIBLE);

                }
                else{
                    isIndicatorUp=true;
                    isVisible=true;
                    mIbAccountListIndicator.setBackgroundResource(R.drawable.indicator_up);
                    mLvAccountList.setVisibility(View.VISIBLE);
                    ll_bt.setVisibility(View.VISIBLE);
                }
            }

        });
        mTvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(forIntent);
            }
        });
    }


    /**
     * 登录账户按钮的响应函数
     * @param v
     */
    public void onBtnLogin(View v)
    {
        dialog.show();
        //获取用户信息
        final String accountCode = mEtAccount.getText().toString();
        final String password = mEtPassword.getText().toString();
        //比较用户输入的用户名和密码VS正确的用户名和密码，网络连接验证，耗时操作，需要在新线程中进行
        new Thread() {
            @Override
            public void run() {
                /**
                 * -1: 远程连接失败
                 * 0: 存在该用户名的用户，但密码不正确
                 * 1：存在该用户，且密码正确
                 * 2：不存在该用户名的用户
                 */
                String loginResult = mDC.mWS.checkUserPassword(accountCode, password);
                System.out.println("************loginResult:"+loginResult);
                String[] loginResults = loginResult.split("\\|"); //"1|BA110102|20161231235959|20161212235959"
                int flag = Integer.parseInt(loginResults[0]);
                //
                if(loginResults.length>1)
                {
                    sMasterNodeWs = loginResults[1];
                    sAreaInfoTimeWs = loginResults[2];
                    sElectricInfoTimeWs = loginResults[3];
                }
                mDC.sMasterCode = sMasterNodeWs;    //保存用户的主节点编号
                Message msg = new Message();
                if (flag == -1) {       //远程连接失败
                    msg.what = 0x1001;
                } else if (flag == 0) {     //存在该用户名的用户，但密码不正确
                    msg.what = 0x1002;
                } else if (flag == 1) {   //存在该用户，且密码正确
                    msg.what = 0x1003;
                } else if (flag == 2) {
                    msg.what = 0x1004;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 登录成功:更新数据
     */
    public void loginSuccess()
    {

        //存储账户名字
        mDC.sAccountCode = mEtAccount.getText().toString();
        mDC.sUrlDir = CreateImage.URL + mDC.sAccountCode + "/";
        //根据用户是否选择记住密码来更新数据
        if(mCbRemeberPassword.isChecked()){
            String password = mEtPassword.getText().toString();
            editor1.putString(mDC.sAccountCode, password);
            editor1.commit();
        }else {
            editor1.putString(mDC.sAccountCode, "");
            editor1.commit();
        }
        editor2.putString(lastAccount, mDC.sAccountCode);
        editor2.commit();

          new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Cursor cursor = mDC.mDB.queryAccountByAccountCode(mDC.sAccountCode);    //读取账户的本地信息
                if(cursor.getCount() == 0){     //如果cursor为空，则本地没有该账号的数据，需从服务器读取
                    int result = mDC.mAccountData.addAccount(mDC.sAccountCode);
                    if(result == -1){
                        Log.d(TAG, "run() 插入账户返回值: " + result);
                    }
                    mDC.mWS.loadAccountFromWs(mDC.sAccountCode,null);
                    mDC.mWS.loadUserFromWs(mDC.sAccountCode,null);
                }else {     //如果cursor不为空，第一个为上一次连接的用户，则本地有该账号的数据，根据时间判断是否读取数据
                    cursor.moveToPosition(0);
                    mDC.mAccount.setAccountCode(cursor.getString(cursor.getColumnIndex("account_code")));
                    mDC.mAccount.setAccountName(cursor.getString(cursor.getColumnIndex("account_name")));
                    mDC.mAccount.setAccountPhone(cursor.getString(cursor.getColumnIndex("account_phone")));
                    mDC.mAccount.setAccountAddress(cursor.getString(cursor.getColumnIndex("account_address")));
                    mDC.mAccount.setAccountEmail(cursor.getString(cursor.getColumnIndex("account_email")));
                    mDC.mAccount.setLePhone(cursor.getString(cursor.getColumnIndex("le_phone")));
                    mDC.mAccount.setLeSign(cursor.getInt(cursor.getColumnIndex("le_sign")));
                    String accountTime = cursor.getString(cursor.getColumnIndex("account_time"));
                    String userTime = cursor.getString(cursor.getColumnIndex("user_time"));
                    mDC.mWS.loadAccountFromWs(mDC.sAccountCode,accountTime);
                    mDC.mWS.loadUserFromWs(mDC.sAccountCode,userTime);
                }
                //账户数据导入完毕



                //将本地数据库中该账户下的全部user导入到内存设备中
                mDC.mUserData.loadUserList();


                if(mDC.mUserList.size() != 0){  //该账号下有用户user
                    mDC.sMasterCode=mDC.mUserList.get(0).getMasterCode();
                    mDC.sUserIP = mDC.mUserList.get(0).getUserIP();
                    mDC.mWST.ConnectToWebSocket(mDC.sMasterCode);
                    String str = "";
                    str = (new MasterSocket()).getMasterNodeCode();
                    //str = "#AA00BB00";   //模拟搜索到主节点
                    if(str != null && !str.equals("")){
                        str = str.substring(1,9);
                        //mDC.sMasterCode = str;//待定
                    }else{
                        System.out.println("搜索主节点失败");
                    }
                    if(str.equals(mDC.sMasterCode)){
                        mDC.bIsRemote = false;
                    }else {
                        mDC.bIsRemote = true;
                    }

                    mDC.mWS.loadUserRoomFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getAreaTime());
                    mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),LoginActivity.this);

                    mDC.mWS.loadSceneFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getSceneTime());
                    mDC.mWS.loadSceneElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getSceneElectricTime());

                    mDC.mAreaData.loadAreaList();
                    mDC.mSceneData.loadSceneList();

                    initLeChengPhoneNumber();
                    if(mDC.sLePhoneNumber == null){
                        mDC.sLePhoneNumber = mDC.mAccount.getLePhone();
                    }
                    initCarema();
                    goToMainPage();
                }else {     //该账号下没有用户user
                    goToAddUser();
                }
                Looper.loop();
            }
        }.start();

    }

    private void initLeChengPhoneNumber(){
        for (ElectricInfoData electric : mDC.mElectricList) {
            if(electric.getElectricType() == 8){
                mDC.sLePhoneNumber = electric.getExtras();
            }
        }
    }


    private void goToAddUser(){
        if(dialog.isShowing()) {
            dialog.cancel();
        }
        Intent intent = new Intent(LoginActivity.this, UserAddActivity.class);
        startActivity(intent);
    }


    private void goToMainPage(){
        //导入用户数据的区域及电器数据
        //mDC.mAreaData.loadAreaList();
        if(dialog.isShowing()) {
            dialog.cancel();
        }
        Intent ourIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(ourIntent);
        finish();
    }
    public void onBtnSign(View v) {
        Intent intent = new Intent(LoginActivity.this, SignActivity.class);
        startActivity(intent);
    }

    public void onBtnConfWifi(View view){
        Intent intent = new Intent(LoginActivity.this, ConfWifi.class);
        startActivity(intent);
    }
    public void onBtnConfWifiSpeak(View view){
        Intent intent = new Intent(LoginActivity.this, ConfWifiSpeak.class);
        startActivity(intent);
    }
    /**
     * 描述：用户登陆函数
     * @return_type：void
     */
    public static void userlogin(String phoneNumber) {
        System.out.println("摄像头模&&&&&&&&&&&&&&");
        Business.getInstance().userlogin(phoneNumber, new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if (0 == msg.what){
                    //startBindUserActivity();
                    //摄像头模块登录成功
                    System.out.println("摄像头模块登录成功");
                    String userToken = (String) msg.obj;
                    Business.getInstance().setToken(userToken);
                }

                else {
                    String result = (String) msg.obj;
                    //摄像头模块登录失败
                    System.out.println("摄像头模块登录失败");
                }

            }

        });
    }

    public class MyLoginListAdapter extends BaseAdapter {

        protected Context context;
        protected ArrayList<HashMap<String,Object>> list;
        protected int itemLayout;
        protected String[] from;
        protected int[] to;

        public MyLoginListAdapter(Context context,
                                  ArrayList<HashMap<String, Object>> list, int itemLayout,
                                  String[] from, int[] to) {
            super();
            this.context = context;
            this.list = list;
            this.itemLayout = itemLayout;
            this.from = from;
            this.to = to;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder{
            public ImageView accountPhoto;
            public TextView accountCode;
            public ImageButton deleteButton;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder=null;
            /*
            currentPosition=position;
            不能使用currentPosition，因为每绘制完一个Item就会更新currentPosition
            这样得到的currentPosition将始终是最后一个Item的position
            */

            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(itemLayout, null);
                holder=new ViewHolder();
                holder.accountPhoto=(ImageView)convertView.findViewById(to[0]);
                holder.accountCode=(TextView)convertView.findViewById(to[1]);
                holder.deleteButton=(ImageButton)convertView.findViewById(to[2]);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder)convertView.getTag();
            }

            //holder.accountPhoto.setBackgroundResource((Integer)list.get(position).get(from[0]));
            holder.accountCode.setText((String)list.get(position).get(from[1]));
            //holder.deleteButton.setBackgroundResource((Integer)list.get(position).get(from[2]));
            holder.deleteButton.setOnClickListener(new ListOnClickListener(position));

            return convertView;
        }

        class ListOnClickListener implements View.OnClickListener {

            private int position;


            public ListOnClickListener(int position) {
                super();
                this.position = position;
            }

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(list.get(position).get("accountCode").equals(mEtAccount.getText().toString())){
                    mEtAccount.setText("");
                    mEtPassword.setText("");
                }
                if(list.get(position).get("accountCode").equals(sp2.getString(lastAccount,""))){
                    editor2.remove(list.get(position).get("accountCode").toString());
                    editor2.commit();
                }
                editor1.remove(list.get(position).get("accountCode").toString());
                editor1.commit();
                list.remove(position);

                //如果删除的就是当前显示的账号，那么将主界面当前显示的头像设置回初始头像
                if(position==currentSelectPosition){
                    mIvAccountPhoto.setImageResource(R.drawable.logo);
                    mEtAccount.setText("");
                    currentSelectPosition=-1;
                }
                else if(position<currentSelectPosition){
                    currentSelectPosition--;    //这里小于当前选择的position时需要进行减1操作
                }

                mIbAccountListIndicator.setBackgroundResource(R.drawable.indicator_down);
                mLvAccountList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！
                ll_bt.setVisibility(View.VISIBLE);
                MyLoginListAdapter.this.notifyDataSetChanged();
            }
        }
    }


}
