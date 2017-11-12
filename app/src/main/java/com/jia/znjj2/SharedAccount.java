package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.data.DataControl;
import com.jia.util.CreateImage;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedAccount extends Activity {
    private DataControl mDC;
    private ListView lvSharedUser;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1038:
                    onResume();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_account);
        mDC = DataControl.getInstance();
        lvSharedUser = (ListView) findViewById(R.id.shared_account_list);
        updateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    public void updateListView()
    {
        int i = mDC.mSharedAccountList.size();
        ArrayList localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap localHashMap = new HashMap();
            localHashMap.put("account_name", mDC.mSharedAccountList.get(j).getAccountName());
            localHashMap.put("account_code", mDC.mSharedAccountList.get(j).getAccountCode());
            localList.add(localHashMap);
        }
        lvButtonAdapter adapter = new lvButtonAdapter(localList, this,
                new String[] {"account_name", "account_code","account_more"},
                new int[] {R.id.shared_account_item_name,R.id.shared_account_item_code, R.id.shared_account_item_more });
        lvSharedUser.setAdapter(adapter);
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
            paramView = this.mInflater.inflate(R.layout.shared_account_item, null);
            this.holder = new buttonViewHolder();
            this.holder.accountItem = (RelativeLayout) paramView.findViewById(R.id.shared_account_item_rl);
            this.holder.accountPhoto = (ImageView) paramView.findViewById(R.id.shared_account_item_photo);
            this.holder.accountName = (TextView)paramView.findViewById(R.id.shared_account_item_name);
            this.holder.accountCode = (TextView)paramView.findViewById(R.id.shared_account_item_code);
            this.holder.accountMore = (ImageView) paramView.findViewById(R.id.shared_account_item_more);
            paramView.setTag(this.holder);
            HashMap localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String name = (String)((HashMap)localHashMap).get(this.keyString[0]);
                String code = (String)((HashMap)localHashMap).get(this.keyString[1]);
                Bitmap bitmap = CreateImage.getLoacalBitmap(mDC.sUrlDir + code + ".jpg");
                this.holder.accountPhoto.setImageBitmap(bitmap);
                this.holder.accountName.setText(name);
                this.holder.accountCode.setText(code);
                this.holder.accountItem.setOnClickListener(new AccountItemListener(paramInt));
            }
            return paramView;
        }

        public void removeItem(int paramInt)
        {
            this.mAppList.remove(paramInt);
            notifyDataSetChanged();
        }

        private class buttonViewHolder
        {
            RelativeLayout accountItem;
            ImageView accountPhoto;
            TextView accountName;
            TextView accountCode;
            ImageView accountMore;

            private buttonViewHolder() {}
        }

        class AccountItemListener implements View.OnClickListener{
            private int position;
            public AccountItemListener(int paramInt){
                this.position = paramInt;
            }
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SharedAccount.this, SharedAccountInfo.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }

        /**
         * 用户user删除按钮的事件监听类
         */
        class AccountDeleteListener
                implements View.OnClickListener
        {
            private int position;

            AccountDeleteListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                new AlertDialog.Builder(SharedAccount.this).setTitle("删除 " + mDC.mUserList.get(position).getUserName() + " 账户" )
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                    {
                                        new Thread(){
                                            @Override
                                            public void run() {
//                                                mDC.mWS.deleteSharedUser(mDC.mSharedUserList.get(position).getMasterCode(),mDC.mSharedUserList.get(position).getAccountCode());
//                                                mDC.mSharedUserList.remove(position);
                                                Message message = new Message();
                                                message.what = 0x1038;
                                                handler.sendMessage(message);
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
                Intent intent= new Intent(SharedAccount.this,UserEditActivity.class);
                intent.putExtra("user_index", position);
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
                mDC.sMasterCode = mDC.mUserList.get(position).getMasterCode();
                mDC.mAreaData.loadAreaList();
            }
        }
    }

    public void sharedUserBack(View view){
        finish();
    }

}
