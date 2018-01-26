package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MyMasterNode extends Activity {
    private ListView lvMasterNode;
    private ProgressDialog dialog;
    private DataControl mDC;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1120:
                    Toast.makeText(MyMasterNode.this, "删除失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1121:
                    Toast.makeText(MyMasterNode.this, "删除失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1122:
                    Toast.makeText(MyMasterNode.this, "删除成功", Toast.LENGTH_LONG).show();
                    updateListView();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_master_node);
        initView();
        updateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        lvMasterNode = (ListView) findViewById(R.id.my_master_node_list);
    }

    public void updateListView()
    {
        int i = mDC.mUserList.size();
        ArrayList localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap localHashMap = new HashMap();
            localHashMap.put("user_name", mDC.mUserList.get(j).getUserName());
            localList.add(localHashMap);
        }
        lvButtonAdapter adapter = new lvButtonAdapter(localList, this,
                new String[] { "user_name", "connect", "user_edit", "user_delete", "user_ll" },
                new int[] {R.id.master_node_item_name, R.id.master_node_item_connect, R.id.master_node_item_edit,
                        R.id.master_node_item_delete, R.id.master_node_item_ll });
        lvMasterNode.setAdapter(adapter);
    }

    public class lvButtonAdapter extends BaseAdapter
    {
        private buttonViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, Object>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public lvButtonAdapter(ArrayList<HashMap<String, Object>> paramArrayList, Context context,
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
            paramView = this.mInflater.inflate(R.layout.master_node_list_item, null);
            this.holder = new buttonViewHolder();
            this.holder.userName = ((TextView)paramView.findViewById(this.valueViewID[0]));
            this.holder.userConnect = ((TextView)paramView.findViewById(this.valueViewID[1]));
            this.holder.userEdit = ((ImageView) paramView.findViewById(this.valueViewID[2]));
            this.holder.userDelete = ((ImageView) paramView.findViewById(this.valueViewID[3]));
            this.holder.listItem = ((RelativeLayout)paramView.findViewById(this.valueViewID[4]));
            paramView.setTag(this.holder);
            HashMap localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String str1 = (String)((HashMap)localHashMap).get(this.keyString[0]);
                this.holder.userName.setText(str1);
                if (paramInt == 0) {
                    this.holder.userConnect.setText("已连接");
                } else {
                    this.holder.userConnect.setText("");
                }

                this.holder.userEdit.setOnClickListener(new UserEditListener(paramInt));
                this.holder.userDelete.setOnClickListener(new UserDeleteListener(paramInt));
                this.holder.listItem.setOnClickListener(new MasterNodeItemListener(paramInt));
            }
            return paramView;
        }


        private class buttonViewHolder
        {
            TextView userName;
            TextView userConnect;
            ImageView userEdit;
            ImageView userDelete;
            RelativeLayout listItem;

            private buttonViewHolder() {}
        }

        /**
         * 账户删除按钮的事件监听类
         */
        class UserDeleteListener
                implements View.OnClickListener
        {
            private int position;

            UserDeleteListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                new AlertDialog.Builder(MyMasterNode.this).setTitle("删除 " + mDC.mUserList.get(position).getUserName() + " 账户" )
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                    {
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                String accountCode = mDC.mUserList.get(position).getAccountCode();
                                                String masterCode = mDC.mUserList.get(position).getMasterCode();
                                                String result = mDC.mWS.deleteUser(accountCode, masterCode);
                                                Message msg = new Message();
                                                if(result.startsWith("-2")){
                                                    msg.what = 0x1120;
                                                }else if(result.startsWith("-1")){
                                                    msg.what = 0x1121;
                                                }else {
                                                    msg.what = 0x1122;
                                                    mDC.mUserData.deleteUser(position);

                                                }
                                                handler.sendMessage(msg);

                                            }
                                        }.start();

                                    }
                                })
                        .setNegativeButton("取消", null).show();
            }
        }

        /**
         * 编辑用户user的事件监听类
         */
        class UserEditListener
                implements View.OnClickListener
        {
            private int position;

            UserEditListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                Intent intent= new Intent(MyMasterNode.this,UserEditActivity.class);
                intent.putExtra("user_sequ", position);
                startActivity(intent);
            }
        }

        /**
         * 用户user列表的事件监听类
         */
        class MasterNodeItemListener
                implements View.OnClickListener
        {
            private int position;

            MasterNodeItemListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                new ChangeUserAsynTask().execute(position);

            }
        }
    }

    public void myMasterNodeBack(View view){
        finish();
    }

    public void addMasterNode(View view){
        Intent intent = new Intent(MyMasterNode.this, UserAddActivity.class);
        startActivity(intent);
    }

    private class ChangeUserAsynTask extends AsyncTask<Integer,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MyMasterNode.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("提示");
            dialog.setMessage("正在切换账户数据");
            dialog.show();
            mDC.mWST.CloseWebsocket();

        }

        @Override
        protected Void doInBackground(Integer... params) {
            //排序
            mDC.sMasterCode = mDC.mUserList.get(params[0]).getMasterCode();
            mDC.mUserData.updateUserSequ(mDC.sMasterCode);
            mDC.mUserData.loadUserList();
            //mDC.mAreaData.loadAreaList();
            mDC.sMasterCode=mDC.mUserList.get(0).getMasterCode();
            mDC.sUserIP = mDC.mUserList.get(0).getUserIP();
            mDC.mWST.ConnectToWebSocket(mDC.sMasterCode);

            String str = "";
            str = (new MasterSocket()).getMasterNodeCode();
            //str = "#AA00BB00";   //模拟搜索到主节点
            if(str != null && !str.equals("") && str.length()>=9){
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
            mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),MyMasterNode.this);
            mDC.mWS.loadSceneFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getSceneTime());
            mDC.mWS.loadSceneElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getSceneElectricTime());
            //mDC.mWS.getElectricStateByUser(mDC.sAccountCode, mDC.sMasterCode);//11.1添加
            mDC.mAreaData.loadAreaList();
            mDC.mSceneData.loadSceneList();

            if(mDC.sLePhoneNumber == null){
                mDC.sLePhoneNumber = mDC.mAccount.getLePhone();
            }
            goToMainPage();//2017.11.02
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            Toast.makeText(MyMasterNode.this, "切换完成", Toast.LENGTH_LONG).show();
            onResume();
        }
    }
    private void goToMainPage(){
        //导入用户数据的区域及电器数据
        //mDC.mAreaData.loadAreaList();
        if(dialog.isShowing()) {
            dialog.cancel();
        }
        Intent ourIntent = new Intent(MyMasterNode.this, MainActivity.class);
        startActivity(ourIntent);
        finish();
    }
}
